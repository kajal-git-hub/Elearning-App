package xyz.penpencil.competishun.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

class UrlImageParser(private val container: View, private val context: Context) : Html.ImageGetter {

    private val client = OkHttpClient()

    override fun getDrawable(source: String): Drawable {
        val urlDrawable = UrlDrawable()
        // Fetch the image asynchronously
        fetchImageAsync(urlDrawable, source)
        return urlDrawable
    }

    private fun fetchImageAsync(urlDrawable: UrlDrawable, source: String) {
        // Use lifecycleScope to launch a coroutine that is lifecycle-aware
        if (context is AppCompatActivity) {
            context.lifecycleScope.launch {
                try {
                    val drawable = fetchDrawable(source)
                    drawable?.let {
                        // Update drawable bounds and set the image
                        urlDrawable.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
                        urlDrawable.drawable = it
                        container.invalidate() // Redraw the container
                    }
                } catch (e: Exception) {
                    // Handle the exception, e.g., log it or provide a placeholder image
                }
            }
        }
    }

    private suspend fun fetchDrawable(urlString: String): Drawable? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = fetch(urlString)
                val drawable = Drawable.createFromStream(inputStream, "src")
                drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                drawable
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun fetch(urlString: String): InputStream? {
        val request = Request.Builder().url(urlString).build()
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            response.body?.byteStream()
        } else {
            null
        }
    }

    // Custom Drawable to facilitate refreshing
    inner class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }
    }
}
