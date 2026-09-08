package com.example.game.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class GameAudioEngine {
    private var isRunning = false
    private var engineSpeedRatio = 0f
    private var isDriving = false
    private var isRaining = false
    private var audioTrack: AudioTrack? = null
    private var audioThread: Thread? = null

    var masterVolume: Float = 0.8f
    var isMuted: Boolean = false

    fun start() {
        if (isRunning) return
        isRunning = true

        val sampleRate = 22050
        try {
            var bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ) * 2
            if (bufferSize <= 0) bufferSize = 4096

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
                audioTrack?.play()
            }
        } catch (_: Throwable) {
            audioTrack = null
        }

        audioThread = thread(start = true, name = "GameAudioThread") {
            val buffer = ShortArray(512)
            var phaseEngine = 0.0
            var phaseCity = 0.0
            val random = Random(999L)

            while (isRunning) {
                if (isMuted || masterVolume <= 0.01f) {
                    buffer.fill(0)
                } else {
                    // Engine frequency: 65 Hz idle up to 280 Hz at high speed
                    val engineFreq = if (isDriving) 65.0 + engineSpeedRatio * 220.0 else 0.0
                    val engineStep = (2.0 * PI * engineFreq) / sampleRate

                    // Ambient city hum (low 48 Hz rumble)
                    val cityStep = (2.0 * PI * 48.0) / sampleRate

                    for (i in buffer.indices) {
                        var sample = 0.0

                        // Engine audio (harmonics)
                        if (isDriving) {
                            phaseEngine += engineStep
                            if (phaseEngine > 2.0 * PI) phaseEngine -= 2.0 * PI
                            val fundamental = sin(phaseEngine)
                            val secondHarmonic = sin(phaseEngine * 2.0) * 0.45
                            val subHarmonic = sin(phaseEngine * 0.5) * 0.25
                            sample += (fundamental + secondHarmonic + subHarmonic) * 0.38
                        }

                        // City ambient background hum
                        phaseCity += cityStep
                        if (phaseCity > 2.0 * PI) phaseCity -= 2.0 * PI
                        sample += sin(phaseCity) * 0.06

                        // Rain white noise if raining
                        if (isRaining) {
                            sample += (random.nextFloat() * 2f - 1f) * 0.08
                        }

                        // Apply master volume
                        val finalSample = (sample * masterVolume * 32767.0).coerceIn(-32767.0, 32767.0)
                        buffer[i] = finalSample.toInt().toShort()
                    }
                }

                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    fun updateVehicleAudio(isPlayerDriving: Boolean, speedRatio: Float) {
        this.isDriving = isPlayerDriving
        this.engineSpeedRatio = speedRatio.coerceIn(0f, 1f)
    }

    fun setWeatherRaining(raining: Boolean) {
        this.isRaining = raining
    }

    fun playClickSound() {
        if (isMuted) return
        thread {
            try {
                val sampleRate = 22050
                val samples = 350
                val buf = ShortArray(samples)
                for (i in buf.indices) {
                    val s = sin((i.toDouble() / sampleRate) * 2.0 * PI * 880.0) * (1.0 - i.toDouble() / samples)
                    buf[i] = (s * masterVolume * 22000).toInt().toShort()
                }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
                    .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setBufferSizeInBytes(buf.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buf, 0, buf.size)
                track.play()
            } catch (_: Exception) {}
        }
    }

    fun playFanfareSound() {
        if (isMuted) return
        thread {
            try {
                val sampleRate = 22050
                val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
                val noteLen = 1400
                val buf = ShortArray(notes.size * noteLen)
                var idx = 0
                for (freq in notes) {
                    for (i in 0 until noteLen) {
                        val s = sin((i.toDouble() / sampleRate) * 2.0 * PI * freq) * 0.6
                        buf[idx++] = (s * masterVolume * 24000).toInt().toShort()
                    }
                }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
                    .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setBufferSizeInBytes(buf.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buf, 0, buf.size)
                track.play()
            } catch (_: Exception) {}
        }
    }

    fun stop() {
        isRunning = false
        try {
            audioThread?.join(300)
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
        audioThread = null
    }
}
