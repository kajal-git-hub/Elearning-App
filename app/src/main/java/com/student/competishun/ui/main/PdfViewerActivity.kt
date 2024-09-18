package com.student.competishun.ui.main

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.student.competishun.R

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )

        webView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "")
                return true
            }
        }

        val pdfUrl = intent.getStringExtra("PDF_URL")
        Log.d("pdfUrl", pdfUrl.toString())

        if (pdfUrl != null) {
            val encodedUrl = java.net.URLEncoder.encode(pdfUrl, "UTF-8")
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$encodedUrl")
        } else {
            Toast.makeText(this, "PDF URL is missing", Toast.LENGTH_SHORT).show()
        }
    }
}
