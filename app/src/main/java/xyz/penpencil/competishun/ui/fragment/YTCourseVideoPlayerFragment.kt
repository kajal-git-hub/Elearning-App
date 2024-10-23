package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentStudyMaterialDetailsBinding
import xyz.penpencil.competishun.databinding.FragmentYTCourseVideoPlayerBinding
import xyz.penpencil.competishun.utils.HelperFunctions

class YTCourseVideoPlayerFragment : Fragment() {
    private lateinit var helperFunctions: HelperFunctions
    private lateinit var binding : FragmentYTCourseVideoPlayerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentYTCourseVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}