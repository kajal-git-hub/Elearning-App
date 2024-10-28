package xyz.penpencil.competishun.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.penpencil.competishun.R
import java.io.FileOutputStream

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "DownloadNotificationChannel"
        const val NOTIFICATION_ID = 78778989
    }

    private val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        createNotificationChannel()
        setForeground(createForegroundInfo())

        return try {
            val url = inputData.getString("url")!!
            val name = inputData.getString("fileName")!!
            val path = inputData.getString("filePath")!!

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("dsdyatsdtyastd", "doWork: failure" )
                    updateNotification(0, 0, 0)
                    return Result.failure()
                }

                val inputStream = response.body?.byteStream() ?: return Result.failure()
                val totalBytes = response.body?.contentLength() ?: -1L
                val outputStream = FileOutputStream(path)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output, totalBytes).collect { downloadInfo ->
                            updateNotification(downloadInfo.progress, 100, downloadInfo.speed)
                        }
                    }
                }
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = createNotification(0, 100, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(progress: Int, maxProgress: Int, speed: Long): Notification {
        val speedText = if (speed > 0) "${speed / 1024} KB/s" else "Calculating..."
        val content = "$progress% at $speedText"

        Log.e("vmnbvbcxvbnxc", "createNotification: ", )

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(content)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)
            .setProgress(maxProgress, progress, false)
            .build()
    }

    private fun updateNotification(progress: Int, maxProgress: Int, speed: Long) {
        val notification = createNotification(progress, maxProgress, speed)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Download...", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Download progress"
        }
        notificationManager.createNotificationChannel(channel)
    }
}

