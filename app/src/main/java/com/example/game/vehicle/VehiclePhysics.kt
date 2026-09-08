package com.example.game.vehicle

import com.example.engine.ecs.Entity
import com.example.engine.ecs.PhysicsComponent
import com.example.engine.ecs.TransformComponent
import com.example.engine.ecs.VehicleComponent
import com.example.engine.math.Vector3
import com.example.game.world.StaticWorldObject
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object VehiclePhysics {

    fun updateVehicle(
        vehicleEntity: Entity,
        deltaTime: Float,
        throttleInput: Float, // -1 (reverse) to +1 (forward)
        steeringInput: Float, // -1 (left) to +1 (right)
        handbrake: Boolean,
        obstacles: List<StaticWorldObject>
    ) {
        val transform = vehicleEntity.get<TransformComponent>() ?: return
        val vehicle = vehicleEntity.get<VehicleComponent>() ?: return
        val physics = vehicleEntity.get<PhysicsComponent>()

        val type = vehicle.type
        val maxSpeed = type.maxSpeed
        val accel = type.acceleration
        val brakePower = type.brakePower
        val handling = type.handling

        // Accelerate or brake
        if (throttleInput > 0.05f) {
            // Forward acceleration
            if (vehicle.currentSpeed < 0f) {
                // Braking while in reverse
                vehicle.currentSpeed += brakePower * deltaTime
            } else {
                val effectiveAccel = accel * (1f - (vehicle.currentSpeed / maxSpeed).coerceIn(0f, 0.9f))
                vehicle.currentSpeed += effectiveAccel * throttleInput * deltaTime
            }
        } else if (throttleInput < -0.05f) {
            // Reverse or forward braking
            if (vehicle.currentSpeed > 0.5f) {
                // Braking while moving forward
                vehicle.currentSpeed -= brakePower * abs(throttleInput) * deltaTime
            } else {
                // Reverse
                val maxReverseSpeed = -maxSpeed * 0.35f
                vehicle.currentSpeed = (vehicle.currentSpeed - (accel * 0.6f) * abs(throttleInput) * deltaTime)
                    .coerceAtLeast(maxReverseSpeed)
            }
        } else {
            // Engine drag / coasting friction
            val friction = if (handbrake) 35f else 4.5f
            if (vehicle.currentSpeed > 0f) {
                vehicle.currentSpeed = (vehicle.currentSpeed - friction * deltaTime).coerceAtLeast(0f)
            } else if (vehicle.currentSpeed < 0f) {
                vehicle.currentSpeed = (vehicle.currentSpeed + friction * deltaTime).coerceAtMost(0f)
            }
        }

        // Clamp speed
        vehicle.currentSpeed = vehicle.currentSpeed.coerceIn(-maxSpeed * 0.35f, maxSpeed)

        // Steering
        val speedRatio = abs(vehicle.currentSpeed) / maxSpeed
        if (speedRatio > 0.02f) {
            val turnDirection = if (vehicle.currentSpeed >= 0f) 1f else -1f
            val steerMultiplier = if (handbrake) 1.6f else 1.0f // Handbrake slide turn
            val yawDelta = steeringInput * handling * 48f * steerMultiplier * turnDirection * deltaTime
            transform.yaw = (transform.yaw - yawDelta) % 360f
        }

        // Move vehicle in direction of heading
        val radYaw = transform.yaw * (PI.toFloat() / 180f)
        val forwardX = sin(radYaw)
        val forwardZ = cos(radYaw)

        val nextX = transform.position.x + forwardX * vehicle.currentSpeed * deltaTime
        val nextZ = transform.position.z + forwardZ * vehicle.currentSpeed * deltaTime

        // Obstacle collision check
        val carRadius = physics?.collisionRadius ?: 2.3f
        var collided = false

        for (obs in obstacles) {
            if (!obs.isCollidable) continue
            val dist = transform.position.distanceToXZ(obs.position)
            if (dist > obs.boundsRadius + 6f) continue

            val nextPos = Vector3(nextX, 0f, nextZ)
            if (nextPos.distanceToXZ(obs.position) < obs.boundsRadius + carRadius) {
                collided = true
                // Dampen speed on impact
                vehicle.currentSpeed = -vehicle.currentSpeed * 0.25f
                break
            }
        }

        if (!collided) {
            transform.position.x = nextX
            transform.position.z = nextZ
        }
    }
}
