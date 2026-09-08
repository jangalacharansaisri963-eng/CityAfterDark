package com.example.engine.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vector3(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    fun set(nx: Float, ny: Float, nz: Float): Vector3 {
        x = nx
        y = ny
        z = nz
        return this
    }

    fun set(other: Vector3): Vector3 {
        x = other.x
        y = other.y
        z = other.z
        return this
    }

    fun add(other: Vector3): Vector3 = Vector3(x + other.x, y + other.y, z + other.z)
    fun add(dx: Float, dy: Float, dz: Float): Vector3 = Vector3(x + dx, y + dy, z + dz)

    fun addAssign(other: Vector3): Vector3 {
        x += other.x
        y += other.y
        z += other.z
        return this
    }

    fun addAssign(dx: Float, dy: Float, dz: Float): Vector3 {
        x += dx
        y += dy
        z += dz
        return this
    }

    fun sub(other: Vector3): Vector3 = Vector3(x - other.x, y - other.y, z - other.z)

    fun subAssign(other: Vector3): Vector3 {
        x -= other.x
        y -= other.y
        z -= other.z
        return this
    }

    fun scale(factor: Float): Vector3 = Vector3(x * factor, y * factor, z * factor)

    fun scaleAssign(factor: Float): Vector3 {
        x *= factor
        y *= factor
        z *= factor
        return this
    }

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun lengthSquared(): Float = x * x + y * y + z * z

    fun distanceTo(other: Vector3): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun distanceToSquared(other: Vector3): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return dx * dx + dy * dy + dz * dz
    }

    fun distanceToXZ(other: Vector3): Float {
        val dx = x - other.x
        val dz = z - other.z
        return sqrt(dx * dx + dz * dz)
    }

    fun normalized(): Vector3 {
        val len = length()
        return if (len > 0.0001f) Vector3(x / len, y / len, z / len) else Vector3(0f, 0f, 0f)
    }

    fun normalizeAssign(): Vector3 {
        val len = length()
        if (len > 0.0001f) {
            x /= len
            y /= len
            z /= len
        }
        return this
    }

    fun dot(other: Vector3): Float = x * other.x + y * other.y + z * other.z

    fun cross(other: Vector3): Vector3 = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    fun lerp(target: Vector3, t: Float): Vector3 {
        val clampedT = t.coerceIn(0f, 1f)
        return Vector3(
            x + (target.x - x) * clampedT,
            y + (target.y - y) * clampedT,
            z + (target.z - z) * clampedT
        )
    }

    companion object {
        val ZERO get() = Vector3(0f, 0f, 0f)
        val UP get() = Vector3(0f, 1f, 0f)
        val FORWARD get() = Vector3(0f, 0f, 1f)
        val RIGHT get() = Vector3(1f, 0f, 0f)
    }
}
