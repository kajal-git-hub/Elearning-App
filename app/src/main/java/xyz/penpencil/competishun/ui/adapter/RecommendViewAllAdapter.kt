import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.curator.AllCourseForStudentQuery
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.utils.HelperFunctions
import java.util.Locale

class RecommendViewAllAdapter(
    private var items: List<AllCourseForStudentQuery.Course>,
    private val onItemClick: (AllCourseForStudentQuery.Course,List<String>?) -> Unit
) : RecyclerView.Adapter<RecommendViewAllAdapter.CourseViewHolder>(), Filterable {

    private var filteredItems: MutableList<AllCourseForStudentQuery.Course> = items.toMutableList()
    private val helperFunctions = HelperFunctions()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommended_course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = filteredItems[position]

        val recommendCourseTags = course.course_tags
        Log.d("recommendCourseTags", recommendCourseTags.toString())

        holder.recommendedClass.apply {
            text = recommendCourseTags?.getOrNull(0) ?: ""
            visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        }

        holder.tvTag2.apply {
            text = recommendCourseTags?.getOrNull(1) ?: ""
            visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        }

        holder.tvLastField.apply {
            text = recommendCourseTags?.getOrNull(2) ?: ""
            visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        }

        holder.courseName.text = "${course.name}"
        Log.e("coursestuss",course.status.name)
        if(course.status.name == "COMING_SOON" ){
            holder.discountPrice.text =  "Coming Soon"
            holder.originalPrice.visibility =  View.GONE
            holder.discount.visibility = View.GONE
        }else {
            if (course.price != null && course.discount != null) {
                val discountPercent =
                    helperFunctions.calculateDiscountPercentage(course.price, course.discount)
                holder.discount.text = "${discountPercent.toInt()}% off"
                holder.discountPrice.text = "₹${course.discount}"
                holder.originalPrice.text = "₹${course.price}"
                holder.percentConstraint.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)

                    .load(course.banner_image)
                    .placeholder(R.drawable.rectangle_1072)
                    .error(R.drawable.default_image)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(holder.bannerImage)


            } else if (course.price != null && course.discount == null) {
                holder.discountPrice.text = "₹${course.price}"
                holder.originalPrice.visibility = View.GONE

            } else if (course.price == course.discount) {
                holder.percentConstraint.visibility = View.GONE
            }
        }
        holder.targetYear.text = "Target ${course.target_year}"
        holder.startDate.text = "Starts On: " + helperFunctions.formatCourseDate(course.live_date.toString())
        holder.endDate.text = "Ends On: " + helperFunctions.formatCourseDate(course.course_end_date.toString())
        holder.lectureCount.text = "Lectures: 0" // Update this if you have actual data
        holder.quizCount.text = "Validity: " + helperFunctions.formatCourseDate(course.course_validity_end_date.toString())
        if(course.status.name != "COMING_SOON" ){
            holder.itemView.setOnClickListener {
                onItemClick(course,recommendCourseTags)
            }
        }
    }

    override fun getItemCount(): Int = filteredItems.size

    fun updateData(newItems: List<AllCourseForStudentQuery.Course>) {
        items = newItems
        filteredItems.clear()
        filteredItems.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""

                val filteredList = if (query.isEmpty()) {
                    items
                } else {
                    items.filter {
                        it.name.lowercase(Locale.getDefault()).contains(query)
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems.clear()
                if (results?.values is List<*>) {
                    filteredItems.addAll(results.values as List<AllCourseForStudentQuery.Course>)
                }
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
        val bannerImage: ImageView = view.findViewById(R.id.recommendbanner)
        val percentConstraint : ConstraintLayout = view.findViewById(R.id.cl_percentOff)
        val tvLastField:TextView = view.findViewById(R.id.tvLastField)
        val tvTag2:TextView = view.findViewById(R.id.tvTag2)

    }
}

