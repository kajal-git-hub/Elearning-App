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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ketch.DownloadModel
import com.ketch.Ketch
import com.ketch.Status
import com.rajat.pdfviewer.PdfViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.penpencil.competishun.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class PdfViewActivity : AppCompatActivity() {

    companion object {
        const val WRITE_PERMISSION_REQUEST_CODE = 1
        const val CHANNEL_ID = "download_channel"
    }

    private var downloadedPdfFile: File? = null  // Hold reference to the downloaded file

    @Inject
    lateinit var ketch: Ketch

    var pdfUrl = ""
    var pdfTitle = ""
    var folderName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        val pdfView = findViewById<com.rajat.pdfviewer.PdfRendererView>(R.id.pdfView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val downloadButton = findViewById<ImageView>(R.id.downloadButton)
        pdfUrl = intent.getStringExtra("PDF_URL") ?: ""
        pdfTitle = intent.getStringExtra("PDF_TITLE") ?: "sample"
        folderName = intent.getStringExtra("FOLDER_NAME")?:""

        Log.d("folderNameGet", folderName)

        if (folderName.contains("DPPs",ignoreCase = true)) {
            downloadButton.visibility = View.VISIBLE
        } else {
            downloadButton.visibility = View.GONE
        }
        Log.d("pdfUrl", pdfUrl)

        if (pdfUrl.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE  // Show the loader initially

            if (pdfUrl.startsWith("http://") || pdfUrl.startsWith("https://")) {
                // Download PDF from URL and load from local storage
                lifecycleScope.launchWhenStarted {
                    try {
                        downloadedPdfFile = downloadPdfFile(pdfUrl, pdfTitle)
                        loadPdfFromFile(pdfView, downloadedPdfFile!!)
//                        downloadButton.visibility = View.VISIBLE  // Show download button after PDF loads
                    } catch (e: Exception) {
                        Toast.makeText(this@PdfViewActivity, "Error loading PDF", Toast.LENGTH_SHORT).show()
                    } finally {
                        progressBar.visibility = View.GONE  // Hide loader
                    }
                }
            } else {
                // Handle local file paths directly
                startActivity(
                    PdfViewerActivity.launchPdfFromPath(
                        context = this,
                        path = pdfUrl,
                        pdfTitle = pdfTitle,
                        saveTo = com.rajat.pdfviewer.util.saveTo.DOWNLOADS,
                    )
                ).also { finish() }
            }
        }

        downloadButton.setOnClickListener {
            savePdfExternally(pdfTitle)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Log.d("onNewIntent", "onNewIntent:")


    }

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

    private fun loadPdfFromFile(pdfView: com.rajat.pdfviewer.PdfRendererView, pdfFile: File) {
        pdfView.initWithFile(file = pdfFile)
    }

    private fun savePdfExternally(pdfTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
                    Log.e("PdfViewActivity", "Error saving PDF: ", e)
                }
            }
        } else {
            checkStoragePermissionAndDownload(pdfTitle)
        }
    }

    private fun showDownloadNotification(fileName: String, fileUri: Uri) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

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
            savePdfToLegacyDownloads(pdfTitle)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            savePdfToLegacyDownloads(intent.getStringExtra("PDF_TITLE") ?: "sample")
        } else {
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePdfToLegacyDownloads(pdfTitle: String) {
        downloadedPdfFile?.let { file ->
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destFile = File(downloadsDir, "$pdfTitle.pdf")

            file.inputStream().use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(this, "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show()

            val uri = FileProvider.getUriForFile(this, "$packageName.provider", destFile)
            showDownloadNotification("$pdfTitle.pdf", uri)
        }
    }
}
