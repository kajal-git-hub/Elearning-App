package com.student.competishun.ui.main

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.student.competishun.R

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdf_viewer)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )
        webView = findViewById(R.id.webView)

        val pdfUrl = intent.getStringExtra("PDF_URL")
        Log.d("pdfUrl",pdfUrl.toString())
        if (pdfUrl != null) {
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
        } else {
            Toast.makeText(this, "PDF URL is missing", Toast.LENGTH_SHORT).show()
        }


    }
}