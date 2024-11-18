package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import xyz.penpencil.competishun.ui.adapter.ExampleAdapter
import xyz.penpencil.competishun.utils.Constants
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentTargetBinding
import xyz.penpencil.competishun.ui.main.MainActivity

@AndroidEntryPoint
class TargetFragment : Fragment() {
    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var currentStep = 1
    private var isItemSelected = false

    private val dataSets = Constants.DATA_SETS
    private val pageTexts = Constants.PAGE_TEXTS
    private val stepTexts = Constants.STEP_TEXTS
    private val spanCount = listOf(2, 2, 1)

    private var selectedItem: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTargetBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        selectedItem = sharedPreferencesManager.targetYear.toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreSelectedItem()

        binding.TargetBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val loginType = arguments?.getString("loginType")
        binding.TargetNext.setOnClickListener {
            if (isItemSelected) {
                val bundle = Bundle().apply {
                    putString("loginType", loginType)
                }
                findNavController().navigate(R.id.action_TargetFragment_to_reference,bundle)
            } else {
                Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etStartedText.text = stepTexts[currentStep]
        binding.etText.text = pageTexts[currentStep]

        val exampleAdapter = ExampleAdapter(
            dataList = dataSets[currentStep],
            currentStep = currentStep,
            spanCount = spanCount[currentStep],
            selectedItem = selectedItem
        ) { selectedItem ->
            isItemSelected = true
            this.selectedItem = selectedItem
            sharedPreferencesManager.targetYear = selectedItem.toInt()
            binding.TargetNext.setBackgroundResource(R.drawable.second_getstarteddone)
            updateButtonBackground()
        }

        binding.targetRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }

        startSlideInAnimation()
        updateButtonBackground()
    }

    private fun restoreSelectedItem() {
        selectedItem = sharedPreferencesManager.targetYear.toString()
        Log.d("TargetFragmentRestore", "Selected Item: $selectedItem")
        if (selectedItem!="0") {
            isItemSelected = true
        } else {
            isItemSelected = false
        }
        updateButtonBackground()
    }

    private fun updateButtonBackground() {
        binding.TargetNext.setBackgroundResource(
            if (isItemSelected) R.drawable.second_getstarteddone
            else R.drawable.second_getstarted
        )
    }

    private fun startSlideInAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.clAnimConstraint.startAnimation(slideInAnimation)
    }

    private fun handleBackPressed() {
        findNavController().popBackStack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedItem", selectedItem)
    }

    override fun onResume() {
        super.onResume()
        updateButtonBackground()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
