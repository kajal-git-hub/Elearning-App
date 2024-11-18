package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentAboutUsBinding
import xyz.penpencil.competishun.ui.adapter.AboutUsAdapter
import xyz.penpencil.competishun.ui.adapter.AboutUsItem
import xyz.penpencil.competishun.ui.main.HomeActivity

class AboutUsFragment : DrawerVisibility() {

    private var _binding: FragmentAboutUsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        binding.etBTHomeAddress.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvAboutUsDesc.text = Html.fromHtml(getString(R.string.about_us_text), Html.FROM_HTML_MODE_LEGACY)

        val aboutUsItems = listOf(
            AboutUsItem(
                image = R.drawable.mohit,
                teacherName = "Mohit Tyagi Sir (MT Sir)",
                teacherSub = "Mathematics",
                teacherYear = "22 Years",
                courseAndCollege = "B.Tech. , IIT Delhi",
                mentorDescriptions = listOf("Mohit Tyagi Sir has over 22 years of experience in teaching Mathematics.", "He is renowned for creating a love for Mathematics among students.","Previously served as the Head of the Maths Team (HOD) at a leading coaching institute in Kota.","His YouTube channel is a source of inspiration for both students and faculty members.")
            ),


            AboutUsItem(
                image = R.drawable.alok,
                teacherName = "Alok Kumar Sir",
                teacherSub = "Physical-Inorganic Chemistry",
                teacherYear = "19 Years",
                courseAndCollege = "B.Tech. , NIT Allahabad",
                mentorDescriptions = listOf("He has held senior faculty positions at many reputed IIT-JEE coaching institutions.", "Known for his organized teaching style, making Chemistry simple and interesting for students.","Relates various Chemistry topics to practical applications, fostering deep interest in the subject.","Believes Science and technology will play a pivotal role in India's development.","Strives to motivate students to pursue careers in Science and technology.")
            ),
            AboutUsItem(
                image = R.drawable.neeraj,
                teacherName = "Neeraj Saini Sir ( NS Sir )",
                teacherSub = "Organic chemistry",
                teacherYear = "18 Years",
                courseAndCollege = "NET-JRF, SLET,",
                mentorDescriptions = listOf("He has 14 years of experience teaching Organic Chemistry as a Senior Faculty at a reputed national institute.", "Known for his concise and simplified teaching style, helping students score well in Organic Chemistry.","Has mentored many students who secured Top-100 AIRs in IIT-JEE (Main) and (Main+Advanced).")
            ),
            AboutUsItem(
                image = R.drawable.amit,
                teacherName = "Amit Bijarnia (ABJ Sir)",
                teacherSub = "Physics",
                teacherYear = "15 Years",
                courseAndCollege = "B.Tech. , IIT Delhi",
                mentorDescriptions = listOf("He is an enthusiastic Physics teacher, highly popular among JEE (Advanced)/IIT-JEE students.", "Known for helping students visualize problems and reach solutions quickly.","Adored by students for his clear, engaging teaching style that fosters a love for Physics.","Many of his students from Kota have achieved prestigious ranks in IIT-JEE (Advanced).")
            ),
        )

        val aboutUsAdapter = AboutUsAdapter(aboutUsItems)
        binding.rvMeetMentors.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = aboutUsAdapter
        }

    }
    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
