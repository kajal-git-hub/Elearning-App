package xyz.penpencil.competishun.utils

import android.os.SystemClock
import android.view.View

class DoubleClickListener(
    private val doubleClickTime: Long = 300L,
    private val onDoubleClick: (View) -> Unit
) : View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime <= doubleClickTime) {
            onDoubleClick(v)
        }
        lastClickTime = currentTime
    }
}
