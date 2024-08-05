package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.student.competishun.R
import com.student.competishun.databinding.FragmentHomeBinding
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.databinding.FragmentTargetBinding
import com.student.competishun.ui.adapter.ExampleAdapter
import com.student.competishun.ui.viewmodel.UpdateUserViewModel

class TargetFragment : Fragment() {
    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!


    private var list = mutableListOf<String>()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private val dataSets = listOf(
        listOf("IIT-JEE", "NEET", "Board", "UCET", "Others"),
        listOf("2025", "2026", "2027", "2028"),
        listOf("Friends/Family", "Social Media", "Advertisement", "Other")
    )

    private val pageTexts = listOf("2", "3", "4")
    private val stepTexts = listOf(
        "Which exam are you \npreparing for? Please select",
        "What is your target year?",
        "How do you know about \nCompetishun?"
    )
    private val spanCount = listOf(2, 2, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTargetBinding.inflate(inflater, container, false)
        return binding.root

        private fun setupRecyclerView() {
            adapter = ExampleAdapter(
                dataSets[currentStep],
                currentStep,
                spanCount[currentStep],
                onItemSelected = { selectedItem ->
                    Log.d("Adapter", "Selected item: $selectedItem")
                    list.add(selectedItem)
                    Log.d("List", list.toString())
                }
            )

            binding.examRecyclerview.layoutManager = GridLayoutManager(context, spanCount[currentStep])
            binding.examRecyclerview.adapter = adapter
        }


        //    private fun showExamSelection(currentStep: Int = this.currentStep) {
//        if (currentStep == 0) {
//            binding.etContentBox.visibility = View.VISIBLE
//            binding.clExamConstraint.visibility = View.VISIBLE
//            binding.etNameConstraint.visibility = View.GONE
//            binding.btnback.isEnabled = false
//            binding.btnback.backgroundTintMode = null
//            updateRecyclerViewData()
//            updateStepText()
//            updatePageText()
//        } else {
//            binding.etContentBox.visibility = View.GONE
//            binding.clExamConstraint.visibility = View.VISIBLE
//            binding.etNameConstraint.visibility = View.GONE
//            updateRecyclerViewData()
//            updateStepText()
//            updatePageText()
//        }
//    }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



private fun startSlideInAnimation() {
    val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
    binding.clAnimConstraint.startAnimation(slideInAnimation)
}

private fun updateStepText() {
    if (currentStep < stepTexts.size) {
        binding.etStartedText.text = stepTexts[currentStep]
    }
}

private fun updatePageText() {
    if (currentStep < pageTexts.size) {
        binding.etText.text = pageTexts[currentStep]
    }
}

private fun updateRecyclerViewData() {
    Log.d("currentStep", currentStep.toString())
    if (currentStep < dataSets.size) {
        val newSpanCount = spanCount.getOrNull(currentStep) ?: 1
        adapter.updateData(dataSets[currentStep], currentStep, newSpanCount)
        binding.examRecyclerview.layoutManager = GridLayoutManager(context, newSpanCount)
    }
}


private fun observeViewModel() {
    updateUserViewModel.updateUserResult.observe(viewLifecycleOwner) { result ->
        result?.let { user ->
            Log.e("updateUserResponse", result.toString())
            Log.e("gettingUserUpdate", user.user?.fullName.toString())
            Toast.makeText(context, "User update successful", Toast.LENGTH_SHORT).show()
            navigateToLoaderScreen()
        } ?: run {
            Log.e("gettingUserUpdate fail", result.toString())
            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
        }
    }
}


private fun navigateToLoaderScreen() {
    binding.root.removeAllViews()
    val processingView = layoutInflater.inflate(R.layout.loader_screen, binding.root, false)
    binding.root.addView(processingView)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            findNavController().navigate(R.id.action_OnBoardingFragment_to_HomeActivity)
//        }, 5000)
}
