package com.student.competishun.utils

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import java.text.SimpleDateFormat
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
            val layoutManager = it.layoutManager
            val visiblePageIndex = when (layoutManager) {
                is LinearLayoutManager -> {
                    (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                }

                else -> 0
            }

            for (i in 0 until dotsIndicator.childCount) {
                val dot = dotsIndicator.getChildAt(i) as ImageView
                val size = 24
                val params = LinearLayout.LayoutParams(size, size)
                params.setMargins(4, 0, 4, 0)
                dot.layoutParams = params
                dot.setImageResource(
                    if (i == visiblePageIndex) R.drawable.doc_active
                    else R.drawable.dot_inactive
                )
            }
        }
    }


    fun formatCourseDate(date: String?): String {
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

    fun calculateDiscountDetails(originalPrice: Double, discountPrice: Double): Pair<Double, Double> {
        val discountPercentage = ((discountPrice / originalPrice) * 100)
        val realPriceAfterDiscount = originalPrice - discountPrice

        // Round to two decimal places
        val roundedDiscountPercentage = String.format("%.2f", discountPercentage).toDouble()
        val roundedRealPriceAfterDiscount = String.format("%.2f", realPriceAfterDiscount).toDouble()

        return Pair(roundedDiscountPercentage, roundedRealPriceAfterDiscount)
    }
}
