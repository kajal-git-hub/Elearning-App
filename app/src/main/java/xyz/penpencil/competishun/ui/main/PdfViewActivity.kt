package xyz.penpencil.competishun.ui.main

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class PdfViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )

        val pdfView = findViewById<com.rajat.pdfviewer.PdfRendererView>(R.id.pdfView)
        val pdfUrl = intent.getStringExtra("PDF_URL")
        val pdfTitle = intent.getStringExtra("PDF_TITLE") ?: "sample"

        if (pdfUrl != null) {
            if (pdfUrl.startsWith("http://") || pdfUrl.startsWith("https://")) {
                // Download PDF from URL and load from local storage
                lifecycleScope.launchWhenStarted {
                    try {
                        val pdfFile = downloadPdfFile(pdfUrl, pdfTitle)
                        loadPdfFromFile(pdfView, pdfFile)
                    } catch (e: Exception) {
                        Toast.makeText(this@PdfViewActivity, "Error loading PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Use PdfViewerActivity for local files directly
                Log.d("local_title",pdfUrl)

                startActivity(PdfViewerActivity.launchPdfFromPath(
                    context = this,
                    path = pdfUrl,
                    pdfTitle = pdfTitle,
                    saveTo = com.rajat.pdfviewer.util.saveTo.DOWNLOADS,
                )).also { finish() }
            }
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
}
