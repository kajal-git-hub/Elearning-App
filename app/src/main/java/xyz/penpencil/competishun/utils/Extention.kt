package xyz.penpencil.competishun.utils

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.Serializable

fun Window.setLightStatusBars(isLight: Boolean) {
    WindowCompat.getInsetsController(this, this.decorView)?.let { controller ->
        controller.isAppearanceLightStatusBars = isLight
    }
}


fun Activity.immerseMode(isEnabled:  Boolean){
    if (isEnabled){
        window?.let { window ->
            ViewCompat.getWindowInsetsController(window.decorView)?.let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }else{
        window?.let { window ->
            ViewCompat.getWindowInsetsController(window.decorView)?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}



inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}