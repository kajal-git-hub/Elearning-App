package com.student.competishun.utils

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R

class HelperFunctions {
    fun setupDotsIndicator(context: Context, itemCount: Int, dotsIndicator: LinearLayout) {
        dotsIndicator.removeAllViews()
        for (i in 0 until itemCount) {
            val dot = ImageView(context)
            dot.setImageResource(R.drawable.dot_inactive)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dot.layoutParams = params
            dotsIndicator.addView(dot)
        }
        updateDotsIndicator(null, dotsIndicator)
    }

    fun updateDotsIndicator(recyclerView: RecyclerView?, dotsIndicator: LinearLayout) {
        recyclerView?.let {
            val layoutManager = it.layoutManager as LinearLayoutManager
            val visiblePosition = (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2

            for (i in 0 until dotsIndicator.childCount) {
                val dot = dotsIndicator.getChildAt(i) as ImageView
                val size = 24
                val params = LinearLayout.LayoutParams(size, size)
                params.setMargins(4, 0, 4, 0)
                dot.layoutParams = params
                dot.setImageResource(
                    if (i == visiblePosition) R.drawable.doc_active
                    else R.drawable.dot_inactive
                )
            }
        }
    }
}
