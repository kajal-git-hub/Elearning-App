package xyz.penpencil.competishun.utils

import android.view.Window
import androidx.core.view.WindowCompat

fun Window.setLightStatusBars(isLight: Boolean) {
    WindowCompat.getInsetsController(this, this.decorView)?.let { controller ->
        controller.isAppearanceLightStatusBars = isLight
    }
}