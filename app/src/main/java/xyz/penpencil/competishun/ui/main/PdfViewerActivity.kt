package xyz.penpencil.competishun.ui.main

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import xyz.penpencil.competishun.R
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.concurrent.thread

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var parcelFileDescriptor: ParcelFileDescriptor
    private lateinit var pdfPageImage: ImageView
    private lateinit var nextPageButton: Button
    private lateinit var previousPageButton: Button
    private var currentPageIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfPageImage = findViewById(R.id.pdfPageImage)
        nextPageButton = findViewById(R.id.nextPageButton)
        previousPageButton = findViewById(R.id.previousPageButton)

        val pdfUrl = intent.getStringExtra("PDF_URL")
        if (pdfUrl != null) {
            thread {
                val pdfFile = if (pdfUrl.startsWith("http://") || pdfUrl.startsWith("https://")) {
                    downloadPdfFile(pdfUrl) // Download from URL
                } else {
                    File(pdfUrl) // Use the local file directly
                }

                if (pdfFile.exists()) {
                    openPdfRenderer(pdfFile)
                    runOnUiThread { showPage(currentPageIndex) }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show()
                        finish() // Close activity if file not found
                    }
                }
            }
        }

        // Set up button listeners for pagination
        nextPageButton.setOnClickListener {
            if (currentPageIndex < pdfRenderer.pageCount - 1) {
                currentPageIndex++
                showPage(currentPageIndex)
            }
        }

        previousPageButton.setOnClickListener {
            if (currentPageIndex > 0) {
                currentPageIndex--
                showPage(currentPageIndex)
            }
        }
    }

    private fun downloadPdfFile(pdfUrl: String): File {
        val url = URL(pdfUrl)
        val connection = url.openConnection()
        connection.connect()

        val inputStream = connection.getInputStream()
        val file = File(cacheDir, "sample.pdf")
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun openPdfRenderer(file: File) {
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(parcelFileDescriptor)
    }

    private fun showPage(pageIndex: Int) {
        if (pdfRenderer.pageCount <= pageIndex) return
        val page = pdfRenderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        pdfPageImage.setImageBitmap(bitmap)
        page.close()

        // Update the buttons based on the current page
        nextPageButton.isEnabled = (pageIndex < pdfRenderer.pageCount - 1)
        previousPageButton.isEnabled = (pageIndex > 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer.close()
        parcelFileDescriptor.close()
    }
}
