package xyz.penpencil.competishun.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.penpencil.competishun.R
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG = "DownloadWorker"

    companion object {
        const val CHANNEL_ID = "DownloadNotificationChannel"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
//        setForegroundAsync(createForegroundInfo(0, 100))
        setForeground(createForegroundInfo(0, 100))

//        setForegroundAsync(foregroundInfo)


        try {
            val url = inputData.getString("url")!!
            val name = inputData.getString("fileName")!!
            val path = inputData.getString("filePath")!!

            val job = CoroutineScope(Dispatchers.IO).async {
                val fileName = "$name.mp4"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Failed to download file: $response")

                    val inputStream: InputStream? = response.body?.byteStream()
                    val totalBytes = response.body?.contentLength() ?: -1L
                    var downloadedBytes = 0L
                    val outputStream = FileOutputStream(path)

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            val buffer = ByteArray(8 * 1024) // Buffer size
                            var bytesRead: Int

                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                downloadedBytes += bytesRead

                                // Update the notification progress
                                if (totalBytes > 0) { // Only update progress if we know the total size
                                    val progress = (downloadedBytes * 100 / totalBytes).toInt()
                                    setForeground(createForegroundInfo(progress, 100))
                                }
                            }

                            Log.d("DownloadVideo", "File downloaded successfully.")
                            return@async Result.success()
                        }
                    }
                }
            }
            job.await()

        } catch (e: Exception) {
            return Result.failure()
        }

        return Result.success()
    }

    private fun createForegroundInfo(progress: Int, maxProgress: Int): ForegroundInfo {
        val notification = createNotification("Download in progress", progress, maxProgress)
        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
        return foregroundInfo
    }

    private fun createNotification(contentText: String, progress: Int, maxProgress: Int): Notification {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "Download", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Downloading")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setProgress(maxProgress, progress, false) // Set progress bar here
            .build()
    }
}

