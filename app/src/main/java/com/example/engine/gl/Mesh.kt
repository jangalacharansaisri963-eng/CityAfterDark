package com.example.engine.gl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Mesh(
    val vertexCount: Int,
    val vertexBuffer: FloatBuffer,
    val normalBuffer: FloatBuffer,
    val colorBuffer: FloatBuffer,
    val indexBuffer: ShortBuffer? = null,
    val indexCount: Int = 0
) {
    fun render(shader: Shader) {
        // Vertex positions
        if (shader.aPosition >= 0) {
            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(shader.aPosition)
            GLES20.glVertexAttribPointer(shader.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        }

        // Normals
        if (shader.aNormal >= 0) {
            normalBuffer.position(0)
            GLES20.glEnableVertexAttribArray(shader.aNormal)
            GLES20.glVertexAttribPointer(shader.aNormal, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)
        }

        // Vertex Colors
        if (shader.aColor >= 0) {
            colorBuffer.position(0)
            GLES20.glEnableVertexAttribArray(shader.aColor)
            GLES20.glVertexAttribPointer(shader.aColor, 4, GLES20.GL_FLOAT, false, 0, colorBuffer)
        }

        if (indexBuffer != null && indexCount > 0) {
            indexBuffer.position(0)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        }

        if (shader.aPosition >= 0) GLES20.glDisableVertexAttribArray(shader.aPosition)
        if (shader.aNormal >= 0) GLES20.glDisableVertexAttribArray(shader.aNormal)
        if (shader.aColor >= 0) GLES20.glDisableVertexAttribArray(shader.aColor)
    }

    companion object {
        fun createFloatBuffer(data: FloatArray): FloatBuffer {
            val bb = ByteBuffer.allocateDirect(data.size * 4)
            bb.order(ByteOrder.nativeOrder())
            val fb = bb.asFloatBuffer()
            fb.put(data)
            fb.position(0)
            return fb
        }

        fun createShortBuffer(data: ShortArray): ShortBuffer {
            val bb = ByteBuffer.allocateDirect(data.size * 2)
            bb.order(ByteOrder.nativeOrder())
            val sb = bb.asShortBuffer()
            sb.put(data)
            sb.position(0)
            return sb
        }
    }
}
