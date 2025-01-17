package xyz.penpencil.competishun.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ketch.Ketch
import com.ketch.NotificationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.DialogDownloadOptionBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import java.io.*

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

    //    fun getAllLectureCount(courseId: String, callback: (String, Int) -> Unit){
//
//        studentCoursesViewModel.fetchLectures(courseId)
//        Log.e("getcourseIds",courseId)
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                studentCoursesViewModel.lectures.collect { result ->
//                    result?.onSuccess { lecture ->
//                        val count = lecture.getAllCourseLecturesCount.lecture_count.toInt()
//                        Log.e("lectureCount",count.toString())
//                        callback(courseId, count)
//                    }?.onFailure { exception ->
//                        Log.e("LectureException", exception.toString())
//                    }
//                }
//            }
//        }
//    }



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

    fun formatCourseDateTime(date: String?): String {

        Log.e("dataeis ",date.toString())
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // Assuming the input date is in UTC
        }
        val outputFormat = SimpleDateFormat("dd MMM, yy hh:mm a", Locale.getDefault()).apply {
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

    fun formatCoursesDateTime(date: String?): String {

        Log.e("dataeis ",date.toString())
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val outputFormat = SimpleDateFormat("dd MMM, yy hh:mm a", Locale.getDefault())
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

    private fun encryptFile(inputFile: File, outputFile: File, secretKey: SecretKey) {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        FileInputStream(inputFile).use { fis ->
            FileOutputStream(outputFile).use { fos ->
                CipherOutputStream(fos, cipher).use { cos ->
                    fis.copyTo(cos)
                }
            }
        }
    }

    private fun decryptFile(encryptedFile: File, decryptedFile: File, secretKey: SecretKey) {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        FileInputStream(encryptedFile).use { fis ->
            FileOutputStream(decryptedFile).use { fos ->
                CipherOutputStream(fos, cipher).use { cos ->
                    fis.copyTo(cos)
                }
            }
        }
    }
    fun downloadPdfOld(context: Context, fileUrl: String, title: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(removeBrackets(fileUrl)))
            .setTitle(title)
            .setDescription("Downloading $title...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.pdf")

        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()

        val onCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (id == downloadId) {
                    Toast.makeText(context, "Download successful", Toast.LENGTH_SHORT).show()

                    context.unregisterReceiver(this)
                }
            }
        }

        ContextCompat.registerReceiver(context,onCompleteReceiver,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED )
        Log.e("YUYUYUIYUIY", "downloadPdf: $fileUrl  == $title")
    }


    fun downloadPdf(context: Context, fileUrl: String, title: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(removeBrackets(fileUrl)))
            .setTitle(title)
            .setDescription("Downloading $title...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.pdf")

        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()

        val onCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (id == downloadId) {
                    Toast.makeText(context, "Download successful", Toast.LENGTH_SHORT).show()

                    context.unregisterReceiver(this)
                }
            }
        }


        ContextCompat.registerReceiver(context,onCompleteReceiver,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED )


        Log.e("YUYUYUIYUIY", "downloadPdf: $fileUrl  == $title")

    }

    fun removeBrackets(input: String): String {
        var url = input
        if (input.startsWith("[")){
            url = input.removePrefix("[")
        }

        if (input.endsWith("]")){
            url = url.removeSuffix("]")
        }
        return url
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
               downloadPdf(context, fileUrl, title)
            }
            .setNegativeButton("No", null)
            .show()
    }

    @SuppressLint("RestrictedApi")
    fun showCustomSnackBar(view: View) {
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)

        val customView = LayoutInflater.from(view.context).inflate(R.layout.custom_snackbar_layout, null)

        val snackBarText = customView.findViewById<TextView>(R.id.snackbar_text)
        snackBarText.text = "Question reported successfully!"

        val closeButton = customView.findViewById<ImageView>(R.id.snackbar_close)
        closeButton.setOnClickListener {
            snackBar.dismiss()
        }

        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        snackBarLayout.setPadding(0, 0, 0, 0)
        snackBarLayout.addView(customView, 0)

        snackBar.show()
    }
}
