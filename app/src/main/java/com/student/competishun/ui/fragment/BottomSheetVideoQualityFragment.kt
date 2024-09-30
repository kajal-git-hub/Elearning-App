package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.data.model.VideoQualityItem
import com.student.competishun.databinding.FragmentBottomSheetVideoQualityBinding
import com.student.competishun.ui.adapter.SelectExamAdapter
import com.student.competishun.ui.adapter.VideoQualityAdapter

class BottomSheetVideoQualityFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomSheetVideoQualityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBottomSheetVideoQualityBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val videoQualityList = listOf(
            VideoQualityItem("360p", "48.5 MB"),
            VideoQualityItem("480p", "480.5 MB"),
            VideoQualityItem("720p", "1.29 GB"),
            VideoQualityItem("1080p", "2.45 GB"),
        )


        binding.rvVideoQualityTypes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = VideoQualityAdapter(context, videoQualityList)
        }


    }

}