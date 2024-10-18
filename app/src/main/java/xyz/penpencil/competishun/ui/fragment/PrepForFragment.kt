package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import xyz.penpencil.competishun.databinding.FragmentPrepForBinding
import xyz.penpencil.competishun.ui.main.MainActivity

@AndroidEntryPoint
class PrepForFragment : Fragment() {
    private var _binding: FragmentPrepForBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var currentStep = 0
    private var isItemSelected = false

    private val dataSets = Constants.DATA_SETS
    private val pageTexts = Constants.PAGE_TEXTS
    private val stepTexts = Constants.STEP_TEXTS
    private val spanCount = listOf(2, 2, 1)
    private var SharedSelectedItem: String? = null
    private var selectedItem: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrepForBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        selectedItem = sharedPreferencesManager.preparingFor

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreSelectedItem()

        binding.etContent.setText(sharedPreferencesManager.descriptionText)


        binding.PrepBack.setOnClickListener {
            findNavController().navigate(R.id.onboardingFragment)
        }
        val loginType = arguments?.getString("loginType")
        binding.PrepNext.setOnClickListener {
            if (isItemSelected) {
                sharedPreferencesManager.descriptionText = binding.etContent.text.toString()
                val bundle = Bundle().apply {
                    putString("loginType", loginType)
                }
                findNavController().navigate(R.id.action_PrepForFragment_to_TargetFragment,bundle)
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
            SharedSelectedItem = selectedItem
            sharedPreferencesManager.preparingFor = selectedItem
            binding.PrepNext.setBackgroundResource(R.drawable.second_getstarteddone)

            if (selectedItem == "Others") {
                binding.etContentBox.visibility = View.VISIBLE
            } else {
                binding.etContentBox.visibility = View.GONE
                isItemSelected = true
            }
            updateButtonBackground()
        }

        binding.prepRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }

        startSlideInAnimation()
        updateButtonBackground()
        setupCharacterCounter()
    }

    private fun restoreSelectedItem() {
        selectedItem = sharedPreferencesManager.preparingFor
        if (!selectedItem.isNullOrEmpty()) {
            isItemSelected = true
            if (selectedItem == "Others") {
                binding.etContentBox.visibility = View.VISIBLE
            } else {
                binding.etContentBox.visibility = View.GONE
            }
            updateButtonBackground()
        }
    }

    private fun setupCharacterCounter() {
        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0
                binding.tvCharCounter.text = "$charCount/100"

                sharedPreferencesManager.descriptionText = s.toString()

                if (charCount > 100) {
                    Toast.makeText(requireContext(), "Character limit exceeded. Maximum 100 characters allowed.", Toast.LENGTH_SHORT).show()
                    val trimmedText = s?.substring(0, 100)
                    binding.etContent.setText(trimmedText)
                    binding.etContent.setSelection(trimmedText?.length ?: 0)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


    private fun updateButtonBackground() {
        binding.PrepNext.setBackgroundResource(
            if (isItemSelected) R.drawable.second_getstarteddone
            else R.drawable.second_getstarted
        )
    }

    private fun startSlideInAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.clAnimConstraint.startAnimation(slideInAnimation)
    }

    private fun handleBackPressed() {
        findNavController().navigate(R.id.onboardingFragment)
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
