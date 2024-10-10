package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
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
            findNavController().navigate(R.id.action_AboutUs_to_homePage)
        }

        val aboutUsItems = listOf(
            AboutUsItem(
                image = R.drawable.mohit,
                teacherName = "Mohit Tyagi Sir (MT Sir)",
                teacherSub = "Mathematics",
                teacherYear = "22 Years",
                courseAndCollege = "B.Tech. , IIT Delhi",
                mentorDescriptions = listOf("Expert in JEE Mathematics", "Over 20 years of teaching experience")
            ),
            AboutUsItem(
                image = R.drawable.alok,
                teacherName = "Alok Kumar Sir",
                teacherSub = "Physical-Inorganic Chemistry",
                teacherYear = "19 Years",
                courseAndCollege = "B.Tech. , NIT Allahabad",
                mentorDescriptions = listOf("Expert in JEE Mathematics", "Over 20 years of teaching experience")
            ),
            AboutUsItem(
                image = R.drawable.neeraj,
                teacherName = "Neeraj Saini Sir ( NS Sir )",
                teacherSub = "Organic chemistry",
                teacherYear = "18 Years",
                courseAndCollege = "NET-JRF, SLET,",
                mentorDescriptions = listOf("Expert in JEE Mathematics", "Over 20 years of teaching experience")
            ),
            AboutUsItem(
                image = R.drawable.amit,
                teacherName = "Amit Bijarnia (ABJ Sir)",
                teacherSub = "Physics",
                teacherYear = "15 Years",
                courseAndCollege = "B.Tech. , IIT Delhi",
                mentorDescriptions = listOf("Expert in JEE Mathematics", "Over 20 years of teaching experience")
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
