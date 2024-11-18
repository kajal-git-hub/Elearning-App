package xyz.penpencil.competishun.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.ItemTopicTypeContentBinding
import xyz.penpencil.competishun.ui.fragment.BottomSheetDownloadBookmark
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.ui.main.YoutubeActivity
import xyz.penpencil.competishun.utils.HelperFunctions
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale


class TopicContentAdapter(
    private var topicContents: MutableList<TopicContentModel>,
    private val folderContentId: String,
    private val folderName: String,
    private val fragmentActivity: FragmentActivity,
    private val context: Context, // Pass context
    private val onItemClick: (TopicContentModel, String, ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>) -> Unit
) :
    RecyclerView.Adapter<TopicContentAdapter.TopicContentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicContentViewHolder {
        val binding = ItemTopicTypeContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopicContentViewHolder(binding, context)
    }

    fun updateData(newTopicContents: MutableList<TopicContentModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            val diffCallback = MyDiffUtilCallback(topicContents, newTopicContents)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            withContext(Dispatchers.Main) {
                topicContents = newTopicContents
                diffResult.dispatchUpdatesTo(this@TopicContentAdapter)
                Log.e("GHJGHJGJGHJ", "updateData: ")
            }
        }
    }

    override fun onBindViewHolder(holder: TopicContentViewHolder, position: Int) {

        val topicContent = topicContents[position]
        holder.bind(topicContents[position], fragmentActivity, folderName,onItemClick)
        Log.e("urlds",topicContent.fileType)
        holder.itemView.setOnClickListener {
            if ((topicContent.fileType == "URL")) {
//                val url =
//                    if (topicContent.topicName.contains("http")) topicContent.url else "https://${topicContent.url}"
//                it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                goToPlayerPage(holder.itemView.context, topicContent.url,topicContent.topicName, topicContent.topicDescription)

            } else if (isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)) {
                val unlockedTopicContentIds = topicContents
                    .filter {
                        isDateTodayOrPast(
                            it.lockTime,
                            topicContent.isExternal
                        ) && it.fileType == "VIDEO"
                    }
                    .map { it.id }.toCollection(ArrayList())
                val unlockedTopicContentNames = topicContents
                    .filter {
                        isDateTodayOrPast(
                            it.lockTime,
                            topicContent.isExternal
                        ) && it.fileType == "VIDEO"
                    }
                    .map { it.topicName }.toCollection(ArrayList())
                val unlockedTopicContentDescs = topicContents
                    .filter {
                        isDateTodayOrPast(
                            it.lockTime,
                            topicContent.isExternal
                        ) && it.fileType == "VIDEO"
                    }
                    .map { it.topicDescription }.toCollection(ArrayList())
                val unlockedTopicContenthomeworks = topicContents
                    .filter {
                        isDateTodayOrPast(
                            it.lockTime,
                            topicContent.isExternal
                        ) && it.fileType == "VIDEO"
                    }
                    .map { it.homeworkName }.toCollection(ArrayList())
                val unlockedTopicContenthomeworkLinks = topicContents
                    .filter {
                        isDateTodayOrPast(
                            it.lockTime,
                            topicContent.isExternal
                        ) && it.fileType == "VIDEO"
                    }
                    .map { it.homeworkUrl }.toCollection(ArrayList())
                val unlockedTopicContenthomeworkDescs = topicContents
                    .filter {
                        isDateTodayOrPast(
                            it.lockTime,
                            topicContent.isExternal
                        ) && it.fileType == "VIDEO"
                    }
                    .map { it.homeworkDesc }.toCollection(ArrayList())

                onItemClick(
                    topicContent,
                    folderContentId,
                    unlockedTopicContentIds,
                    unlockedTopicContentNames,
                    unlockedTopicContentDescs,
                    unlockedTopicContenthomeworks,
                    unlockedTopicContenthomeworkLinks,
                    unlockedTopicContenthomeworkDescs
                )
            } else if (topicContent.fileType == "UNKNOWN__") {
                Log.e("TAG", "onBindViewHolder: ")
            } else {
                Toast.makeText(it.context, "Content is locked!", Toast.LENGTH_SHORT).show()
            }
        }
        if (!isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)) {
            holder.itemView.findViewById<ImageView>(R.id.iv_MoreInfoLec).visibility = View.GONE
        }
    }

    private fun goToPlayerPage(context: Context, videoUrl: String, name: String,desc:String) {
        val intent = Intent(context, YoutubeActivity::class.java).apply {
            putExtra("url", videoUrl)
            putExtra("urlDescription", desc)
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = topicContents.size

    class TopicContentViewHolder(
        private val binding: ItemTopicTypeContentBinding,
        private val context: Context// Use context for download function
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            topicContent: TopicContentModel,
            fragmentActivity: FragmentActivity,
            folderName: String,
            onItemClick: (TopicContentModel, String, ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>) -> Unit
        ) {

            binding.ivSubjectBookIcon.setImageResource(topicContent.subjectIcon)

            var helperFunctions = HelperFunctions()

            binding.ivMoreInfoLec.setOnClickListener {
                val bottomSheet = BottomSheetDownloadBookmark()
                bottomSheet.setItemDetails(topicContent)
                bottomSheet.show(fragmentActivity.supportFragmentManager, bottomSheet.tag)
            }
            Log.e("getlocketime ${topicContent.topicName}", topicContent.lockTime)
//            if (showDateIfFutureOrToday(topicContent.lockTime)) binding.videoicon.setImageResource(R.drawable.frame_1707481707) else binding.videoicon.setImageResource(
//                R.drawable.frame_1707481080
//            )
            Log.e("foldernamess", folderName)
            val icon = when (folderName) {
                "Chemistry" -> R.drawable.chemistory
                "Physics" -> R.drawable.phys_icon
                "Maths", "Mathematics" -> R.drawable.math_icon
                "Biology" -> R.drawable.biology_ic
                else -> R.drawable.other_icon // Set a default icon if folder name doesn't match
            }
            val background = when (folderName) {
                "Chemistry" -> R.color.biology_color
                "Physics" -> R.color.physics_color
                "Maths", "Mathematics" -> R.color.mathematics_color
                "Biology" -> R.color.biology_color
                else -> R.color.other_color // Set a default icon if folder name doesn't match
            }
            Log.e(
                "getlocketime ${
                    isDateTodayOrPast(
                        topicContent.lockTime,
                        topicContent.isExternal
                    )
                } ", topicContent.lockTime
            )
            if (isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)) {
                if (topicContent.fileType == "VIDEO") {
                    binding.tvCourseDescription.visibility = View.VISIBLE
                    binding.shapeableImage.setImageResource(icon)
                    binding.shapeableImage.setBackgroundResource(background)
                    binding.shapeableImage.visibility = View.VISIBLE
                    binding.clEmtpyVeiw.visibility = View.INVISIBLE
                    binding.etHomeWorkText.visibility = View.VISIBLE
                    binding.etHomeWorkPdf.visibility = View.VISIBLE
                    binding.etHomeWorkPdf.text =
                        if (topicContent.homeworkName.isNotEmpty()) " " + helperFunctions.removeBrackets(
                            topicContent.homeworkName
                        ) else "NA"
                    binding.etHomeWorkPdf.setOnClickListener {
                        Log.d("urlhomegetting", topicContent.homeworkUrl)
                        val urlToSent = removeBrackets(topicContent.homeworkUrl)
                        val intent = Intent(context, PdfViewActivity::class.java).apply {
                            putExtra("PDF_URL", urlToSent)
                            putExtra("PDF_TITLE", topicContent.homeworkName)
                            putExtra("FOLDER_NAME", folderName)

                        }
                        context?.startActivity(intent)
//                        helperFunctions.downloadPdf(context,topicContent.homeworkUrl,topicContent.homeworkName)
                    }
                    binding.videoicon.setImageResource(R.drawable.frame_1707481707)
                    binding.ivPersonIdentifier.setBackgroundResource(R.drawable.clock_black)
                } else if (topicContent.fileType == "PDF") {
                    binding.tvCourseDescription.visibility = View.VISIBLE
                    binding.etHomeWorkPdf.visibility = View.GONE
                    binding.etHomeWorkText.visibility = View.GONE
                    binding.shapeableImage.visibility = View.INVISIBLE
                    binding.clEmtpyVeiw.visibility = View.VISIBLE
                    binding.videoicon.setImageResource(R.drawable.pdf_bg)
                }
                else if (topicContent.fileType == "IMAGE") {
                    binding.tvCourseDescription.visibility = View.VISIBLE
                    binding.tvLecture.text = "Image"
                    binding.etHomeWorkPdf.visibility = View.GONE
                    binding.etHomeWorkText.visibility = View.GONE
                    binding.videoicon.setImageResource(R.drawable.arrow_right_white)
                    binding.ivMoreInfoLec.visibility = View.GONE
                }
                binding.videoicon.visibility = View.VISIBLE
                binding.ivPersonIdentifier.setBackgroundResource(R.drawable.download_person)

            }
             else if (topicContent.fileType == "URL") {
            //    binding.clReadAndPlay.visibility = View.GONE
                binding.ivMoreInfoLec.visibility = View.GONE
                binding.etHomeWorkText.visibility = View.GONE
            } else
            {
                binding.tvCourseDescription.visibility = View.VISIBLE
                binding.etHomeWorkText.visibility = View.VISIBLE
                if (topicContent.fileType == "VIDEO") {
                    binding.shapeableImage.setImageResource(icon)
                    binding.shapeableImage.setBackgroundResource(background)
                    binding.shapeableImage.visibility = View.VISIBLE
                    binding.clEmtpyVeiw.visibility = View.INVISIBLE
                } else {
                    binding.tvCourseDescription.visibility = View.VISIBLE
                    binding.shapeableImage.visibility = View.GONE
                    binding.clEmtpyVeiw.visibility = View.VISIBLE
                }
                binding.videoicon.visibility = View.VISIBLE
                binding.etHomeWorkText.visibility = View.GONE
                binding.etHomeWorkPdf.visibility = View.GONE
                binding.videoicon.setImageResource(R.drawable.frame_1707481080)
            }
            Log.e("lectueres", topicContent.lecture)
            binding.tvLecture.text = topicContent.lecture
            binding.tvLecturerName.text = topicContent.lecturerName
            if (topicContent.fileType == "URL" && topicContent.url.contains(
                    "http"
                )
            ) {
                Log.e("lectueyt", topicContent.lecture)
                binding.etHomeWorkText.visibility = View.GONE
                binding.ivMoreInfoLec.visibility = View.GONE
                binding.tvLecture.text = "Title"
                binding.tvTopicName.text = topicContent.topicDescription
                binding.tvCourseDescription.visibility = View.GONE
                binding.tvTopicName.post {
                    val maxLines = 2
                    if (binding.tvTopicName.lineCount > maxLines) {
                        val originalText = topicContent.topicDescription
                        val end = minOf(
                            binding.tvTopicName.layout.getLineEnd(maxLines - 1),
                            originalText.length
                        )
                        val truncatedText = originalText.substring(0, end).trim()

                        if (truncatedText.length < originalText.length) {
                            val spannableString = SpannableString("$truncatedText... Read more")

                            val clickableSpan = object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    // Handle the "Read More" click event
                                    binding.tvTopicName.text = originalText
                                }
                            }

                            spannableString.setSpan(
                                clickableSpan,
                                spannableString.length - "Read more".length,
                                spannableString.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            // Set the color for "Read More"
                            spannableString.setSpan(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        binding.root.context,
                                        R.color.blue_3E3EF7
                                    )
                                ),
                                spannableString.length - "Read more".length,
                                spannableString.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            binding.tvTopicName.text = spannableString
                            binding.tvTopicName.maxLines = maxLines
                            binding.tvTopicName.ellipsize = TextUtils.TruncateAt.END
                        } else {
                            binding.tvTopicName.text = originalText
                        }
                    } else {
                        binding.tvTopicName.text = topicContent.topicDescription
                    }
                }
            } else {
                binding.tvTopicName.text = topicContent.topicName
                binding.tvCourseDescription.text = topicContent.topicDescription
                  binding.tvCourseDescription.visibility = View.VISIBLE
            }

            // Handle the topic description with truncation and "Read More"
            binding.tvCourseDescription.post {
                val maxLines = 2
                if (binding.tvCourseDescription.lineCount > maxLines) {
                    val originalText = topicContent.topicDescription
                    val end = minOf(
                        binding.tvCourseDescription.layout.getLineEnd(maxLines - 1),
                        originalText.length
                    )
                    val truncatedText = originalText.substring(0, end).trim()

                    if (truncatedText.length < originalText.length) {
                        val spannableString = SpannableString("$truncatedText... Read more")

                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                // Handle the "Read More" click event
                                binding.tvCourseDescription.text = originalText
                            }
                        }

                        spannableString.setSpan(
                            clickableSpan,
                            spannableString.length - "Read more".length,
                            spannableString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        // Set the color for "Read More"
                        spannableString.setSpan(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.blue_3E3EF7
                                )
                            ),
                            spannableString.length - "Read more".length,
                            spannableString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        binding.tvCourseDescription.text = spannableString
                        binding.tvCourseDescription.maxLines = maxLines
                        binding.tvCourseDescription.ellipsize = TextUtils.TruncateAt.END
                    } else {
                        binding.tvCourseDescription.text = originalText
                    }
                } else {
                    binding.tvCourseDescription.text = topicContent.topicDescription
                }
            }

            binding.rootTopic.setOnClickListener {
                Log.d("typeChecking", topicContent.fileType)
                if (topicContent.fileType == "URL") {
                    it.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(topicContent.url)
                        )
                    )
                }
                if(topicContent.fileType =="IMAGE"){
                    onItemClick(topicContent, "", arrayListOf(),arrayListOf(),arrayListOf(),arrayListOf(),arrayListOf(),arrayListOf())
                }
            }

        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun showDateIfFutureOrToday(dateString: String): Boolean {
            // Correct the date string if necessary
            Log.e("dateStrings", dateString)
            val cleanedDateString = dateString.replace("Sept", "Sep").trim()

            // Define the corrected primary date format pattern (assuming '24' refers to 2024)
            val primaryFormatter = DateTimeFormatter.ofPattern("dd MMM, yy hh:mm a", Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.SMART) // Handle ambiguous year formats like '24'

            // Define the fallback format pattern for date only
            val fallbackFormatter = DateTimeFormatter.ofPattern("dd MMM, yy", Locale.ENGLISH)

            return try {
                // Try to parse using the primary formatter
                val dateTime = LocalDateTime.parse(cleanedDateString, primaryFormatter)
                // Get today's date
                val today = LocalDateTime.now()
                // Compare the dates (check if date is today or in the future)
                dateTime.isAfter(today) || dateTime.isEqual(today)
            } catch (e: DateTimeParseException) {
                Log.e(
                    "DateParsingError",
                    "Primary format parsing error: ${e.message}. Input date string: '$cleanedDateString'"
                )
                try {
                    // Try parsing using the fallback formatter (without time)
                    val dateTime = LocalDate.parse(cleanedDateString, fallbackFormatter)
                    val today = LocalDate.now()
                    // Compare the dates (check if date is today or in the future)
                    dateTime.isAfter(today) || dateTime.isEqual(today)
                } catch (fallbackException: DateTimeParseException) {
                    Log.e(
                        "DateParsingError",
                        "Fallback format parsing error: ${fallbackException.message}. Input date string: '$cleanedDateString'"
                    )
                    false
                }
            }
        }

        fun removeBrackets(input: String): String {
            var url = input
            if (input.startsWith("[")) {
                url = input.removePrefix("[")
            }

            if (input.endsWith("]")) {
                url = url.removeSuffix("]")
            }
            return url
        }

        fun isDateTodayOrPast(dateString: String, external: Boolean): Boolean {
            // Clean up the date string
            if (external) {
                return true
            }

            val cleanedDateString = dateString.replace("Sept", "Sep").trim()
                .replace("pm".toRegex(), "PM")
                .replace("am".toRegex(), "AM")


            // Define the primary date format pattern
            val formatter = DateTimeFormatter.ofPattern("dd MMM, yy hh:mm a", Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.SMART)

            return try {
                // Parse the date and time
                val dateTime = LocalDateTime.parse(cleanedDateString, formatter)
                // Get today's date and time
                val now = LocalDateTime.now()
                // Check if the date is today or in the past
                dateTime.isBefore(now) || dateTime.isEqual(now)
            } catch (e: DateTimeParseException) {
                Log.e(
                    "DateParsingError",
                    "Error parsing date: ${e.message}. Input date string: '$cleanedDateString'"
                )
                false
            }
        }

    }

    private fun isDateTodayOrPast(dateString: String, external: Boolean): Boolean {
        // Clean up the date string
        if (external) {
            return true
        }

        val cleanedDateString = dateString.replace("Sept", "Sep").trim()
            .replace("pm".toRegex(), "PM")
            .replace("am".toRegex(), "AM")


        // Define the primary date format pattern
        val formatter = DateTimeFormatter.ofPattern("dd MMM, yy hh:mm a", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.SMART)

        return try {
            // Parse the date and time
            val dateTime = LocalDateTime.parse(cleanedDateString, formatter)
            // Get today's date and time
            val now = LocalDateTime.now()
            // Check if the date is today or in the past
            dateTime.isBefore(now) || dateTime.isEqual(now)
        } catch (e: DateTimeParseException) {
            Log.e(
                "DateParsingError",
                "Error parsing date: ${e.message}. Input date string: '$cleanedDateString'"
            )
            false
        }
    }


}

class MyDiffUtilCallback(
    private val oldList: List<TopicContentModel>,
    private val newList: List<TopicContentModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}


