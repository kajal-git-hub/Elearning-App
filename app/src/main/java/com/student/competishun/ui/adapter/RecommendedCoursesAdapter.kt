import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.R
import com.student.competishun.curator.GetAllCourseQuery
import com.student.competishun.utils.HelperFunctions

class RecommendedCoursesAdapter(
    private val items: List<GetAllCourseQuery.Course>
) : RecyclerView.Adapter<RecommendedCoursesAdapter.CourseViewHolder>() {

    private lateinit var helperFunctions: HelperFunctions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommended_course_item, parent, false)
        helperFunctions = HelperFunctions()
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = items[position]

        holder.courseName.text = course.name

        if (course.price != null && course.discount != null) {
            val (discountPercent, discountPrice) = helperFunctions.calculateDiscountDetails(course.price, course.discount)
            holder.discount.text = "$discountPercent% off"
            holder.discountPrice.text = "₹$discountPrice"
            holder.originalPrice.text = "₹${course.price}"
        }

        holder.targetYear.text = "Target ${course.target_year}"
        holder.startDate.text = helperFunctions.formatCourseDate(course.course_validity_start_date.toString())
        holder.endDate.text = helperFunctions.formatCourseDate(course.course_validity_end_date.toString())

        holder.lectureCount.text = "Lectures: 0"
        holder.quizCount.text = "Quiz & Tests: 0"
    }

    override fun getItemCount(): Int = items.size

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.tvRecommendedCourseName)
        val discount: TextView = view.findViewById(R.id.tvDiscount)
        val targetYear: TextView = view.findViewById(R.id.tvTarget)
        val startDate: MaterialTextView = view.findViewById(R.id.tvStartDate)
        val endDate: MaterialTextView = view.findViewById(R.id.tvEndDate)
        val lectureCount: TextView = view.findViewById(R.id.tvLectureNo)
        val quizCount: TextView = view.findViewById(R.id.tvQuizTests)
        val originalPrice: TextView = view.findViewById(R.id.orgPrice)
        val discountPrice: TextView = view.findViewById(R.id.dicountPrice)
    }
}
