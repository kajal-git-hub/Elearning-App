package com.student.competishun.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.student.competishun.R
import com.student.competishun.databinding.FragmentPdfVeiwBinding

class PdfViewFragment : Fragment() {

    private lateinit var binding: FragmentPdfVeiwBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPdfVeiwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUrl = arguments?.getString("pdf_url") ?: return

        // Load PDF from URL using AndroidPdfViewer library
        val uri = Uri.parse(pdfUrl)
//        loadPdfFromUrl(uri)

    }
//    private fun loadPdfFromUrl(pdfUri: Uri) {
//        // Loading the PDF directly from the URL without downloading to local storage
//        binding.pdfView.fromUri(pdfUri)
//            .defaultPage(0)
//            .enableSwipe(true)
//            .swipeHorizontal(false)
//            .enableAnnotationRendering(true)
//            .onLoad { pageCount ->
//                // PDF loaded, handle post load actions if needed
//            }
//            .onError { throwable ->
//                // Handle PDF load error
//                Toast.makeText(requireContext(), "Error loading PDF", Toast.LENGTH_SHORT).show()
//            }
//            .load()
//    }

}