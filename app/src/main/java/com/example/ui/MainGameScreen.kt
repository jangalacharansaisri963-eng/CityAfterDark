package com.example.ui

import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.engine.ecs.VehicleComponent
import com.example.game.GameEngine
import com.example.game.features.WeaponType
import com.example.ui.components.AtmHackDialog
import com.example.ui.components.CameraTouchPad
import com.example.ui.components.DealershipGarageDialog
import com.example.ui.components.FullMapDialog
import com.example.ui.components.GameHud
import com.example.ui.components.InVehicleActionControls
import com.example.ui.components.OnFootActionControls
import com.example.ui.components.PauseMenuDialog
import com.example.ui.components.PhoneDialog
import com.example.ui.components.PhotoModeDialog
import com.example.ui.components.RealEstateDialog
import com.example.ui.components.RotatePhoneScreen
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SkillTreeDialog
import com.example.ui.components.VirtualJoystick
import com.example.ui.components.WardrobeDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "MainGameScreen"

@Composable
fun MainGameScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var forceLandscapeOverride by remember { mutableStateOf(false) }
    val isLandscape = forceLandscapeOverride || (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)

    val gameEngine = remember { GameEngine(context) }

    var isReady by remember { mutableStateOf(false) }
    var initError by remember { mutableStateOf<String?>(null) }

    // Dialog & overlay states
    var showPauseMenu by remember { mutableStateOf(false) }
    var showFullMap by remember { mutableStateOf(false) }
    var showWardrobe by remember { mutableStateOf(false) }
    var showGarage by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Initialize game engine asynchronously off the main UI thread
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Initializing GameEngine…")
                gameEngine.initialize()
                Log.d(TAG, "GameEngine initialized successfully")
                withContext(Dispatchers.Main) {
                    isReady = true
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to initialize GameEngine", t)
                withContext(Dispatchers.Main) {
                    initError = t.message ?: "Failed to initialize 3D world"
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                gameEngine.destroy()
            } catch (t: Throwable) {
                Log.e(TAG, "Error destroying game engine", t)
            }
        }
    }

    if (initError != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0E14)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "City After Dark failed to start",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = initError ?: "Unknown error",
                    color = Color(0xFFFF8A80),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
                TextButton(onClick = {
                    initError = null
                    isReady = false
                }) {
                    Text("Dismiss")
                }
            }
        }
        return
    }

    if (!isReady) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0E14)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF00E5FF))
                Text(
                    text = "Loading city…",
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    // If held in portrait mode, show warning and halt rendering
    if (!isLandscape) {
        gameEngine.isPaused = true
        RotatePhoneScreen(
            onSimulateLandscape = { forceLandscapeOverride = true }
        )
        return
    } else {
        gameEngine.isPaused = showPauseMenu || showFullMap || showWardrobe || showGarage || showSettings ||
                gameEngine.showPhoneDialog || gameEngine.showAtmHackDialog || gameEngine.showSkillTreeDialog ||
                gameEngine.showRealEstateDialog || gameEngine.showPhotoModeDialog
    }

    // Game Update Loop running on Compose Frame Clock (approx 60 FPS)
    LaunchedEffect(isLandscape, isReady) {
        if (!isReady) return@LaunchedEffect
        var lastTimeNanos = System.nanoTime()
        while (true) {
            withFrameNanos { frameTimeNanos ->
                val dt = ((frameTimeNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTimeNanos = frameTimeNanos
                try {
                    gameEngine.update(dt)
                } catch (t: Throwable) {
                    Log.e(TAG, "gameEngine.update failed", t)
                }
            }
        }
    }

    // Clear Toast message after 3 seconds
    LaunchedEffect(gameEngine.saveToastMessage) {
        if (gameEngine.saveToastMessage != null) {
            delay(3000)
            gameEngine.saveToastMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 3D OpenGL Surface View
        AndroidView(
            factory = { ctx ->
                GLSurfaceView(ctx).apply {
                    setEGLContextClientVersion(2)
                    try {
                        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                    } catch (_: Throwable) {
                    }
                    setRenderer(gameEngine.renderer)
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                    setPreserveEGLContextOnPause(true)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Camera Touch Pad covering the center/right screen
        CameraTouchPad(
            onLook = { dyaw, dpitch ->
                gameEngine.camera.rotate(dyaw, dpitch)
            },
            modifier = Modifier.fillMaxSize()
        )

        // 3. Heads-Up Display (Minimap, Health, Mission Card, District, Dialogue)
        val currentMission = gameEngine.missionManager.getCurrentMission()
        val currentObj = gameEngine.missionManager.getCurrentObjective()
        val distToObj = if (currentObj != null) gameEngine.player.position.distanceToXZ(currentObj.targetPosition) else null

        GameHud(
            player = gameEngine.player,
            currentDistrict = gameEngine.currentDistrict,
            timeString = gameEngine.dayNightSystem.getFormattedTime(),
            weatherString = gameEngine.dayNightSystem.weather.displayName,
            currentMission = currentMission,
            objectiveDescription = currentObj?.description,
            distanceToObjective = distToObj,
            objectiveTimer = gameEngine.missionManager.objectiveTimer,
            isDriving = gameEngine.isDrivingVehicle,
            vehicleSpeedKmh = gameEngine.vehicleSpeedKmh,
            activeDialogue = gameEngine.activeDialogue,
            onAdvanceDialogue = { gameEngine.advanceDialogue() },
            onOpenPauseMenu = { showPauseMenu = true },
            onOpenFullMap = { showFullMap = true },
            roadNetwork = gameEngine.cityData.roadNetwork,
            entityManager = gameEngine.entityManager,
            targetPosition = currentObj?.targetPosition,
            engine = gameEngine,
            modifier = Modifier.fillMaxSize()
        )

        // 4. Virtual Joystick (Bottom-Left)
        VirtualJoystick(
            onMove = { x, y ->
                gameEngine.inputJoystickX = x
                gameEngine.inputJoystickY = y
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
        )

        // 5. Action Buttons (Bottom-Right)
        if (gameEngine.isDrivingVehicle) {
            val veh = gameEngine.player.currentVehicle?.get<VehicleComponent>()
            InVehicleActionControls(
                onThrottle = { t -> gameEngine.throttleInput = t },
                onHandbrake = { hb -> gameEngine.handbrake = hb },
                onHonk = { gameEngine.honkVehicle() },
                onToggleNitro = { gameEngine.toggleNitro() },
                isNitroActive = veh?.isNitroActive == true,
                nitroPercent = (veh?.nitroAmount ?: 0f) / 100f,
                onToggleHeadlights = { gameEngine.toggleHeadlights() },
                headlightsOn = veh?.headlightsOn == true,
                currentRadioStationName = gameEngine.radioSystem.currentStation.name,
                onNextRadio = { gameEngine.radioSystem.nextStation() },
                onExitVehicle = { gameEngine.exitVehicle() },
                onOpenPhone = { gameEngine.showPhoneDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        } else {
            val currWep = gameEngine.player.weapons.currentWeapon
            OnFootActionControls(
                isSprinting = gameEngine.player.isSprinting,
                onToggleSprint = { gameEngine.player.isSprinting = !gameEngine.player.isSprinting },
                onJump = { gameEngine.player.jump() },
                onRoll = { gameEngine.player.roll() },
                onAttack = { gameEngine.fireActiveWeapon() },
                onToggleFocus = { gameEngine.player.toggleFocus() },
                isFocusActive = gameEngine.player.isFocusActive,
                currentWeaponName = currWep.name,
                currentWeaponAmmo = if (currWep.type == WeaponType.FISTS) "∞" else "${currWep.ammoInMag}/${currWep.ammoReserve}",
                onNextWeapon = { gameEngine.player.weapons.nextWeapon() },
                onOpenPhone = { gameEngine.showPhoneDialog = true },
                interactionPrompt = gameEngine.interactionPrompt,
                onInteract = { gameEngine.triggerInteraction() },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        // Temporary Save / Notification Toast
        AnimatedVisibility(
            visible = gameEngine.saveToastMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp)
        ) {
            Surface(
                color = Color(0xF200E5FF),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
            ) {
                Text(
                    text = gameEngine.saveToastMessage ?: "",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Dialogs
        if (showPauseMenu) {
            PauseMenuDialog(
                onResume = { showPauseMenu = false },
                onSaveGame = {
                    gameEngine.saveGame()
                },
                onOpenMap = {
                    showPauseMenu = false
                    showFullMap = true
                },
                onOpenWardrobe = {
                    showPauseMenu = false
                    showWardrobe = true
                },
                onOpenGarage = {
                    showPauseMenu = false
                    showGarage = true
                },
                onOpenSettings = {
                    showPauseMenu = false
                    showSettings = true
                },
                saveFeedback = gameEngine.saveToastMessage
            )
        }

        if (showFullMap) {
            FullMapDialog(
                playerPos = gameEngine.player.position,
                targetPos = currentObj?.targetPosition,
                onClose = { showFullMap = false },
                onFastTravel = { dest -> gameEngine.fastTravelTo(dest) }
            )
        }

        if (showWardrobe) {
            WardrobeDialog(
                player = gameEngine.player,
                onEquipOutfit = { outfit -> gameEngine.setOutfit(outfit) },
                onClose = { showWardrobe = false }
            )
        }

        if (showGarage) {
            DealershipGarageDialog(
                player = gameEngine.player,
                onSpawnVehicle = { vehType -> gameEngine.spawnPersonalVehicle(vehType) },
                onClose = { showGarage = false }
            )
        }

        if (showSettings) {
            SettingsDialog(
                dayNightSystem = gameEngine.dayNightSystem,
                viewDistance = gameEngine.renderer.viewDistance,
                onChangeViewDistance = { gameEngine.renderer.viewDistance = it },
                trafficDensity = gameEngine.trafficSystem.maxTrafficVehicles,
                onChangeTrafficDensity = { gameEngine.trafficSystem.maxTrafficVehicles = it },
                masterVolume = gameEngine.audioEngine.masterVolume,
                onChangeVolume = {
                    gameEngine.audioEngine.masterVolume = it
                    gameEngine.audioEngine.isMuted = it <= 0.01f
                },
                onClose = { showSettings = false }
            )
        }

        // 20+ New Feature Dialog Overlays
        if (gameEngine.showPhoneDialog) {
            PhoneDialog(
                engine = gameEngine,
                onClose = { gameEngine.showPhoneDialog = false }
            )
        }

        if (gameEngine.showAtmHackDialog) {
            AtmHackDialog(
                onRewardSuccess = { cash ->
                    gameEngine.player.money += cash
                    gameEngine.skillTree.addXP(100)
                    gameEngine.wantedSystem.addHeat(20f)
                    gameEngine.saveToastMessage = "ATM HACKED: +$${cash} Cash Transferred!"
                    gameEngine.audioEngine.playFanfareSound()
                    gameEngine.showAtmHackDialog = false
                },
                onAlarmTriggered = {
                    gameEngine.wantedSystem.addHeat(35f)
                    gameEngine.saveToastMessage = "SECURITY ALERT: Police Dispatched!"
                    gameEngine.showAtmHackDialog = false
                },
                onDismiss = { gameEngine.showAtmHackDialog = false }
            )
        }

        if (gameEngine.showPhotoModeDialog) {
            PhotoModeDialog(
                onDismiss = { gameEngine.showPhotoModeDialog = false }
            )
        }

        if (gameEngine.showSkillTreeDialog) {
            SkillTreeDialog(
                skillTree = gameEngine.skillTree,
                onDismiss = { gameEngine.showSkillTreeDialog = false }
            )
        }

        if (gameEngine.showRealEstateDialog) {
            RealEstateDialog(
                realEstate = gameEngine.realEstate,
                player = gameEngine.player,
                onFastTravel = { dest -> gameEngine.fastTravelTo(dest) },
                onDismiss = { gameEngine.showRealEstateDialog = false }
            )
        }
    }
}
