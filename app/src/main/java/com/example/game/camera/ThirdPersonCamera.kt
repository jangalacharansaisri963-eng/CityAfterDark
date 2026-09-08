package com.example.game.camera

import com.example.engine.math.Matrix4
import com.example.engine.math.Vector3
import com.example.game.world.StaticWorldObject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ThirdPersonCamera {
    var targetPosition: Vector3 = Vector3(82f, 1.4f, 42f)
    var currentPosition: Vector3 = Vector3(82f, 3.5f, 48f)

    var yaw: Float = 0f        // Orbit around Y axis
    var pitch: Float = 18f     // Orbit elevation angle (degrees)
    var distance: Float = 5.5f  // Current distance from target
    var targetDistance: Float = 5.5f

    var fov: Float = 65f
    var aspect: Float = 16f / 9f
    var near: Float = 0.5f
    var far: Float = 350f

    val viewMatrix = Matrix4()
    val projectionMatrix = Matrix4()

    fun update(
        deltaTime: Float,
        target: Vector3,
        isDriving: Boolean,
        drivingSpeed: Float,
        obstacles: List<StaticWorldObject>
    ) {
        // Desired distance expands slightly at higher driving speeds for sense of speed
        targetDistance = if (isDriving) {
            (8.5f + (drivingSpeed / 40f) * 3.5f).coerceIn(8.0f, 12.5f)
        } else {
            5.2f
        }
        distance += (targetDistance - distance) * (6f * deltaTime).coerceIn(0f, 1f)

        // Smooth follow target position
        val targetHeight = if (isDriving) 1.2f else 1.5f
        val desiredTarget = Vector3(target.x, target.y + targetHeight, target.z)
        targetPosition = targetPosition.lerp(desiredTarget, 14f * deltaTime)

        // Spherical coordinates calculation
        val radYaw = (yaw) * (PI.toFloat() / 180f)
        val radPitch = (pitch) * (PI.toFloat() / 180f)

        val offsetX = distance * sin(radYaw) * cos(radPitch)
        val offsetY = distance * sin(radPitch)
        val offsetZ = distance * cos(radYaw) * cos(radPitch)

        var idealCamPos = Vector3(
            targetPosition.x + offsetX,
            (targetPosition.y + offsetY).coerceAtLeast(0.6f), // Keep above road
            targetPosition.z + offsetZ
        )

        // Collision avoidance with buildings: Raycast-like check between target and idealCamPos
        for (obs in obstacles) {
            if (!obs.isCollidable) continue
            val distToTarget = targetPosition.distanceToXZ(obs.position)
            if (distToTarget > obs.boundsRadius + distance + 2f) continue

            // If ideal camera is inside obstacle radius, push camera in front of obstacle
            val camDistToObs = idealCamPos.distanceToXZ(obs.position)
            if (camDistToObs < obs.boundsRadius + 1.0f) {
                // Shorten distance to prevent wall clipping
                val safeDistance = (distance * 0.55f).coerceAtLeast(2.0f)
                idealCamPos = Vector3(
                    targetPosition.x + (offsetX / distance) * safeDistance,
                    (targetPosition.y + (offsetY / distance) * safeDistance).coerceAtLeast(0.6f),
                    targetPosition.z + (offsetZ / distance) * safeDistance
                )
            }
        }

        currentPosition = currentPosition.lerp(idealCamPos, 16f * deltaTime)

        // Build OpenGL View Matrix
        viewMatrix.setLookAt(
            currentPosition.x, currentPosition.y, currentPosition.z,
            targetPosition.x, targetPosition.y, targetPosition.z,
            0f, 1f, 0f
        )
    }

    fun onResize(width: Int, height: Int) {
        if (height <= 0) return
        aspect = width.toFloat() / height.toFloat()
        projectionMatrix.setPerspective(fov, aspect, near, far)
    }

    fun rotate(deltaYaw: Float, deltaPitch: Float) {
        yaw = (yaw + deltaYaw) % 360f
        pitch = (pitch - deltaPitch).coerceIn(-10f, 65f)
    }
}
