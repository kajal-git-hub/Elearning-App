package xyz.penpencil.competishun

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.ketch.DownloadConfig
import com.ketch.Ketch
import com.ketch.NotificationConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppController: Application() {

    lateinit var ketch: Ketch

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ketch = Ketch.builder()
            .setDownloadConfig(DownloadConfig())
            .setNotificationConfig(
                NotificationConfig(
                    true,
                    smallIcon = R.drawable.ic_launcher_foreground
                )
            )
            .enableLogs(true)
            .build(applicationContext)
    }
}