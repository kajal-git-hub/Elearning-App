import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.request.target.Target
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.R
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetAllCourseQuery
import com.student.competishun.curator.adapter.AllCourseForStudentQuery_ResponseAdapter
import com.student.competishun.utils.HelperFunctions
import org.w3c.dom.Text

class RecommendedCoursesAdapter(
    private val items: List<AllCourseForStudentQuery.Course>,
    private val onItemClick: (AllCourseForStudentQuery.Course) -> Unit
) : RecyclerView.Adapter<RecommendedCoursesAdapter.CourseViewHolder>() {

    private lateinit var helperFunctions: HelperFunctions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommended_course_item, parent, false)
        helperFunctions = HelperFunctions()
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = items[position]
        val courseTags = course.course_tags
        Log.d("courseTags", courseTags.toString())

        holder.recommendedClass.text = courseTags?.getOrNull(0) ?: "NA"
        holder.tvTag2.text = courseTags?.getOrNull(1) ?: "NA"
        holder.tvLastField.text = courseTags?.getOrNull(2) ?: "NA"


        holder.courseName.text = "${course.name}  ${course.academic_year}"

        if (course.price != null && course.discount != null) {
            val (discountPercent, discountPrice) = helperFunctions.calculateDiscountDetails(course.price.toDouble(), course.discount.toDouble())
            holder.discount.text = "${discountPercent.toInt()}% off"
            holder.discountPrice.text = "₹$discountPrice"
            holder.originalPrice.text = "₹${course.price}"
            Glide.with(holder.itemView.context)

                .load(course.banner_image)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.bannerImage)

        }
//        if(course.course_class.toString() =="TWELFTH_PLUS"){
//            holder.recommendedClass.text = "12th+ Class"
//
//        }else if(course.course_class.toString()=="TWELFTH"){
//            holder.recommendedClass.text = "12th Class"
//
//        }
//        else if(course.course_class.toString()=="ELEVENTH"){
//            holder.recommendedClass.text = "11th Class"
//
//        }
        holder.targetYear.text = "Target ${course.target_year}"
        holder.startDate.text = "Starts On: "+helperFunctions.formatCourseDate(course.course_start_date.toString())
        holder.endDate.text = "Expiry Date: "+helperFunctions.formatCourseDate(course.course_end_date.toString())

        holder.lectureCount.text = "Lectures: 0"
        holder.quizCount.text = "Validity: "+helperFunctions.formatCourseDate(course.course_validity_end_date.toString())
        holder.itemView.setOnClickListener {
            onItemClick(course)
        }
    }

    override fun getItemCount(): Int = items.size

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.tvRecommendedCourseName)
        val discount: TextView = view.findViewById(R.id.tvDiscount)
        val recommendedClass : TextView = view.findViewById(R.id.recommendedClass)
        val targetYear: TextView = view.findViewById(R.id.tvTarget)
        val startDate: MaterialTextView = view.findViewById(R.id.tvStartDate)
        val endDate: MaterialTextView = view.findViewById(R.id.tvEndDate)
        val lectureCount: TextView = view.findViewById(R.id.tvLectureNo)
        val quizCount: TextView = view.findViewById(R.id.tvQuizTests)
        val originalPrice: TextView = view.findViewById(R.id.orgPrice)
        val discountPrice: TextView = view.findViewById(R.id.dicountPrice)
        val bannerImage:ImageView = view.findViewById(R.id.recemmended_banner)
        val tvTag2:TextView = view.findViewById(R.id.tvTag2)

        val tvLastField:TextView = view.findViewById(R.id.tvLastField)
    }
}
