package com.student.competishun.utils

import com.student.competishun.data.model.RecommendedCourseDataModel
import com.student.competishun.data.model.Testimonial
import com.student.competishun.data.model.WhyCompetishun

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
        listOf(SOCIAL_MEDIA, ADVERTISEMENT,FRIENDS_FAMILY, OTHER)
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


}
