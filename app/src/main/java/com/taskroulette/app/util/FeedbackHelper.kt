package com.taskroulette.app.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Approximates the web prototype's WebAudio ticks + navigator.vibrate() calls
 * using ToneGenerator (no bundled audio assets) and the platform vibrator.
 */
class FeedbackHelper(context: Context) {

    private val appContext = context.applicationContext

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private var toneGenerator: ToneGenerator? = null

    private fun tones(): ToneGenerator {
        var tg = toneGenerator
        if (tg == null) {
            tg = ToneGenerator(AudioManager.STREAM_MUSIC, 55)
            toneGenerator = tg
        }
        return tg
    }

    fun tick(soundEnabled: Boolean) {
        if (soundEnabled) {
            runCatching { tones().startTone(ToneGenerator.TONE_PROP_BEEP2, 60) }
        }
        vibrateOneShot(8)
    }

    fun reveal(soundEnabled: Boolean) {
        if (soundEnabled) {
            runCatching { tones().startTone(ToneGenerator.TONE_PROP_ACK, 300) }
        }
        vibrateWaveform(longArrayOf(40, 60))
    }

    private fun vibrateOneShot(durationMs: Long) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        }
    }

    private fun vibrateWaveform(timings: LongArray) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
