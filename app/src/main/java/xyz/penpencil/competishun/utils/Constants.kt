package xyz.penpencil.competishun.utils

import xyz.penpencil.competishun.data.model.RecommendedCourseDataModel
import xyz.penpencil.competishun.data.model.Testimonial
import xyz.penpencil.competishun.data.model.WhyCompetishun

object Constants {
    // Define each list item as a constant
    const val IIT_JEE = "IIT-JEE"
    const val NEET = "NEET"
    const val BOARD = "Board"
    const val UCET = "UCET"
    const val OTHERS = "Others"

    const val YEAR_2025 = "2025"
    const val YEAR_2026 = "2026"
    const val YEAR_2027 = "2027"
    const val YEAR_2028 = "2028"

    const val FRIENDS_FAMILY = "Family/Friends"
    const val SOCIAL_MEDIA = "Social Media"
    const val ADVERTISEMENT = "Advertisement"
    const val OTHER = "Other"

    // Define data sets as lists of constants
    val DATA_SETS = listOf(
        listOf(IIT_JEE, NEET, BOARD, UCET, OTHERS),
        listOf(YEAR_2025, YEAR_2026, YEAR_2027, YEAR_2028),
        listOf(SOCIAL_MEDIA, ADVERTISEMENT, FRIENDS_FAMILY, OTHER)
    )

    // Define page texts as constants
    const val PAGE_TEXT_1 = "2"
    const val PAGE_TEXT_2 = "3"
    const val PAGE_TEXT_3 = "4"

    val PAGE_TEXTS = listOf(PAGE_TEXT_1, PAGE_TEXT_2, PAGE_TEXT_3)

    // Define step texts as constants
    const val STEP_TEXT_1 = "Which exam are you \npreparing for? Please select"
    const val STEP_TEXT_2 = "What is your target year?"
    const val STEP_TEXT_3 = "How do you know about \nCompetishun?"

    val STEP_TEXTS = listOf(STEP_TEXT_1, STEP_TEXT_2, STEP_TEXT_3)

    val recommendedCourseList = listOf(
        RecommendedCourseDataModel(
            discount = "11% OFF",
            courseName = "Prakhar Integrated (Fast Lane-2) 2024-25",
            tag1 = "12th Class",
            tag2 = "Full-Year",
            tag3 = "Target 2025",
            startDate = "Starts On: 01 Jul, 24",
            endDate = "Expiry Date: 31 Jul, 24",
            lectureCount = "Lectures: 56",
            quizCount = "Quiz & Tests: 120",
            originalPrice = "₹44,939",
            discountPrice = "₹29,900"
        ),
        RecommendedCourseDataModel(
            discount = "11% OFF",
            courseName = "Prakhar Integrated (Fast Lane-2) 2024-25",
            tag1 = "12th Class",
            tag2 = "Full-Year",
            tag3 = "Target 2025",
            startDate = "Starts On: 01 Jul, 24",
            endDate = "Expiry Date: 31 Jul, 24",
            lectureCount = "Lectures: 56",
            quizCount = "Quiz & Tests: 120",
            originalPrice = "₹44,939",
            discountPrice = "₹29,900"
        ),
        RecommendedCourseDataModel(
            discount = "11% OFF",
            courseName = "Prakhar Integrated (Fast Lane-2) 2024-25",
            tag1 = "12th Class",
            tag2 = "Full-Year",
            tag3 = "Target 2025",
            startDate = "Starts On: 01 Jul, 24",
            endDate = "Expiry Date: 31 Jul, 24",
            lectureCount = "Lectures: 56",
            quizCount = "Quiz & Tests: 120",
            originalPrice = "₹44,939",
            discountPrice = "₹29,900"
        )
    )

    val listWhyCompetishun = listOf(
        WhyCompetishun(
            "Competishun",
            "IIT - JEE Cracked",
            "NEET Cracked",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ),
        WhyCompetishun(
            "Competishun",
            "IIT - JEE Cracked",
            "NEET Cracked",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        ),
        WhyCompetishun(
            "Competishun",
            "IIT - JEE Cracked",
            "NEET Cracked",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
        ),
        WhyCompetishun(
            "Competishun",
            "IIT - JEE Cracked",
            "NEET Cracked",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        )
    )

    val testimonials = listOf(
        Testimonial(
            "The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.",
            "Aman Sharma",
            "Class: 12th",
            "IIT JEE"
        ),
        Testimonial(
            "The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.",
            "Aman Sharma",
            "Class: 12th",
            "IIT JEE"
        ),
        Testimonial(
            "The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.",
            "Aman Sharma",
            "Class: 12th",
            "IIT JEE"
        ),
        Testimonial(
            "The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.",
            "Aman Sharma",
            "Class: 12th",
            "IIT JEE"
        ),
        Testimonial(
            "The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.",
            "Aman Sharma",
            "Class: 12th",
            "IIT JEE"
        )
    )
//    val items = mutableListOf(
//        OurContentItem.FirstItem(
//            OurContentFirstItem(
//                R.drawable.frame_1707480918,
//                "Demo Resources",
//                R.drawable.group_1272628768
//            )
//        ),
//        OurContentItem.OtherItem(
//            OtherContentItem(
//                R.drawable.frame_1707480918,
//                "Preparation Mantra",
//                R.drawable.lock
//            )
//        ),
//        OurContentItem.OtherItem(
//            OtherContentItem(
//                R.drawable.frame_1707480918,
//                "Lectures",
//                R.drawable.lock
//            )
//        ),
//        OurContentItem.OtherItem(
//            OtherContentItem(
//                R.drawable.frame_1707480918,
//                "Tests",
//                R.drawable.lock
//            )
//        ),
//        OurContentItem.OtherItem(
//            OtherContentItem(
//                R.drawable.frame_1707480918,
//                "Study Materials",
//                R.drawable.lock
//            )
//        ),
//    )


// subject content fragment bottom constraint
//
//    <androidx.constraintlayout.widget.ConstraintLayout
//    android:id="@+id/clRunningCourseReminder"
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:background="@color/white"
//    android:visibility="gone"
//    app:layout_constraintBottom_toBottomOf="parent"
//    app:layout_constraintEnd_toEndOf="parent"
//    app:layout_constraintStart_toStartOf="parent">
//
//    <!-- Progress Bar -->
//    <ProgressBar
//    android:id="@+id/customProgressIndicatorRunningCourseReminder"
//    style="@style/Widget.AppCompat.ProgressBar.Horizontal"
//    android:layout_width="match_parent"
//    android:layout_height="8dp"
//    android:max="100"
//    android:progress="50"
//    android:progressDrawable="@drawable/progress_bar_reminder"
//    app:layout_constraintEnd_toEndOf="parent"
//    app:layout_constraintStart_toStartOf="parent"
//    app:layout_constraintTop_toTopOf="parent" />
//
//    <!-- Running Course Icons and Texts -->
//    <ImageView
//    android:id="@+id/IconRunningCourseReminder"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:layout_marginVertical="13dp"
//    android:layout_marginStart="@dimen/_16dp"
//    android:src="@drawable/layer_1"
//    app:layout_constraintBottom_toBottomOf="parent"
//    app:layout_constraintStart_toStartOf="parent"
//    app:layout_constraintTop_toBottomOf="@id/customProgressIndicatorRunningCourseReminder" />
//
//    <com.google.android.material.textview.MaterialTextView
//    android:id="@+id/tvRunningCourseName"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:layout_marginStart="12dp"
//    android:layout_marginTop="13dp"
//    android:fontFamily="@font/nunito"
//    android:text="Binomial Theorem"
//    android:textColor="#2B2829"
//    android:textSize="14sp"
//    android:textStyle="bold"
//    app:layout_constraintStart_toEndOf="@+id/IconRunningCourseReminder"
//    app:layout_constraintTop_toBottomOf="@+id/customProgressIndicatorRunningCourseReminder" />
//
//    <!-- Other UI elements inside the ConstraintLayout -->
//    <!-- .... -->
//    </androidx.constraintlayout.widget.ConstraintLayout>
//
//


//    private fun downloadFile(details: TopicContentModel) {
//        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val uri = Uri.parse(details.url)
//        val request = DownloadManager.Request(uri)
//
//        request.setTitle(details.topicName)
//        request.setDescription(details.topicDescription)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, details.topicName + "." + details.fileType.lowercase())
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setAllowedOverMetered(true)
//
//        val downloadId = downloadManager.enqueue(request)
//        Log.d("DownloadManager", "Download started with ID: $downloadId")
//
//        requireContext().registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//                if (id == downloadId) {
//                    Log.d("DownloadManager", "Download completed")
//                    val query = DownloadManager.Query().setFilterById(downloadId)
//                    val cursor = downloadManager.query(query)
//                    if (cursor.moveToFirst()) {
//                        val filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
//                        Log.d("DownloadManager", "File path: $filePath")
//
//                        // Save file path to SharedPreferences
//                        details.url = filePath
//                        storeItemInPreferences(details)
//                    }
//                    cursor.close()
//                    dismiss()
//                }
//            }
//        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
//    }
//
//

//    private fun deleteDownloadedItem(item: TopicContentModel) {
//        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
//        sharedPreferencesManager.deleteDownloadedItem(item) // Implement this method
//
//        // Remove the file from local storage
//        val fileName = "${item.topicName}.${item.fileType.lowercase()}"
//        val file = File(requireContext().filesDir, fileName)
//        if (file.exists()) {
//            if (file.delete()) {
//                Log.d("DeleteItem", "File deleted successfully: ${file.absolutePath}")
//            } else {
//                Log.e("DeleteItem", "Failed to delete file: ${file.absolutePath}")
//            }
//        } else {
//            Log.w("DeleteItem", "File not found: ${file.absolutePath}")
//        }
//        Toast.makeText(requireContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show()
//    }

}
