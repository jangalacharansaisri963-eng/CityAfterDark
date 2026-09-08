package com.example.engine.math

import android.opengl.Matrix
import kotlin.math.cos
import kotlin.math.sin

class Matrix4 {
    val values = FloatArray(16)

    init {
        identity()
    }

    fun identity(): Matrix4 {
        Matrix.setIdentityM(values, 0)
        return this
    }

    fun set(other: Matrix4): Matrix4 {
        System.arraycopy(other.values, 0, values, 0, 16)
        return this
    }

    fun multiply(other: Matrix4): Matrix4 {
        val result = FloatArray(16)
        Matrix.multiplyMM(result, 0, values, 0, other.values, 0)
        System.arraycopy(result, 0, values, 0, 16)
        return this
    }

    fun translate(x: Float, y: Float, z: Float): Matrix4 {
        Matrix.translateM(values, 0, x, y, z)
        return this
    }

    fun translate(v: Vector3): Matrix4 {
        Matrix.translateM(values, 0, v.x, v.y, v.z)
        return this
    }

    fun rotate(angleDegrees: Float, x: Float, y: Float, z: Float): Matrix4 {
        Matrix.rotateM(values, 0, angleDegrees, x, y, z)
        return this
    }

    fun scale(x: Float, y: Float, z: Float): Matrix4 {
        Matrix.scaleM(values, 0, x, y, z)
        return this
    }

    fun setPerspective(fovyDegrees: Float, aspect: Float, near: Float, far: Float): Matrix4 {
        Matrix.perspectiveM(values, 0, fovyDegrees, aspect, near, far)
        return this
    }

    fun setLookAt(
        eyeX: Float, eyeY: Float, eyeZ: Float,
        centerX: Float, centerY: Float, centerZ: Float,
        upX: Float, upY: Float, upZ: Float
    ): Matrix4 {
        Matrix.setLookAtM(values, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
        return this
    }

    fun copy(): Matrix4 {
        val copy = Matrix4()
        System.arraycopy(values, 0, copy.values, 0, 16)
        return copy
    }

    companion object {
        fun multiply(a: Matrix4, b: Matrix4): Matrix4 {
            val result = Matrix4()
            Matrix.multiplyMM(result.values, 0, a.values, 0, b.values, 0)
            return result
        }
    }
}
