package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.student.competishun.data.model.WelcomeModel
import com.student.competishun.R
import com.student.competishun.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    // Data model to hold different sets of content

    // Example data sets
    private val data1 = WelcomeModel(
        R.drawable.doubt_image,
        resources.getString(R.string.evaluate_txt),
        "Description for test series 1",
        R.drawable.doubt_icon
    )

    private val data2 = WelcomeModel(
        R.drawable.testseries_image,
        resources.getString(R.string.evaluate_txt),
        "Description for test series 1",
        R.drawable.testseries_icon
    )
    private val data3 = WelcomeModel(
        R.drawable.evaluate_image,
        "Comprehensive Test Series 1",
        "Description for test series 1",
        R.drawable.evaluate_icon
    )

    private var showingData1 = true  // Track which data set is currently displayed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set initial data
        updateData(data1)

        // Handle click on im_imageButton
        binding.imImageButton.setOnClickListener {
            // Toggle between data sets
            if (showingData1) {
                updateData(data2)
                showingData1 = false
            } else {
                updateData(data1)
                showingData1 = true
            }
        }
    }

    // Function to update views with new data
    private fun updateData(data: WelcomeModel) {
        binding.etTitleText.text = data.title
        binding.etTitleDescription.text = data.description
        binding.etCenterImage.setImageResource(data.image)
        binding.etCenterImage.setImageResource(data.skipImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
