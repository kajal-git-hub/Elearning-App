package xyz.penpencil.competishun.utils

import xyz.penpencil.competishun.data.model.RecommendedCourseDataModel
import xyz.penpencil.competishun.data.model.Testimonial
import xyz.penpencil.competishun.data.model.WhyCompetishun

object Constants {
    // Define each list item as a constant
    const val IIT_JEE = "IIT-JEE"
    const val NEET = "NEET"
    const val BOARD = "Board"
    const val UCET = "CUET"
    const val OTHERS = "Others"
    const val RazorpayKeyId_Prod = "rzp_live_7Hx1eP9SZPlJYE"
    const val RazorpayKeyId = "rzp_test_DcVrk6NysFj71r"
    const val GoogleClientId = "887693153546-mv6cfeppj49al2c2bdpainrh6begq6bi.apps.googleusercontent.com"
    const val GoogleClientId_Prod = "484629070442-58dgnt04ppq6b2rc2i1vnu1eoclu6248.apps.googleusercontent.com"
    const val YEAR_2025 = "2025"
    const val YEAR_2026 = "2026"
    const val YEAR_2027 = "2027"
    const val YEAR_2028 = "2028"

    const val FRIENDS_FAMILY = "Family/Friends"
    const val SOCIAL_MEDIA = "Social Media"
    const val ADVERTISEMENT = "Advertisement"
    const val COMPETISHUN_STUDENT = "Competishun Student"
    const val YOUTUBE_CHANNEL = "YouTube Channel"
    const val INSTAGRAM_FACEBOOK = "Instagram/Facebook"
    const val INTERNET_EMAIL_SMS = "Internet/Email/SMS"
    const val SEMINAR_WEBINARS = "Seminars/Webinars"
    const val SCHOOL_TEACHER = "School/Teacher"
    const val GOOGLE_SEARCH = "Google Search"
    const val NEWSPAPER_MAGAZINE = "Newspaper/Magazine"
    const val WHATSAPP_TELEGRAM = "WhatsApp/Telegram"
    const val OTHER = "Other"

    // Define data sets as lists of constants
    val DATA_SETS = listOf(
        listOf(IIT_JEE, NEET, BOARD, UCET, OTHERS),
        listOf(YEAR_2025, YEAR_2026, YEAR_2027, YEAR_2028),
        listOf(FRIENDS_FAMILY, COMPETISHUN_STUDENT, YOUTUBE_CHANNEL, INSTAGRAM_FACEBOOK, GOOGLE_SEARCH, INTERNET_EMAIL_SMS,
            SEMINAR_WEBINARS, SCHOOL_TEACHER, NEWSPAPER_MAGAZINE, WHATSAPP_TELEGRAM, ADVERTISEMENT, OTHER) )

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

    val OTHER_REQUIREMENT_FIELDS = listOf("AADHAR_CARD", "PASSPORT_SIZE_PHOTO", "ALL", "FATHERS_NAME", "FULL_ADDRESS", "T_SHIRTS", "WHATSAPP_NUMBER")

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
            "https://youtu.be/06dJv3gJX88"
        ),
        WhyCompetishun(
            "Competishun",
            "IIT - JEE Cracked",
            "NEET Cracked",
            "https://youtu.be/l2tLkv_t15k"
        ),
        WhyCompetishun(
            "Competishun",
            "IIT - JEE Cracked",
            "NEET Cracked",
            "https://youtu.be/50hiqJY_8h0"
        ),
    )

    val testimonials = listOf(
        Testimonial(
            "I Joined, Competishun because of Mohit Tyagi Sir and their best academic team. I loved their lectures  on YouTube so, I bought the course as well.  Their content, Test structure are so well designed that helped me in achieving my goal to IIT.",
            "NIHAR HINGRAJIA",
            "JEE ADVANCED – 2022",
            "AIR : 56"
        ),
        Testimonial(
            " I found out about Team Competishun over Youtube and I am glad that I joined their JEE Main & Adv. coaching classes. They helped me achieve really good results. They are really cost-effective, have good teachers and are always on-time. Thank you Team Competishun.",
            "Harshit Srivastava",
            "JEE ADVANCED – 2022",
            "AIR : 215"
        ),
        Testimonial(
            "Taking Online Course right here at Competishun has been a prime gain to me. The courses are well laid out and the faculties are supportive and responsible towards each doubt I asked. I want to devote my success to Team Competishun & Thank you for assisting me out in attaining my preferred goal.",
            "RAJAT GOLECHHA",
            "JEE ADVANCED – 2021",
            "AIR : 100"
        ),
        Testimonial(
            "I discovered Team Competishun through YouTube and later on I joined Competishun Online Classes, and I am delighted that I enrolled in their JEE Main & Adv. coaching classes. Their assistance played a vital role In my outstanding results and achieving AIR 811 is all because of guidance by top faculties' of Competishun. The classes are cost-effective, led by excellent teachers, and consistently punctual, their test series, doubt clearance facilities are very good. I express my gratitude to Team Competishun for their support and guidance.",
            "Dhurv Gupta",
            "",
            "AIR 811"
        ),
        Testimonial(
            "I have studies my class 11th & class 12th entirely from Team competishun and from my experience with Team competishun, I can say with certainty that they are great at guiding, helping you prepare, and boosting your confidence. Their scheduled zoom session for doubt clearance, test series and most important study material helped me in clearly concepts. Specially Mohit Tyagi Sir helped me in personal guidance. If you're determined to reach your goal, I'm sure Team Competishun will do everything they can to support you in acing your IIT objectives.",
            "Vansh Agarwal",
            "",
            "AlR-185"
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
