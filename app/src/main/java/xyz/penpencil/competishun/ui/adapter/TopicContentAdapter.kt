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
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.ItemTopicTypeContentBinding
import xyz.penpencil.competishun.ui.fragment.BottomSheetDownloadBookmark
import xyz.penpencil.competishun.utils.HelperFunctions
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale


class TopicContentAdapter(
    private val topicContents: List<TopicContentModel>,
    private val folderContentId: String,
    private val fragmentActivity: FragmentActivity,
    private val context: Context, // Pass context
    private val onItemClick: (TopicContentModel, String,ArrayList<String>,ArrayList<String>, ArrayList<String>,ArrayList<String>,ArrayList<String>,ArrayList<String>) -> Unit
) :
    RecyclerView.Adapter<TopicContentAdapter.TopicContentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicContentViewHolder {
        val binding = ItemTopicTypeContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopicContentViewHolder(binding,context)
    }

    override fun onBindViewHolder(holder: TopicContentViewHolder, position: Int) {

        val topicContent = topicContents[position]
        holder.bind(topicContents[position], fragmentActivity)
        Log.e("valuesss ${isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)} ", topicContent.lockTime.toString())
        val unlockedTopicContentIds = topicContents
            .filter { isDateTodayOrPast(it.lockTime, topicContent.isExternal) && it.fileType == "VIDEO" }
            .map { it.id }.toCollection(ArrayList())
        val unlockedTopicContentNames = topicContents
            .filter { isDateTodayOrPast(it.lockTime, topicContent.isExternal) && it.fileType == "VIDEO" }
            .map { it.topicName }.toCollection(ArrayList())
        val unlockedTopicContentDescs = topicContents
            .filter { isDateTodayOrPast(it.lockTime, topicContent.isExternal) && it.fileType == "VIDEO" }
            .map { it.topicDescription }.toCollection(ArrayList())
        val unlockedTopicContenthomeworks = topicContents
            .filter { isDateTodayOrPast(it.lockTime, topicContent.isExternal) && it.fileType == "VIDEO" }
            .map { it.homeworkName }.toCollection(ArrayList())
        val unlockedTopicContenthomeworkLinks = topicContents
            .filter { isDateTodayOrPast(it.lockTime, topicContent.isExternal) && it.fileType == "VIDEO" }
            .map { it.homeworkUrl }.toCollection(ArrayList())
        val unlockedTopicContenthomeworkDescs = topicContents
            .filter { isDateTodayOrPast(it.lockTime, topicContent.isExternal) && it.fileType == "VIDEO" }
            .map { it.homeworkDesc }.toCollection(ArrayList())
        Log.e("unlockedTopics",unlockedTopicContentIds.toString())
        Log.e("unlockedTopic",unlockedTopicContentNames.toString())
        // Disable click if locked, enable if not
        holder.itemView.setOnClickListener {
            if ((topicContent.fileType == "URL")){
                val url = if (topicContent.topicName.contains("http")) topicContent.url else "https://${topicContent.url}"
                it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } else if (isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)){
                onItemClick(topicContent, folderContentId,unlockedTopicContentIds,unlockedTopicContentNames,unlockedTopicContentDescs,unlockedTopicContenthomeworks,unlockedTopicContenthomeworkLinks,unlockedTopicContenthomeworkDescs)
            } else if (topicContent.fileType == "UNKNOWN__" ) {
                Log.e("TAG", "onBindViewHolder: ")
            }else {
                Toast.makeText(it.context, "Content is locked!", Toast.LENGTH_SHORT).show()
            }
        }
        if (!isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)) {
            // Enable the click listener for unlocked items
            holder.itemView.findViewById<ImageView>(R.id.iv_MoreInfoLec).visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = topicContents.size

    class TopicContentViewHolder(private val binding: ItemTopicTypeContentBinding,
                                 private val context: Context // Use context for download function
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topicContent: TopicContentModel, fragmentActivity: FragmentActivity) {

            Log.e("nzxbNZBxbZNXbnZ", "bind: "+topicContent.fileType)
            binding.ivSubjectBookIcon.setImageResource(topicContent.subjectIcon)

            var helperFunctions = HelperFunctions()

            binding.ivMoreInfoLec.setOnClickListener {
                val bottomSheet = BottomSheetDownloadBookmark()
                bottomSheet.setItemDetails(topicContent)
                bottomSheet.show(fragmentActivity.supportFragmentManager, bottomSheet.tag)
            }
          Log.e("getlocketime ${topicContent.topicName}",topicContent.lockTime)
//            if (showDateIfFutureOrToday(topicContent.lockTime)) binding.videoicon.setImageResource(R.drawable.frame_1707481707) else binding.videoicon.setImageResource(
//                R.drawable.frame_1707481080
//            )
            Log.e("getlocketime ${isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)} ",topicContent.lockTime)
            if ( isDateTodayOrPast(topicContent.lockTime, topicContent.isExternal)) {
                if (topicContent.fileType == "VIDEO")
                {
                    binding.etHomeWorkText.visibility = View.VISIBLE
                    binding.etHomeWorkPdf.visibility = View.VISIBLE
                    binding.etHomeWorkPdf.text = if (topicContent.homeworkName.isNotEmpty()) " "+helperFunctions.removeBrackets(topicContent.homeworkName) else "NA"
                    binding.etHomeWorkPdf.setOnClickListener {
                        helperFunctions.downloadPdf(context,topicContent.homeworkUrl,topicContent.homeworkName)
                    }
                    binding.videoicon.setImageResource(R.drawable.frame_1707481707)
                    binding.ivPersonIdentifier.setBackgroundResource(R.drawable.clock_black)
                } else if (topicContent.fileType == "PDF"){
                    binding.etHomeWorkPdf.visibility = View.GONE
                    binding.etHomeWorkText.visibility = View.GONE

                    binding.videoicon.setImageResource(R.drawable.pdf_bg)}
                binding.videoicon.visibility = View.VISIBLE
                binding.ivPersonIdentifier.setBackgroundResource(R.drawable.download_person)


            } else if (topicContent.fileType == "UNKNOWN__"  || topicContent.fileType == "URL"){
                binding.clReadAndPlay.visibility = View.GONE
                binding.ivMoreInfoLec.visibility = View.GONE
            }else {
                binding.videoicon.visibility = View.VISIBLE
                binding.videoicon.setImageResource(R.drawable.frame_1707481080)
            }

            binding.tvLecture.text = topicContent.lecture
            binding.tvLecturerName.text = topicContent.lecturerName
            if (topicContent.fileType == "UNKNOWN__"  || topicContent.fileType == "URL" && topicContent.url.contains("http") ){
                binding.tvLecture.text = "Link"
            }
            binding.tvTopicName.text = topicContent.topicName

            // Handle the topic description with truncation and "Read More"
            binding.tvCourseDescription.post {
                val maxLines = 2
                if (binding.tvCourseDescription.lineCount > maxLines) {
                    val originalText = topicContent.topicDescription
                    val end = binding.tvCourseDescription.layout.getLineEnd(maxLines - 1)
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

            binding.tvTopicName.setOnClickListener {
                if (topicContent.fileType == "UNKNOWN__"  || topicContent.fileType == "URL"){
                    it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(topicContent.url)))
                }
            }

        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun showDateIfFutureOrToday(dateString: String): Boolean {
            // Correct the date string if necessary
            Log.e("dateStrings",dateString)
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
                Log.e("DateParsingError", "Primary format parsing error: ${e.message}. Input date string: '$cleanedDateString'")
                try {
                    // Try parsing using the fallback formatter (without time)
                    val dateTime = LocalDate.parse(cleanedDateString, fallbackFormatter)
                    val today = LocalDate.now()
                    // Compare the dates (check if date is today or in the future)
                    dateTime.isAfter(today) || dateTime.isEqual(today)
                } catch (fallbackException: DateTimeParseException) {
                    Log.e("DateParsingError", "Fallback format parsing error: ${fallbackException.message}. Input date string: '$cleanedDateString'")
                    false
                }
            }
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
