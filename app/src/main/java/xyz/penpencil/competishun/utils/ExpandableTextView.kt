package xyz.penpencil.competishun.utils


import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ExpandableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        addReadMore()
    }

    private fun addReadMore() {
        val maxLines = 2
        val readMoreText = " Read More"

        if (lineCount > maxLines) {
            val layout: Layout = layout
            val lastLineStart: Int = layout.getLineStart(maxLines - 1)
            val originalText = text.subSequence(0, lastLineStart).toString()
            val textWithReadMore = "$originalText...$readMoreText"

            text = textWithReadMore
        }
    }
}
