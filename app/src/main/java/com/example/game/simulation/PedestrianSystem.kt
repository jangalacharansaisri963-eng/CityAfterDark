package com.example.game.simulation

import com.example.engine.ecs.EntityManager
import com.example.engine.ecs.InteractiveComponent
import com.example.engine.ecs.InteractionType
import com.example.engine.ecs.PedestrianComponent
import com.example.engine.ecs.PedestrianState
import com.example.engine.ecs.TransformComponent
import com.example.engine.math.Vector3
import com.example.game.world.RoadNetwork
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PedestrianSystem(
    private val entityManager: EntityManager,
    private val roadNetwork: RoadNetwork
) {
    var maxPedestrians: Int = 18 // Configured by settings (10 for Low, 18 for Med, 28 for High)
    private val random = Random(5678L)
    private var spawnTimer: Float = 0f

    private val civilianDialogueSnippets = listOf(
        "Careful on the roads tonight, the syndicates are active.",
        "They say someone pulled off a score on the skyline tower.",
        "Marcus at the Diner makes the best black coffee in town.",
        "Rain is rolling in from the marina docks tonight.",
        "Check out the Neon Promenade if you need high-class threads.",
        "Stay out of the Industrial District after midnight unless you're armed."
    )

    fun update(deltaTime: Float, playerPos: Vector3) {
        val pedestrians = entityManager.getEntitiesWith2<PedestrianComponent, TransformComponent>()

        for (entity in pedestrians) {
            val ped = entity.get<PedestrianComponent>() ?: continue
            val transform = entity.get<TransformComponent>() ?: continue

            val distToPlayer = transform.position.distanceToXZ(playerPos)
            if (distToPlayer > 130f) {
                entityManager.removeEntity(entity)
                continue
            }

            when (ped.state) {
                PedestrianState.IDLE -> {
                    ped.waitTimer -= deltaTime
                    if (ped.waitTimer <= 0f) {
                        ped.state = PedestrianState.WALKING
                        // Pick random next waypoint nearby
                        val target = transform.position.add(
                            (random.nextFloat() - 0.5f) * 28f,
                            0f,
                            (random.nextFloat() - 0.5f) * 28f
                        )
                        ped.waypoints = listOf(target)
                        ped.currentWaypointIndex = 0
                    }
                }
                PedestrianState.WALKING -> {
                    if (ped.waypoints.isNotEmpty() && ped.currentWaypointIndex < ped.waypoints.size) {
                        val target = ped.waypoints[ped.currentWaypointIndex]
                        val diff = target.sub(transform.position)
                        val dist = diff.length()

                        if (dist < 1.2f) {
                            // Reached waypoint, idle briefly
                            ped.state = PedestrianState.IDLE
                            ped.waitTimer = 2.5f + random.nextFloat() * 4f
                        } else {
                            val dir = diff.normalized()
                            transform.position.addAssign(dir.x * ped.walkSpeed * deltaTime, 0f, dir.z * ped.walkSpeed * deltaTime)
                            val targetYaw = atan2(dir.x, dir.z) * (180f / PI.toFloat())
                            transform.yaw = targetYaw
                        }
                    } else {
                        ped.state = PedestrianState.IDLE
                        ped.waitTimer = 1.5f
                    }
                }
                PedestrianState.WAITING_CROSSING -> {
                    ped.waitTimer -= deltaTime
                    if (ped.waitTimer <= 0f) ped.state = PedestrianState.WALKING
                }
                PedestrianState.FLEEING -> {
                    ped.waitTimer -= deltaTime
                    if (ped.waitTimer <= 0f) ped.state = PedestrianState.WALKING
                }
            }
        }

        // Spawn civilians near player
        spawnTimer -= deltaTime
        if (pedestrians.size < maxPedestrians && spawnTimer <= 0f) {
            spawnTimer = 1.0f
            spawnPedestrianNear(playerPos)
        }
    }

    private fun spawnPedestrianNear(playerPos: Vector3) {
        val angle = random.nextFloat() * 2f * PI.toFloat()
        val dist = 25f + random.nextFloat() * 65f
        val spawnPos = Vector3(
            playerPos.x + cos(angle) * dist,
            0f,
            playerPos.z + sin(angle) * dist
        )

        val pedEntity = entityManager.createEntity("Pedestrian_${random.nextInt(1000)}")
        val outfitColors = arrayOf(
            floatArrayOf(0.2f, 0.4f, 0.7f, 1f),
            floatArrayOf(0.8f, 0.3f, 0.2f, 1f),
            floatArrayOf(0.3f, 0.6f, 0.3f, 1f),
            floatArrayOf(0.9f, 0.8f, 0.2f, 1f),
            floatArrayOf(0.5f, 0.2f, 0.6f, 1f)
        )
        val outfit = outfitColors[random.nextInt(outfitColors.size)]

        val dialogue = civilianDialogueSnippets[random.nextInt(civilianDialogueSnippets.size)]

        pedEntity.add(TransformComponent(position = spawnPos))
        pedEntity.add(
            PedestrianComponent(
                state = PedestrianState.WALKING,
                walkSpeed = 1.6f + random.nextFloat() * 0.8f,
                waypoints = listOf(spawnPos.add((random.nextFloat() - 0.5f) * 30f, 0f, (random.nextFloat() - 0.5f) * 30f)),
                outfitColor = outfit
            )
        )
        pedEntity.add(InteractiveComponent(InteractionType.NPC_TALK, "TALK TO CITIZEN", payloadId = dialogue))
    }
}
