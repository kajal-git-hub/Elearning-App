package xyz.penpencil.competishun.ui.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.rajat.pdfviewer.PdfViewerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.penpencil.competishun.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class PdfViewActivity : AppCompatActivity() {

    companion object {
        const val WRITE_PERMISSION_REQUEST_CODE = 1
        const val CHANNEL_ID = "download_channel"
    }

    private var downloadedPdfFile: File? = null  // Hold reference to the downloaded file

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )

        val pdfView = findViewById<com.rajat.pdfviewer.PdfRendererView>(R.id.pdfView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val downloadButton = findViewById<ImageView>(R.id.downloadButton)
        val pdfUrl = intent.getStringExtra("PDF_URL")
        val pdfTitle = intent.getStringExtra("PDF_TITLE") ?: "sample"

        if (pdfUrl != null) {
            progressBar.visibility = View.VISIBLE  // Show the loader initially

            if (pdfUrl.startsWith("http://") || pdfUrl.startsWith("https://")) {
                // Download PDF from URL and load from local storage
                lifecycleScope.launchWhenStarted {
                    try {
                        downloadedPdfFile = downloadPdfFile(pdfUrl, pdfTitle)
                        loadPdfFromFile(pdfView, downloadedPdfFile!!)
                        downloadButton.visibility = View.VISIBLE  // Show the download button after PDF loads
                    } catch (e: Exception) {
                        Toast.makeText(this@PdfViewActivity, "Error loading PDF", Toast.LENGTH_SHORT).show()
                    } finally {
                        progressBar.visibility = View.GONE  // Hide loader
                    }
                }
            } else {
                // Handle local file paths directly
                startActivity(PdfViewerActivity.launchPdfFromPath(
                    context = this,
                    path = pdfUrl,
                    pdfTitle = pdfTitle,
                    saveTo = com.rajat.pdfviewer.util.saveTo.DOWNLOADS,
                )).also { finish() }
            }
        }

        // Handle download button click
        downloadButton.setOnClickListener {
            savePdfExternally(pdfTitle)
        }

        // Create the notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    // Download the PDF file from the provided URL
    private suspend fun downloadPdfFile(pdfUrl: String, pdfTitle: String): File {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(pdfUrl).build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw Exception("Failed to download file")

            val pdfFile = File(filesDir, "$pdfTitle.pdf")
            val inputStream: InputStream = response.body?.byteStream() ?: throw Exception("No file content")
            val outputStream = FileOutputStream(pdfFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            pdfFile
        }
    }

    // Load the downloaded PDF into the PdfRendererView
    private fun loadPdfFromFile(pdfView: com.rajat.pdfviewer.PdfRendererView, pdfFile: File) {
        pdfView.initWithFile(file = pdfFile)
    }

    // Save the PDF externally to the Downloads folder
    private fun savePdfExternally(pdfTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+ (Scoped Storage), use MediaStore API to save the file to the Downloads folder
            val resolver = contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$pdfTitle.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                try {
                    val outputStream: OutputStream? = resolver.openOutputStream(it)
                    downloadedPdfFile?.let { file ->
                        outputStream?.use { output ->
                            file.inputStream().use { input ->
                                input.copyTo(output)
                            }
                        }
                    }
                    Toast.makeText(this, "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show()
                    showDownloadNotification("$pdfTitle.pdf", it)  // Pass URI for notification
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show()
                    Log.e("xyz.penpencil.competishun.ui.main.PdfViewActivity", "Error saving PDF: ", e)
                }
            }
        } else {
            // For Android 9 and below, request permission to write to external storage
            checkStoragePermissionAndDownload(pdfTitle)
        }
    }

    // Show download notification with proper file URI
    private fun showDownloadNotification(fileName: String, fileUri: Uri) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to open the PDF file when the notification is clicked
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Grant read permission for the URI

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.download)
            .setContentTitle("Download Complete")
            .setContentText("File saved as $fileName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    // Create notification channel for Android 8.0+
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Download Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for file downloads"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // Check and request permission for Android 9 and below
    private fun checkStoragePermissionAndDownload(pdfTitle: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, save the PDF to the Downloads folder
            savePdfToLegacyDownloads(pdfTitle)
        }
    }

    // Handle permission result for Android 9 and below
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed to save the PDF
            savePdfToLegacyDownloads(intent.getStringExtra("PDF_TITLE") ?: "sample")
        } else {
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Save the PDF to Downloads folder for Android 9 and below
    private fun savePdfToLegacyDownloads(pdfTitle: String) {
        downloadedPdfFile?.let { file ->
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destFile = File(downloadsDir, "$pdfTitle.pdf")

            file.inputStream().use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Notify user that the file is downloaded
            Toast.makeText(this, "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show()

            // Create URI and notification
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", destFile)
            showDownloadNotification("$pdfTitle.pdf", uri)
        }
    }
}
