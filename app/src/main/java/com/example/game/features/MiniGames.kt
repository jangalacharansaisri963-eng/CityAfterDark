package com.example.game.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

class DriftScoreTracker {
    var isDrifting by mutableStateOf(false)
    var currentDriftScore by mutableFloatStateOf(0f)
    var driftMultiplier by mutableFloatStateOf(1.0f)
    var lastAwardedCash by mutableIntStateOf(0)
    var stuntNotification by mutableStateOf<String?>(null)

    fun recordDrift(speedKmh: Float, isHandbraking: Boolean, steerInput: Float, deltaTime: Float): Int {
        if (speedKmh > 35f && (isHandbraking || kotlin.math.abs(steerInput) > 0.6f)) {
            isDrifting = true
            driftMultiplier = (driftMultiplier + deltaTime * 0.4f).coerceAtMost(5.0f)
            currentDriftScore += speedKmh * 1.5f * driftMultiplier * deltaTime
            return 0
        } else {
            if (isDrifting && currentDriftScore > 80f) {
                val cashEarned = (currentDriftScore * 0.08f * driftMultiplier).toInt().coerceAtLeast(25)
                lastAwardedCash = cashEarned
                stuntNotification = "STUNT DRIFT: +$$cashEarned (${currentDriftScore.toInt()} PTS x${"%.1f".format(driftMultiplier)})"
                currentDriftScore = 0f
                driftMultiplier = 1.0f
                isDrifting = false
                return cashEarned
            }
            isDrifting = false
            currentDriftScore = 0f
            driftMultiplier = 1.0f
            return 0
        }
    }
}

class AtmHackGame {
    var targetCode by mutableStateOf("4829")
    var currentGuess by mutableStateOf("")
    var attemptsLeft by mutableIntStateOf(3)
    var feedbackMessage by mutableStateOf("Enter 4-Digit Security Bypass Key")
    var isSuccess by mutableStateOf(false)
    var isFailed by mutableStateOf(false)

    fun startNewHack() {
        val d1 = Random.nextInt(1, 9)
        val d2 = Random.nextInt(1, 9)
        val d3 = Random.nextInt(1, 9)
        val d4 = Random.nextInt(1, 9)
        targetCode = "$d1$d2$d3$d4"
        currentGuess = ""
        attemptsLeft = 3
        feedbackMessage = "Hint: First digit is $d1, Last digit is $d4"
        isSuccess = false
        isFailed = false
    }

    fun enterDigit(digit: Int) {
        if (isSuccess || isFailed) return
        if (currentGuess.length < 4) {
            currentGuess += digit.toString()
        }
    }

    fun deleteDigit() {
        if (currentGuess.isNotEmpty()) {
            currentGuess = currentGuess.dropLast(1)
        }
    }

    fun submitGuess(): Boolean {
        if (currentGuess.length != 4) {
            feedbackMessage = "Code must be 4 digits!"
            return false
        }
        if (currentGuess == targetCode) {
            isSuccess = true
            feedbackMessage = "ACCESS GRANTED! Transferring $2,850..."
            return true
        } else {
            attemptsLeft--
            currentGuess = ""
            if (attemptsLeft <= 0) {
                isFailed = true
                feedbackMessage = "SECURITY LOCKDOWN! Alarm triggered!"
            } else {
                feedbackMessage = "INCORRECT! $attemptsLeft attempts remaining."
            }
            return false
        }
    }
}
