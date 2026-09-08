package com.example.game.simulation

import com.example.engine.ecs.EntityManager
import com.example.engine.ecs.InteractiveComponent
import com.example.engine.ecs.InteractionType
import com.example.engine.ecs.PhysicsComponent
import com.example.engine.ecs.TrafficVehicleComponent
import com.example.engine.ecs.TransformComponent
import com.example.engine.ecs.VehicleComponent
import com.example.engine.math.Vector3
import com.example.game.vehicle.VehicleType
import com.example.game.world.RoadNetwork
import com.example.game.world.TrafficLightState
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class TrafficSystem(
    private val entityManager: EntityManager,
    private val roadNetwork: RoadNetwork
) {
    var maxTrafficVehicles: Int = 14 // Controlled by settings (8 for Low, 14 for Med, 22 for High)
    private val random = Random(1234L)
    private var spawnCooldown: Float = 0f

    private val vehicleTypes = arrayOf(
        VehicleType.METRO_SWIFT,
        VehicleType.VANGUARD_SEDAN,
        VehicleType.CITY_CAB,
        VehicleType.TITAN_SUV,
        VehicleType.HAULER_PICKUP,
        VehicleType.CARGO_VAN
    )

    fun update(deltaTime: Float, playerPos: Vector3) {
        val trafficEntities = entityManager.getEntitiesWith2<TrafficVehicleComponent, TransformComponent>()

        // Update existing traffic vehicles
        for (entity in trafficEntities) {
            val traffic = entity.get<TrafficVehicleComponent>() ?: continue
            val transform = entity.get<TransformComponent>() ?: continue
            val vehicle = entity.get<VehicleComponent>() ?: continue

            // If player has entered this vehicle, remove AI control
            if (vehicle.isPlayerDriven) {
                entity.remove(TrafficVehicleComponent::class.java)
                continue
            }

            // Despawn if too far from player
            val distToPlayer = transform.position.distanceToXZ(playerPos)
            if (distToPlayer > 180f) {
                entityManager.removeEntity(entity)
                continue
            }

            // Move along road
            if (traffic.roadIndex in roadNetwork.roads.indices) {
                val road = roadNetwork.roads[traffic.roadIndex]
                val laneOffset = if (traffic.lane == 0) 3.5f else -3.5f
                val roadDir = road.direction

                // Check for red light at upcoming intersection
                var shouldStop = false
                for (inter in roadNetwork.intersections) {
                    val distToInter = transform.position.distanceToXZ(inter.center)
                    if (distToInter in 4f..18f) {
                        // Check light state for this road orientation
                        val isEastWest = kotlin.math.abs(roadDir.x) > kotlin.math.abs(roadDir.z)
                        val lightState = if (isEastWest) inter.stateEastWest else inter.stateNorthSouth
                        if (lightState == TrafficLightState.RED || lightState == TrafficLightState.YELLOW) {
                            shouldStop = true
                            break
                        }
                    }
                }

                // Check distance to other vehicles ahead
                for (other in trafficEntities) {
                    if (other.id == entity.id) continue
                    val otherTrans = other.get<TransformComponent>() ?: continue
                    val distToOther = transform.position.distanceToXZ(otherTrans.position)
                    if (distToOther < 8.5f) {
                        // Check if in front
                        val toOther = otherTrans.position.sub(transform.position).normalized()
                        if (toOther.dot(roadDir) > 0.6f) {
                            shouldStop = true
                            break
                        }
                    }
                }

                // Check distance to player
                if (distToPlayer < 7.5f) {
                    val toPlayer = playerPos.sub(transform.position).normalized()
                    if (toPlayer.dot(roadDir) > 0.5f) {
                        shouldStop = true
                    }
                }

                if (shouldStop) {
                    vehicle.currentSpeed = (vehicle.currentSpeed - 24f * deltaTime).coerceAtLeast(0f)
                } else {
                    vehicle.currentSpeed = (vehicle.currentSpeed + 8f * deltaTime).coerceAtMost(traffic.desiredSpeed)
                }

                // Apply velocity
                transform.position.addAssign(
                    roadDir.x * vehicle.currentSpeed * deltaTime,
                    0f,
                    roadDir.z * vehicle.currentSpeed * deltaTime
                )

                // Face direction of road travel
                val targetYaw = atan2(roadDir.x, roadDir.z) * (180f / PI.toFloat())
                transform.yaw = targetYaw

                // If reached end of road segment, pick a connecting road
                val distToEnd = transform.position.distanceToXZ(road.end)
                if (distToEnd < 6f) {
                    // Find connected road or reverse direction
                    val nextRoads = roadNetwork.roads.indices.filter { it != traffic.roadIndex }
                    if (nextRoads.isNotEmpty()) {
                        traffic.roadIndex = nextRoads[random.nextInt(nextRoads.size)]
                        val nextRoad = roadNetwork.roads[traffic.roadIndex]
                        transform.position.set(nextRoad.start.add(laneOffset, 0f, 0f))
                    }
                }
            }
        }

        // Spawn new traffic vehicle if under limit
        spawnCooldown -= deltaTime
        if (trafficEntities.size < maxTrafficVehicles && spawnCooldown <= 0f && roadNetwork.roads.isNotEmpty()) {
            spawnCooldown = 1.2f
            spawnTrafficVehicleNear(playerPos)
        }
    }

    private fun spawnTrafficVehicleNear(playerPos: Vector3) {
        // Find roads within 50m to 120m of player
        val candidateRoads = roadNetwork.roads.indices.filter { rIdx ->
            val road = roadNetwork.roads[rIdx]
            val dist = playerPos.distanceToXZ(road.start)
            dist in 45f..130f
        }

        if (candidateRoads.isEmpty()) return

        val roadIdx = candidateRoads[random.nextInt(candidateRoads.size)]
        val road = roadNetwork.roads[roadIdx]
        val lane = random.nextInt(2)
        val laneOffset = if (lane == 0) 3.5f else -3.5f

        // Spawn at road start offset
        val spawnPos = road.start.add(
            road.direction.z * laneOffset,
            0f,
            -road.direction.x * laneOffset
        )

        val vType = vehicleTypes[random.nextInt(vehicleTypes.size)]
        val entity = entityManager.createEntity("Traffic_${vType.displayName}")
        entity.add(TransformComponent(position = spawnPos))
        entity.add(PhysicsComponent(collisionRadius = 2.2f))
        entity.add(VehicleComponent(type = vType, currentSpeed = 10f))
        entity.add(TrafficVehicleComponent(roadIndex = roadIdx, lane = lane, desiredSpeed = 11f + random.nextFloat() * 4f))
        entity.add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "ENTER ${vType.displayName}"))
    }
}
