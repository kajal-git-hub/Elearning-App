package xyz.penpencil.competishun.utils

import android.text.Spanned
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @BindingAdapter(value = ["setTextViewHtml"], requireAll = false)
    @JvmStatic
    fun setTextHtml(view: TextView, str:String?){
        try {
            val spannedHtml: Spanned = HtmlCompat.fromHtml(str?.trim()?:"", HtmlCompat.FROM_HTML_MODE_LEGACY)

            // Set the formatted text in the TextView
            view.text = spannedHtml
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }
}