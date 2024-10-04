package xyz.penpencil.competishun.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.WelcomeModel
import xyz.penpencil.competishun.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var data1: WelcomeModel
    private lateinit var data2: WelcomeModel
    private lateinit var data3: WelcomeModel

    private var currentDataIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data1 = WelcomeModel(
            R.drawable.doubt_image,
            createStyledText(getString(R.string.instant_doubt_resolution), "Doubt"),
            resources.getString(R.string.doubt_description),
            R.drawable.doubt_icon
        )

        data2 = WelcomeModel(
            R.drawable.testseries_image,
            createStyledText(getString(R.string.comprehensive_test_series), "Test"),
            resources.getString(R.string.test_description),
            R.drawable.testseries_icon
        )

        data3 = WelcomeModel(
            R.drawable.evaluate_image,
            createStyledText(getString(R.string.evaluate_your_performances), "Performance"),
            resources.getString(R.string.evaluate_description),
            R.drawable.evaluate_icon
        )

        binding.etSkipText.setOnClickListener {
            navigateToLoginFragment()
        }

        updateData(data1)

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

    private fun createStyledText(fullText: String, highlightText: String): SpannableString {
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(highlightText)
        val endIndex = startIndex + highlightText.length

        if (startIndex >= 0) {
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF7A28")),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            Log.e("WelcomeFragment", "Highlight text not found: $highlightText")
        }

        return spannableString
    }

    private fun updateData(data: WelcomeModel) {
        Log.d("WelcomeFragment", "Updating data: ${data.title}")
        binding.etTitleText.text = data.title
        binding.etTitleDescription.text = data.description
        binding.etCenterImage.setImageResource(data.image)
        binding.imImageButton.setImageResource(data.skipImage)
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(R.id.action_onWelcomeFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
