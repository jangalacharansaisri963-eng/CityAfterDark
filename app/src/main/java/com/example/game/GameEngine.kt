package com.example.game

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.engine.ecs.DistrictComponent
import com.example.engine.ecs.EntityManager
import com.example.engine.ecs.InteractiveComponent
import com.example.engine.ecs.InteractionType
import com.example.engine.ecs.TransformComponent
import com.example.engine.ecs.VehicleComponent
import com.example.engine.math.Vector3
import com.example.game.audio.GameAudioEngine
import com.example.game.camera.ThirdPersonCamera
import com.example.game.character.Outfit
import com.example.game.character.Player
import com.example.game.data.GameDatabase
import com.example.game.data.GameSaveEntity
import com.example.game.missions.DialogueLine
import com.example.game.missions.MissionManager
import com.example.game.renderer.GameRenderer
import com.example.game.simulation.DayNightSystem
import com.example.game.simulation.PedestrianSystem
import com.example.game.simulation.TrafficSystem
import com.example.game.simulation.WeatherType
import com.example.game.vehicle.VehiclePhysics
import com.example.game.vehicle.VehicleType
import com.example.game.world.CityBuilder
import com.example.game.world.CityData
import com.example.game.world.District
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameEngine(private val context: Context) {
    val entityManager = EntityManager()
    val player = Player()
    val camera = ThirdPersonCamera()
    val dayNightSystem = DayNightSystem()
    val missionManager = MissionManager()
    val audioEngine = GameAudioEngine()

    lateinit var cityData: CityData
    lateinit var trafficSystem: TrafficSystem
    lateinit var pedestrianSystem: PedestrianSystem
    lateinit var renderer: GameRenderer

    private val db = GameDatabase.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    // Observable Game State for UI
    var currentDistrict by mutableStateOf(District.DOWNTOWN)
    var interactionPrompt by mutableStateOf<String?>(null)
    private var nearbyInteractiveTarget: InteractiveComponent? = null

    var activeDialogue by mutableStateOf<DialogueLine?>(null)
    private var pendingDialogues: List<DialogueLine> = emptyList()
    private var dialogueIndex = 0

    var isPaused by mutableStateOf(false)
    var isDrivingVehicle by mutableStateOf(false)
    var vehicleSpeedKmh by mutableFloatStateOf(0f)

    var inputJoystickX = 0f
    var inputJoystickY = 0f
    var throttleInput = 0f
    var steeringInput = 0f
    var handbrake = false

    var saveToastMessage by mutableStateOf<String?>(null)

    fun initialize() {
        cityData = CityBuilder.buildCity(entityManager)
        trafficSystem = TrafficSystem(entityManager, cityData.roadNetwork)
        pedestrianSystem = PedestrianSystem(entityManager, cityData.roadNetwork)

        renderer = GameRenderer(
            player = player,
            camera = camera,
            entityManager = entityManager,
            cityData = cityData,
            dayNightSystem = dayNightSystem,
            missionManager = missionManager
        )

        audioEngine.start()

        // Start with first mission briefing
        missionManager.startCurrentMission()

        // Try loading saved game
        scope.launch {
            val save = db.saveDao().getSaveDirect()
            if (save != null) {
                player.position = Vector3(save.playerX, save.playerY, save.playerZ)
                player.yaw = save.playerHeading
                player.health = save.playerHealth
                player.money = save.playerMoney
                player.setOutfit(Player.OUTFITS.find { it.id == save.equippedOutfitId } ?: Player.OUTFITS[0])
                missionManager.currentMissionIndex = save.currentMissionIndex
                dayNightSystem.timeOfDay = save.timeOfDay
                dayNightSystem.weather = WeatherType.valueOf(save.weatherName)
            }
        }
    }

    fun update(deltaTime: Float) {
        if (isPaused) return

        // 1. Day / Night & Weather Simulation
        dayNightSystem.update(deltaTime)
        audioEngine.setWeatherRaining(dayNightSystem.weather == WeatherType.RAIN)

        // 2. Road Network & Traffic Lights
        cityData.roadNetwork.update(deltaTime)

        // 3. District detection
        currentDistrict = CityBuilder.getDistrictAt(player.position.x, player.position.z)

        // 4. Update Player or Vehicle
        val currentVeh = player.currentVehicle
        if (player.isInsideVehicle && currentVeh != null) {
            isDrivingVehicle = true
            val vComp = currentVeh.get<VehicleComponent>()
            val trans = currentVeh.get<TransformComponent>()

            if (vComp != null && trans != null) {
                // Steer with joystick X if not using pedals
                val finalSteer = if (abs(steeringInput) > 0.05f) steeringInput else inputJoystickX
                val finalThrottle = if (abs(throttleInput) > 0.05f) throttleInput else inputJoystickY

                VehiclePhysics.updateVehicle(
                    vehicleEntity = currentVeh,
                    deltaTime = deltaTime,
                    throttleInput = finalThrottle,
                    steeringInput = finalSteer,
                    handbrake = handbrake,
                    obstacles = cityData.staticObjects
                )

                // Sync player pos to vehicle
                player.position = trans.position.copy()
                vehicleSpeedKmh = vComp.currentSpeed * 3.6f

                // Audio
                val speedRatio = abs(vComp.currentSpeed) / vComp.type.maxSpeed
                audioEngine.updateVehicleAudio(true, speedRatio)

                // Camera follow vehicle
                camera.update(deltaTime, trans.position, true, abs(vComp.currentSpeed), cityData.staticObjects)
            }
        } else {
            isDrivingVehicle = false
            vehicleSpeedKmh = 0f
            audioEngine.updateVehicleAudio(false, 0f)

            player.update(
                deltaTime = deltaTime,
                inputX = inputJoystickX,
                inputY = inputJoystickY,
                cameraYaw = camera.yaw,
                obstacles = cityData.staticObjects
            )

            // Camera follow player
            camera.update(deltaTime, player.position, false, 0f, cityData.staticObjects)
        }

        // 5. Traffic AI Simulation
        trafficSystem.update(deltaTime, player.position)

        // 6. Pedestrian AI Simulation
        pedestrianSystem.update(deltaTime, player.position)

        // 7. Interactive Context Trigger Checks
        updateInteractionPrompt()

        // 8. Story Mission Progress Check
        updateMissionProgress(deltaTime)
    }

    private fun updateInteractionPrompt() {
        var closestPrompt: String? = null
        var closestDist = Float.MAX_VALUE
        var closestTarget: InteractiveComponent? = null

        val interactives = entityManager.getEntitiesWith2<InteractiveComponent, TransformComponent>()
        for (e in interactives) {
            // Don't interact with vehicle we are currently inside
            if (player.isInsideVehicle && e.id == player.currentVehicle?.id) continue

            val trans = e.get<TransformComponent>() ?: continue
            val inter = e.get<InteractiveComponent>() ?: continue

            val dist = player.position.distanceToXZ(trans.position)
            if (dist < inter.radius && dist < closestDist) {
                closestDist = dist
                closestPrompt = inter.promptText
                closestTarget = inter
            }
        }

        interactionPrompt = closestPrompt
        nearbyInteractiveTarget = closestTarget
    }

    private fun updateMissionProgress(deltaTime: Float) {
        val currentMission = missionManager.getCurrentMission() ?: return
        val currentObjective = missionManager.getCurrentObjective() ?: return

        // Objective countdown timer if timed
        if (currentObjective.timeLimitSeconds > 0f) {
            missionManager.objectiveTimer = (missionManager.objectiveTimer - deltaTime).coerceAtLeast(0f)
        }

        // Distance check to objective target location
        val dist = player.position.distanceToXZ(currentObjective.targetPosition)
        if (dist <= currentObjective.targetRadius) {
            // Reached objective!
            if (currentObjective.dialogueOnReach.isNotEmpty()) {
                startDialogueSequence(currentObjective.dialogueOnReach)
            }

            audioEngine.playFanfareSound()
            val finishedMission = missionManager.advanceObjective()
            if (finishedMission) {
                player.money += currentMission.rewardCash
                saveToastMessage = "MISSION COMPLETE: ${currentMission.title}! +$${currentMission.rewardCash}"
            }
        }
    }

    fun triggerInteraction() {
        val target = nearbyInteractiveTarget ?: return
        audioEngine.playClickSound()

        when (target.type) {
            InteractionType.VEHICLE_DOOR -> {
                // Find vehicle entity
                val vehEntities = entityManager.getEntitiesWith2<VehicleComponent, TransformComponent>()
                for (v in vehEntities) {
                    val trans = v.get<TransformComponent>() ?: continue
                    if (player.position.distanceToXZ(trans.position) < 4f) {
                        enterVehicle(v)
                        break
                    }
                }
            }
            InteractionType.MISSION_GIVER -> {
                if (!missionManager.isMissionActive) {
                    missionManager.startCurrentMission()
                }
            }
            InteractionType.SAFEHOUSE -> {
                player.health = player.maxHealth
                player.stamina = player.maxStamina
                saveGame()
                saveToastMessage = "Rested at Safehouse. Health Restored & Game Saved!"
            }
            InteractionType.NPC_TALK -> {
                startDialogueSequence(
                    listOf(
                        DialogueLine("Citizen", "Metropolitan Resident", target.payloadId.ifEmpty { "Evening, Vance. Stay sharp out there." })
                    )
                )
            }
            InteractionType.TAXI_PASSENGER -> {
                player.money += 350
                saveToastMessage = "Fare Delivered: +$350 Taxi Fare!"
                audioEngine.playFanfareSound()
            }
            InteractionType.STREET_RACE_MARKER -> {
                player.money += 2500
                saveToastMessage = "Street Circuit Won: +$2,500 Prize Money!"
                audioEngine.playFanfareSound()
            }
            else -> {}
        }
    }

    fun enterVehicle(vehicleEntity: com.example.engine.ecs.Entity) {
        val vComp = vehicleEntity.get<VehicleComponent>() ?: return
        val trans = vehicleEntity.get<TransformComponent>() ?: return

        vComp.isPlayerDriven = true
        player.currentVehicle = vehicleEntity
        player.isInsideVehicle = true
        player.position = trans.position.copy()
    }

    fun exitVehicle() {
        val currentVeh = player.currentVehicle ?: return
        val vComp = currentVeh.get<VehicleComponent>()
        val trans = currentVeh.get<TransformComponent>()

        vComp?.isPlayerDriven = false
        vComp?.currentSpeed = 0f
        vComp?.throttle = 0f

        // Place player next to driver side door
        if (trans != null) {
            player.position = trans.position.add(-2.4f, 0f, 0f)
        }
        player.isInsideVehicle = false
        player.currentVehicle = null
    }

    fun spawnPersonalVehicle(type: VehicleType) {
        // Spawn car 4m ahead of player
        val spawnPos = player.position.add(0f, 0f, 4.5f)
        val entity = entityManager.createEntity("Personal_${type.displayName}")
        entity.add(TransformComponent(position = spawnPos, yaw = player.yaw))
        entity.add(com.example.engine.ecs.PhysicsComponent(collisionRadius = 2.4f))
        entity.add(VehicleComponent(type = type))
        entity.add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "ENTER ${type.displayName}"))
        saveToastMessage = "Valet delivered ${type.displayName} nearby!"
        audioEngine.playFanfareSound()
    }

    fun fastTravelTo(dest: Vector3) {
        if (player.isInsideVehicle) {
            exitVehicle()
        }
        player.position = dest.copy()
        saveToastMessage = "Fast Travel: Arrived at destination."
    }

    fun startDialogueSequence(lines: List<DialogueLine>) {
        if (lines.isEmpty()) return
        pendingDialogues = lines
        dialogueIndex = 0
        activeDialogue = pendingDialogues[0]
    }

    fun advanceDialogue() {
        audioEngine.playClickSound()
        dialogueIndex++
        if (dialogueIndex < pendingDialogues.size) {
            activeDialogue = pendingDialogues[dialogueIndex]
        } else {
            activeDialogue = null
            pendingDialogues = emptyList()
        }
    }

    fun saveGame() {
        scope.launch {
            val save = GameSaveEntity(
                id = 1,
                playerX = player.position.x,
                playerY = player.position.y,
                playerZ = player.position.z,
                playerHeading = player.yaw,
                playerHealth = player.health,
                playerMoney = player.money,
                currentMissionIndex = missionManager.currentMissionIndex,
                equippedOutfitId = player.currentOutfit.id,
                timeOfDay = dayNightSystem.timeOfDay,
                weatherName = dayNightSystem.weather.name,
                viewDistance = renderer.viewDistance,
                trafficDensity = trafficSystem.maxTrafficVehicles,
                audioVolume = audioEngine.masterVolume
            )
            db.saveDao().saveGame(save)
            saveToastMessage = "Game Progress Saved to Local Database!"
        }
    }

    fun setOutfit(outfit: Outfit) {
        player.setOutfit(outfit)
        audioEngine.playFanfareSound()
    }

    fun destroy() {
        audioEngine.stop()
    }
}
