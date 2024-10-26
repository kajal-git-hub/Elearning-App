package xyz.penpencil.competishun.download

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class DownloaderManager private constructor(
    private val context: Context,
    private val url: String,
    private val filePath: String,
    private val fileName: String
) {

    companion object {
        const val CHANNEL_ID = "DownloadNotificationChannel"
        const val REQUEST_NOTIFICATION_PERMISSION = 1
    }

    private fun startDownload() {
        val downloadData = Data.Builder()
            .putString("url", url)
            .putString("filePath", filePath)
            .putString("fileName", fileName)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(downloadData)
            .build()

        WorkManager.getInstance(context).enqueue(downloadRequest)
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.areNotificationsEnabled()) {
                startDownload()
            } else {
                // If permission is not granted, request it
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        } else {
            startDownload()
        }
    }

    class Builder(private val context: Context) {
        private var url: String = ""
        private var filePath: String = ""
        private var fileName: String = ""

        fun setUrl(url: String) = apply { this.url = url }
        fun setFilePath(filePath: String) = apply { this.filePath = filePath }
        fun setFileName(fileName: String) = apply { this.fileName = fileName }

        fun build(): DownloaderManager {
            val manager = DownloaderManager(context, url, filePath, fileName)
            manager.checkAndRequestNotificationPermission()
            return manager
        }
    }
}
