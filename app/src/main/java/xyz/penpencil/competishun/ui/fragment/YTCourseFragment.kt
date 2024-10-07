package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentYTCourseBinding


class YTCourseFragment : Fragment() {

    private val binding by lazy {
        FragmentYTCourseBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvCourseMaterialCount.text = "36 Courses"
        binding.tvShowingResults.text = "Showing results (4):"

    }

}