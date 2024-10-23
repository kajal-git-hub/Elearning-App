package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.penpencil.competishun.databinding.FragmentYTCourseDetailsBinding
import xyz.penpencil.competishun.utils.HelperFunctions

class YTCourseDetailsFragment : Fragment() {

    private lateinit var binding: FragmentYTCourseDetailsBinding
    private lateinit var helperFunctions: HelperFunctions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       binding = FragmentYTCourseDetailsBinding.inflate(inflater,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}