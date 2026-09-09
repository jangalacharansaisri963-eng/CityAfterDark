package com.example.game.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WantedSystem {
    var wantedStars by mutableIntStateOf(0)
    var heatPoints by mutableFloatStateOf(0f)
    var isEvadingPolice by mutableStateOf(false)
    var evasionTimer by mutableFloatStateOf(0f)
    var policeRadioChatter by mutableStateOf<String?>(null)
    var policeRadioMessage: String?
        get() = policeRadioChatter
        set(v) { policeRadioChatter = v }

    fun addHeat(amount: Float) {
        heatPoints = (heatPoints + amount).coerceIn(0f, 500f)
        val newStars = when {
            heatPoints >= 400f -> 5
            heatPoints >= 300f -> 4
            heatPoints >= 200f -> 3
            heatPoints >= 100f -> 2
            heatPoints >= 35f -> 1
            else -> 0
        }
        if (newStars > wantedStars) {
            wantedStars = newStars
            isEvadingPolice = false
            evasionTimer = 18f
            policeRadioChatter = when (wantedStars) {
                1 -> "Dispatch: 10-4, minor infraction reported in area."
                2 -> "Dispatch: All units, suspect is resisting. Proceed with caution."
                3 -> "Dispatch: Code 3 pursuit authorized. Deploy spike strips."
                4 -> "Dispatch: SWAT teams mobilizing, target is heavily armed!"
                5 -> "Dispatch: Maximum alert, air support inbound!"
                else -> null
            }
        }
    }

    fun update(deltaTime: Float, isHiddenOrFar: Boolean) {
        if (wantedStars == 0) return

        if (isHiddenOrFar) {
            isEvadingPolice = true
            evasionTimer -= deltaTime
            if (evasionTimer <= 0f) {
                // Cooldown complete, reduce heat
                wantedStars = (wantedStars - 1).coerceAtLeast(0)
                heatPoints = (wantedStars * 80f).coerceAtLeast(0f)
                if (wantedStars > 0) {
                    evasionTimer = 15f
                    policeRadioChatter = "Dispatch: Lost visual on suspect. Maintain perimeter."
                } else {
                    isEvadingPolice = false
                    policeRadioChatter = "Dispatch: Suspect lost. Return to normal patrol."
                }
            }
        } else {
            isEvadingPolice = false
            evasionTimer = 18f + wantedStars * 4f
        }
    }

    fun clearWantedLevel() {
        wantedStars = 0
        heatPoints = 0f
        isEvadingPolice = false
        evasionTimer = 0f
        policeRadioChatter = "Heat cleared. Safehouse protection active."
    }
}
