package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TabItem
import com.student.competishun.ui.adapter.CourseAdapter


private const val TAG = "CourseFragment"

class CourseFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val tabItems1 = listOf(
            TabItem(
                discount = "11% OFF",
                courseName = "Prakhar Integrated (Fast Lane-2) 2024-25",
                tags = listOf("12th Class", "Full-Year", "Target 2025"),
                startDate = "Starts On: 01 Jul, 24",
                endDate = "Expiry Date: 31 Jul, 24",
                lectures = "Lectures: 56",
                quizzes = "Quiz & Tests: 120",
                originalPrice = "₹44,939",
                discountPrice = "₹29,900"
            )
            // Add more TabItem objects here
        )

        val tabItems2 = listOf(
            TabItem(
                discount = "15% OFF",
                courseName = "Medical Entrance Prep 2024-25",
                tags = listOf("12th Class", "Full-Year", "Target 2025"),
                startDate = "Starts On: 01 Aug, 24",
                endDate = "Expiry Date: 31 Aug, 24",
                lectures = "Lectures: 60",
                quizzes = "Quiz & Tests: 130",
                originalPrice = "₹50,000",
                discountPrice = "₹42,500"
            )
            // Add more TabItem objects here
        )

        val tabItems3 = listOf(TabItem(
                discount = "20% OFF",
                courseName = "Engineering Entrance Prep 2024-25",
                tags = listOf("12th Class", "Full-Year", "Target 2025"),
                startDate = "Starts On: 01 Sep, 24",
                endDate = "Expiry Date: 31 Sep, 24",
                lectures = "Lectures: 70",
                quizzes = "Quiz & Tests: 140",
                originalPrice = "₹60,000",
                discountPrice = "₹48,000"
            )
        )

        val combinedTabItems = tabItems1 + tabItems2 + tabItems3

        recyclerView.adapter = CourseAdapter(combinedTabItems)

        // Optionally set up data for the adapter

        return view
    }

}