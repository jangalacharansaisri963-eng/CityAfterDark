package com.example.game.simulation

import com.example.engine.math.Vector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class WeatherType(val displayName: String) {
    CLEAR("Clear Night"),
    CLOUDY("Overcast"),
    RAIN("Rain & Wet Asphalt"),
    FOG("Dense City Fog")
}

class DayNightSystem {
    // Current time in hours (0.00 to 24.00). Default 20.5f (8:30 PM dusk/neon hour)
    var timeOfDay: Float = 20.5f
    var timeScale: Float = 0.05f // 1 real second = 0.05 game hours (approx 8 min full cycle)
    var isCyclePaused: Boolean = false

    var weather: WeatherType = WeatherType.CLEAR
    var rainFactor: Float = 0.0f // 0 to 1
    var wetness: Float = 0.2f

    // Computed lighting properties
    val sunDirection = Vector3()
    val sunColor = FloatArray(3)
    val ambientColor = FloatArray(3)
    val fogColor = FloatArray(3)
    var fogDensity: Float = 0.85f
    var fogNear: Float = 40f
    var fogFar: Float = 260f
    var emissiveMultiplier: Float = 1.0f
    var areCityLightsOn: Boolean = true

    fun update(deltaTime: Float) {
        if (!isCyclePaused) {
            timeOfDay = (timeOfDay + deltaTime * timeScale) % 24f
        }

        // Calculate sun position in sky
        val sunAngle = ((timeOfDay - 6f) / 24f) * (2f * PI.toFloat())
        sunDirection.x = cos(sunAngle)
        sunDirection.y = sin(sunAngle)
        sunDirection.z = 0.4f
        sunDirection.normalizeAssign()

        // Interpolate colors based on hour
        when {
            // Night (21:00 - 05:00)
            timeOfDay >= 21f || timeOfDay < 5f -> {
                areCityLightsOn = true
                emissiveMultiplier = 1.6f
                sunColor[0] = 0.15f; sunColor[1] = 0.2f; sunColor[2] = 0.35f
                ambientColor[0] = 0.12f; ambientColor[1] = 0.14f; ambientColor[2] = 0.22f
                fogColor[0] = 0.04f; fogColor[1] = 0.06f; fogColor[2] = 0.12f
            }
            // Dawn (05:00 - 07:00)
            timeOfDay in 5f..7f -> {
                val t = (timeOfDay - 5f) / 2f
                areCityLightsOn = t < 0.5f
                emissiveMultiplier = 1.6f * (1f - t) + 0.3f
                sunColor[0] = 0.95f; sunColor[1] = 0.65f; sunColor[2] = 0.4f
                ambientColor[0] = 0.35f; ambientColor[1] = 0.32f; ambientColor[2] = 0.38f
                fogColor[0] = 0.35f; fogColor[1] = 0.25f; fogColor[2] = 0.28f
            }
            // Day (07:00 - 18:00)
            timeOfDay in 7f..18f -> {
                areCityLightsOn = false
                emissiveMultiplier = 0.2f
                sunColor[0] = 1.0f; sunColor[1] = 0.98f; sunColor[2] = 0.92f
                ambientColor[0] = 0.55f; ambientColor[1] = 0.58f; ambientColor[2] = 0.62f
                fogColor[0] = 0.48f; fogColor[1] = 0.55f; fogColor[2] = 0.65f
            }
            // Sunset / Dusk (18:00 - 21:00) - The Iconic "City After Dark" look!
            else -> {
                val t = (timeOfDay - 18f) / 3f
                areCityLightsOn = t > 0.3f
                emissiveMultiplier = 0.3f + t * 1.3f
                sunColor[0] = 0.98f; sunColor[1] = 0.45f; sunColor[2] = 0.2f
                ambientColor[0] = 0.28f; ambientColor[1] = 0.22f; ambientColor[2] = 0.32f
                fogColor[0] = 0.25f; fogColor[1] = 0.14f; fogColor[2] = 0.22f
            }
        }

        // Weather adjustments
        when (weather) {
            WeatherType.CLEAR -> {
                wetness = 0.15f
                fogDensity = 0.75f
                fogNear = 50f
                fogFar = 280f
            }
            WeatherType.CLOUDY -> {
                ambientColor[0] *= 0.8f; ambientColor[1] *= 0.8f; ambientColor[2] *= 0.8f
                fogDensity = 0.9f
                fogNear = 40f
                fogFar = 230f
            }
            WeatherType.RAIN -> {
                wetness = 0.85f // Reflective asphalt
                ambientColor[0] *= 0.7f; ambientColor[1] *= 0.75f; ambientColor[2] *= 0.85f
                fogDensity = 1.2f
                fogNear = 25f
                fogFar = 180f
            }
            WeatherType.FOG -> {
                wetness = 0.4f
                fogDensity = 1.6f
                fogNear = 15f
                fogFar = 120f
            }
        }
    }

    fun getFormattedTime(): String {
        val totalMinutes = (timeOfDay * 60).toInt()
        val hours = (totalMinutes / 60) % 24
        val minutes = totalMinutes % 60
        val ampm = if (hours >= 12) "PM" else "AM"
        val displayHours = if (hours % 12 == 0) 12 else hours % 12
        return String.format("%d:%02d %s", displayHours, minutes, ampm)
    }
}
