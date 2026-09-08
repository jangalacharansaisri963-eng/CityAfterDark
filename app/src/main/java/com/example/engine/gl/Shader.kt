package com.example.engine.gl

import android.opengl.GLES20
import android.util.Log

class Shader(vertexCode: String, fragmentCode: String) {
    val programId: Int

    val aPosition: Int
    val aNormal: Int
    val aColor: Int

    val uMVPMatrix: Int
    val uModelMatrix: Int
    val uNormalMatrix: Int
    val uViewPos: Int

    val uLightDir: Int
    val uLightColor: Int
    val uAmbientColor: Int

    val uFogColor: Int
    val uFogDensity: Int
    val uFogNear: Int
    val uFogFar: Int

    val uEmissiveFactor: Int
    val uWetness: Int

    init {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)

        programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)
        GLES20.glLinkProgram(programId)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val log = GLES20.glGetProgramInfoLog(programId)
            GLES20.glDeleteProgram(programId)
            throw RuntimeException("Could not link GL program: $log")
        }

        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)

        // Cache attribute and uniform locations
        aPosition = GLES20.glGetAttribLocation(programId, "aPosition")
        aNormal = GLES20.glGetAttribLocation(programId, "aNormal")
        aColor = GLES20.glGetAttribLocation(programId, "aColor")

        uMVPMatrix = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        uModelMatrix = GLES20.glGetUniformLocation(programId, "uModelMatrix")
        uNormalMatrix = GLES20.glGetUniformLocation(programId, "uNormalMatrix")
        uViewPos = GLES20.glGetUniformLocation(programId, "uViewPos")

        uLightDir = GLES20.glGetUniformLocation(programId, "uLightDir")
        uLightColor = GLES20.glGetUniformLocation(programId, "uLightColor")
        uAmbientColor = GLES20.glGetUniformLocation(programId, "uAmbientColor")

        uFogColor = GLES20.glGetUniformLocation(programId, "uFogColor")
        uFogDensity = GLES20.glGetUniformLocation(programId, "uFogDensity")
        uFogNear = GLES20.glGetUniformLocation(programId, "uFogNear")
        uFogFar = GLES20.glGetUniformLocation(programId, "uFogFar")

        uEmissiveFactor = GLES20.glGetUniformLocation(programId, "uEmissiveFactor")
        uWetness = GLES20.glGetUniformLocation(programId, "uWetness")
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    companion object {
        private fun compileShader(type: Int, code: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)

            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                val info = GLES20.glGetShaderInfoLog(shader)
                GLES20.glDeleteShader(shader)
                throw RuntimeException("Compilation failed for shader ($type): $info\nCode:\n$code")
            }
            return shader
        }

        const val STANDARD_VERTEX_SHADER = """
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            uniform mat4 uNormalMatrix;
            
            attribute vec4 aPosition;
            attribute vec3 aNormal;
            attribute vec4 aColor;
            
            varying vec4 vColor;
            varying vec3 vNormal;
            varying vec3 vFragPos;
            varying float vDistance;
            
            void main() {
                vec4 worldPos = uModelMatrix * aPosition;
                vFragPos = worldPos.xyz;
                vNormal = normalize(vec3(uNormalMatrix * vec4(aNormal, 0.0)));
                vColor = aColor;
                
                gl_Position = uMVPMatrix * aPosition;
                vDistance = gl_Position.z;
            }
        """

        const val STANDARD_FRAGMENT_SHADER = """
            precision mediump float;
            
            varying vec4 vColor;
            varying vec3 vNormal;
            varying vec3 vFragPos;
            varying float vDistance;
            
            uniform vec3 uViewPos;
            uniform vec3 uLightDir;
            uniform vec3 uLightColor;
            uniform vec3 uAmbientColor;
            
            uniform vec3 uFogColor;
            uniform float uFogDensity;
            uniform float uFogNear;
            uniform float uFogFar;
            
            uniform float uEmissiveFactor;
            uniform float uWetness;
            
            void main() {
                // Ambient lighting
                vec3 ambient = uAmbientColor * vColor.rgb;
                
                // Diffuse lighting
                vec3 norm = normalize(vNormal);
                vec3 lightDir = normalize(uLightDir);
                float diff = max(dot(norm, lightDir), 0.0);
                vec3 diffuse = diff * uLightColor * vColor.rgb;
                
                // Specular lighting (higher on wet surfaces and car paint)
                vec3 viewDir = normalize(uViewPos - vFragPos);
                vec3 reflectDir = reflect(-lightDir, norm);
                float specPower = mix(16.0, 48.0, uWetness);
                float spec = pow(max(dot(viewDir, reflectDir), 0.0), specPower);
                vec3 specular = (0.2 + uWetness * 0.4) * spec * uLightColor;
                
                // Emissive boost (for neon signs, windows, lamps, taillights)
                vec3 emissive = vColor.rgb * uEmissiveFactor;
                
                vec3 result = ambient + diffuse + specular + emissive;
                
                // Distance atmospheric fog
                float fogFactor = clamp((vDistance - uFogNear) / (uFogFar - uFogNear), 0.0, 1.0);
                fogFactor = fogFactor * uFogDensity;
                result = mix(result, uFogColor, clamp(fogFactor, 0.0, 1.0));
                
                gl_FragColor = vec4(result, vColor.a);
            }
        """
    }
}
