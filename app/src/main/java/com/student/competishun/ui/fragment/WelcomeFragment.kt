package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.student.competishun.data.model.WelcomeModel
import com.student.competishun.R
import com.student.competishun.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private val data1 = WelcomeModel(
        R.drawable.doubt_image,
        R.string.instant_doubt_resolution.toString(),
        R.string.doubt_description.toString(),
        R.drawable.doubt_icon
    )

    private val data2 = WelcomeModel(
        R.drawable.testseries_image,
        R.string.comprehensive_test_series.toString(),
        R.string.test_description.toString(),
        R.drawable.testseries_icon
    )

    private val data3 = WelcomeModel(
        R.drawable.evaluate_image,
        R.string.evaluate_your_performances.toString(),
        R.string.evaluate_description.toString(),
        R.drawable.evaluate_icon
    )

    private var currentDataIndex = 0  // Track which data set is currently displayed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etSkipText.setOnClickListener {
            navigateToLoginFragment()
        }

        // Set initial data
        updateData(data1)

        // Handle click on imImageButton
        binding.imImageButton.setOnClickListener {
            when (currentDataIndex) {
                0 -> {
                    updateData(data2)
                    currentDataIndex = 1
                }

                1 -> {
                    updateData(data3)
                    currentDataIndex = 2
                }

                2 -> {
                    navigateToLoginFragment()
                }
            }
        }
    }

    // Function to update views with new data
    private fun updateData(data: WelcomeModel) {
        binding.etTitleText.text = data.title
        binding.etTitleDescription.text = data.description
        binding.etCenterImage.setImageResource(data.image)
        binding.imImageButton.setImageResource(data.skipImage)
    }

    // Function to navigate to the login fragment
    private fun navigateToLoginFragment() {
        findNavController().navigate(R.id.action_onWelcomeFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
