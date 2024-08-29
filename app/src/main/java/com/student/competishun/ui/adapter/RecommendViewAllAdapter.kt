import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.R
import com.student.competishun.curator.GetAllCourseQuery
import com.student.competishun.utils.HelperFunctions
import java.util.Locale

class RecommendViewAllAdapter(
    private var items: List<GetAllCourseQuery.Course>,
    private val onItemClick: (GetAllCourseQuery.Course) -> Unit
) : RecyclerView.Adapter<RecommendViewAllAdapter.CourseViewHolder>(), Filterable {

    private val helperFunctions = HelperFunctions()
    private var filteredItems = items.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommended_course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = filteredItems[position]

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

        when (course.course_class.toString()) {
            "TWELFTH_PLUS" -> holder.recommendedClass.text = "12th+ Class"
            "TWELFTH" -> holder.recommendedClass.text = "12th Class"
            "ELEVENTH" -> holder.recommendedClass.text = "11th Class"
        }

        holder.targetYear.text = "Target ${course.target_year}"
        holder.startDate.text = "Starts On: " + helperFunctions.formatCourseDate(course.course_start_date.toString())
        holder.endDate.text = "Expiry Date: " + helperFunctions.formatCourseDate(course.course_end_date.toString())
        holder.lectureCount.text = "Lectures: 0" // Update this if you have actual data
        holder.quizCount.text = "Validity: " + helperFunctions.formatCourseDate(course.course_validity_end_date.toString())

        holder.itemView.setOnClickListener {
            onItemClick(course)
        }
    }

    override fun getItemCount(): Int = filteredItems.size

    fun updateData(newItems: List<GetAllCourseQuery.Course>) {
        items = newItems
        filteredItems.clear()
        filteredItems.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.getDefault())
                val results = FilterResults()
                results.values = if (query.isNullOrEmpty()) {
                    items
                } else {
                    items.filter {
                        it.name.lowercase(Locale.getDefault()).contains(query)
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as MutableList<GetAllCourseQuery.Course>
                notifyDataSetChanged()
            }
        }
    }

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.tvRecommendedCourseName)
        val discount: TextView = view.findViewById(R.id.tvDiscount)
        val recommendedClass: TextView = view.findViewById(R.id.recommendedClass)
        val targetYear: TextView = view.findViewById(R.id.tvTarget)
        val startDate: MaterialTextView = view.findViewById(R.id.tvStartDate)
        val endDate: MaterialTextView = view.findViewById(R.id.tvEndDate)
        val lectureCount: TextView = view.findViewById(R.id.tvLectureNo)
        val quizCount: TextView = view.findViewById(R.id.tvQuizTests)
        val originalPrice: TextView = view.findViewById(R.id.orgPrice)
        val discountPrice: TextView = view.findViewById(R.id.dicountPrice)
        val bannerImage: ImageView = view.findViewById(R.id.recemmended_banner)
    }
}
