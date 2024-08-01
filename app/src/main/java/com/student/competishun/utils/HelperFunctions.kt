package com.student.competishun.utils

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    fun formatCourseDate(date: String?):String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM, yy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(date)
            outputFormat.format(date ?: return "No date available")
        } catch (e: Exception) {
            e.printStackTrace()
            "No date available"
        }
    }

    fun calculateDiscountDetails(originalPrice: Int, discountPrice: Int): Pair<Int, Int> {
        val discountPercentage = ((discountPrice.toDouble() / originalPrice.toDouble()) * 100).toInt()
        val realPriceAfterDiscount = originalPrice - discountPrice
        return Pair(discountPercentage, realPriceAfterDiscount)
    }
}
