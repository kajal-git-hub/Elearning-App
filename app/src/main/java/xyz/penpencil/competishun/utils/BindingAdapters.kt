package xyz.penpencil.competishun.utils

import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @BindingAdapter(value = ["setTextViewHtml"], requireAll = false)
    @JvmStatic
    fun setTextHtml(view: TextView, str: String?) {
        try {
            val spannedHtml: Spanned = HtmlCompat.fromHtml(str?.trim() ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)

            view.text = spannedHtml

            view.movementMethod = LinkMovementMethod.getInstance()

            Linkify.addLinks(view.text as Spannable, Linkify.ALL)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
