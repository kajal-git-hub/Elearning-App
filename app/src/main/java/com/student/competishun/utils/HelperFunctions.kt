package com.student.competishun.utils

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.student.competishun.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HelperFunctions {
    fun setupDotsIndicator(context: Context,itemCount: Int, dotsIndicator: LinearLayout) {
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
                val size = when (Math.abs(i - visiblePosition)) {
                    0 -> 24
                    1 -> 20
                    2 -> 16
                    else -> 12
                }
                val params = LinearLayout.LayoutParams(size, size)
                params.setMargins(4, 0, 4, 0)
                dot.layoutParams = params
                dot.setImageResource(
                    if (i == visiblePosition) R.drawable.dot_active
                    else R.drawable.dot_inactive
                )
            }
        }
    }


    fun formatCourseDate(date: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // Assuming the input date is in UTC
        }
        val outputFormat = SimpleDateFormat("dd MMM, yy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set the output time zone to IST
        }
        return try {
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: return "-")
        } catch (e: Exception) {
            e.printStackTrace()
            "-"
        }
    }

    fun calculateDiscountPercentage(price: Int, discountPrice: Int): Double {
        if (price <= 0) {
            throw IllegalArgumentException("Price must be greater than 0")
        }
        return ((price - discountPrice).toDouble() / price) * 100
    }




    fun calculateDiscountDetails(originalPrice: Double, discountPrice: Double): Pair<Int, Int> {
        val discountPercentage = ((discountPrice / originalPrice) * 100).toInt()
        val realPriceAfterDiscount = (originalPrice - discountPrice).toInt()

        return Pair(discountPercentage, realPriceAfterDiscount)
    }


    fun downloadPdf(context: Context,fileUrl: String, title: String) {
         val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
         val request = DownloadManager.Request(Uri.parse(fileUrl))
             .setTitle(title)
             .setDescription("Downloading PDF...")
             .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
             .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.pdf")

         downloadManager.enqueue(request)
    }


    fun toDisplayString(classname: String?): String {
        return when (classname) {
            "ELEVENTH" -> "11th"
            "TWELFTH" -> "12th"
            "TWELFTH_PLUS" -> "12th+"
            "UNKNOWN" -> "Unknown"
            else -> "Unknown"
        }
    }

    fun showDownloadDialog(context: Context,fileUrl: String, title: String) {
        AlertDialog.Builder(context)
            .setTitle("Download PDF")
            .setMessage("Do you want to download $title?")
            .setPositiveButton("Yes") { _, _ ->
                // Call the helper function to download the PDF
               downloadPdf(context, fileUrl, title)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
