package xyz.penpencil.competishun.ui.fragment

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.penpencil.competishun.databinding.FragmentPdfViewerBinding
import java.io.File

class PdfViewerFragment : Fragment() {

    private lateinit var binding : FragmentPdfViewerBinding
    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var currentPage: PdfRenderer.Page


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPdfViewerBinding.inflate(inflater,container,false)

        arguments?.getString("PDF_URL")?.let { pdfPath ->
            val file = File(pdfPath)
            if (file.exists()) {
                renderPdf(file)
            } else {
                // Handle the case where the PDF file does not exist
            }
        }

        return binding.root
    }
    private fun renderPdf(file: File) {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)

        // Open the first page
        currentPage = pdfRenderer.openPage(0)

        val bitmap = Bitmap.createBitmap(
            currentPage.width, currentPage.height,
            Bitmap.Config.ARGB_8888
        )

        // Render the page onto the bitmap
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        // Display the rendered page in the ImageView
        binding.pdfImageView.setImageBitmap(bitmap)

        currentPage.close() // Close the current page after rendering
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer.close() // Close the renderer when the fragment is destroyed
    }

}