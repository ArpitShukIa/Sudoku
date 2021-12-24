package com.arpit.sudoku

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService

fun vibrate(context: Context, duration: Long) {
    val vibrator = context.getSystemService<Vibrator>() ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("deprecation")
        vibrator.vibrate(duration)
    }
}