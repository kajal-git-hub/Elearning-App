package xyz.penpencil.competishun.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView

class CustomWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    private var isFixedHeight = true

    init {
        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100 && !isFixedHeight) {
                    adjustHeightToContent()
                }
            }
        }
        settings.javaScriptEnabled = true
    }

    fun toggleHeight() {
        isFixedHeight = !isFixedHeight
        if (isFixedHeight) {
            layoutParams.height = dpToPx(20)
        } else {
            adjustHeightToContent()
        }
        requestLayout()
    }

    fun loadStaticHtmlContent(htmlContent: String) {
        loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
    private fun adjustHeightToContent() {
        measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        layoutParams.height = measuredHeight
        requestLayout()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
