package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.ui.adapter.ExampleAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.api.ApiProcess
import xyz.penpencil.competishun.data.model.UpdateUserResponse
import xyz.penpencil.competishun.databinding.FragmentReferenceBinding
import xyz.penpencil.competishun.ui.main.MainActivity
import xyz.penpencil.competishun.utils.Constants


@AndroidEntryPoint
class ReferenceFragment : Fragment() {
    private var _binding: FragmentReferenceBinding? = null
    private val binding get() = _binding!!
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private var currentStep = 2
    private var isItemSelected = false
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val dataSets = Constants.DATA_SETS
    private val pageTexts = Constants.PAGE_TEXTS
    private val stepTexts = Constants.STEP_TEXTS
    private val spanCount = listOf(2, 2, 1)

    private var selectedItem: String? = null
    private var SharedSelectedItem: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReferenceBinding.inflate(inflater, container, false)

        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        // Retrieve the saved reference from SharedPreferences
        selectedItem = sharedPreferencesManager.reference
        SharedSelectedItem = selectedItem

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedItem = sharedPreferencesManager.reference
        SharedSelectedItem = selectedItem

        restoreSelectedItem()

        binding.RefNext.text = "Done"

        binding.RefBack.setOnClickListener {
            println("ClickedBacktoTarget")
            findNavController().navigate(R.id.TargetFragment)
        }

        binding.RefNext.setOnClickListener {
            if (isItemSelected) {
                // Save to SharedPreferences only when "Done" is clicked
                sharedPreferencesManager.reference = SharedSelectedItem

                sharedPreferencesManager.isReferenceSelectionInProgress = false
                Log.e("emailpresen",sharedPreferencesManager.email.toString())
                val loginType = arguments?.getString("loginType")
                if (loginType != null && loginType == "email" ) {
                    Log.e("emailType",sharedPreferencesManager.email.toString())
                    val updateUserInput = UpdateUserInput(
                        city = Optional.Present(sharedPreferencesManager.city),
                        fullName = Optional.Present(sharedPreferencesManager.name),
                        preparingFor = Optional.Present(sharedPreferencesManager.preparingFor),
                        reference = Optional.Present(sharedPreferencesManager.reference),
                        mobileNumber = Optional.present(sharedPreferencesManager.mobileNo),
                        countryCode = Optional.present("+91"),
                        email = Optional.present(sharedPreferencesManager.email),
                        targetYear = Optional.Present(sharedPreferencesManager.targetYear),
                    )
                    updateUserViewModel.updateUserErrorHandled(updateUserInput,null,null)
                } else {
                    Log.e("nopresenELSE",sharedPreferencesManager.mobileNo.toString())
                      val updateUserInput = UpdateUserInput(
                        city = Optional.Present(sharedPreferencesManager.city),
                        fullName = Optional.Present(sharedPreferencesManager.name),
                        preparingFor = Optional.Present(sharedPreferencesManager.preparingFor),
                        reference = Optional.Present(sharedPreferencesManager.reference),
                          email = Optional.present(sharedPreferencesManager.email),
                        targetYear = Optional.Present(sharedPreferencesManager.targetYear),
                    )
                    updateUserViewModel.updateUserErrorHandled(updateUserInput,null,null)
                }



            } else {
                Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        updateUserViewModel.updateUserErrorHandledResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ApiProcess.Success -> {
                    val data: UpdateUserResponse = result.data
                    data.user?.userInformation?.let { userInfo ->
                        Log.e("gettingUserUpdateTarget", userInfo.targetYear?.toString() ?: "null")
                        Log.e("gettingUserUpdaterefer", userInfo.reference?.toString() ?: "null")
                        Log.e("gettingUserUpdateprep", userInfo.preparingFor?.toString() ?: "null")
                        Log.e("gettingUseremai", data.user.email ?: "null")
                        Log.e("gettingUserno", data.user.mobileNumber ?: "null")
                        Log.e("gettingUserstate", data.user.userInformation.address?.state ?: "null")
                    }

                    // Call the navigation method after successful update
                    navigateToLoaderScreen()
                }
                is ApiProcess.Failure -> {
                    Log.e("gettingUserUpdatefail", "Error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
                ApiProcess.Loading -> {
                    // Handle loading state if needed
                    Log.d("UpdateUser", "Loading user update...")
                }
                else -> {
                    Log.e("gettingUserUpdatefail", "Unexpected result")
                }
            }
        })


        binding.etStartedText.text = stepTexts[currentStep]
        binding.etText.text = pageTexts[currentStep]

        setupRecyclerView()

        startSlideInAnimation()

        updateButtonBackground()
    }

    private fun restoreSelectedItem() {
        selectedItem = sharedPreferencesManager.reference
        SharedSelectedItem = selectedItem
        if (!selectedItem.isNullOrEmpty()) {
            isItemSelected = true
            if (selectedItem == "Other") {
                binding.etContentBox.visibility = View.VISIBLE
            } else {
                binding.etContentBox.visibility = View.GONE
            }
        } else {
            isItemSelected = false
        }
        updateButtonBackground()
    }

    override fun onResume() {
        super.onResume()
        updateButtonBackground()
    }

    private fun setupRecyclerView() {
        val exampleAdapter = ExampleAdapter(
            dataList = dataSets[currentStep],
            currentStep = currentStep,
            spanCount = spanCount[currentStep],
            selectedItem = selectedItem
        ) { selectedItem ->
            isItemSelected = true
            this.selectedItem = selectedItem
            SharedSelectedItem = selectedItem
           // sharedPreferencesManager.isReferenceSelectionInProgress = true
            sharedPreferencesManager.reference = selectedItem
            Log.e("selectedItem",selectedItem)

            if (selectedItem == "Other") {
                binding.etContentBox.visibility = View.VISIBLE
            } else {
                binding.etContentBox.visibility = View.GONE
                isItemSelected = true
            }
            updateButtonBackground()
        }

        binding.refRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }
    }


    private fun updateButtonBackground() {
        if (isItemSelected) {
            binding.RefNext.setBackgroundResource(R.drawable.second_getstarteddone)
        } else {
            binding.RefNext.setBackgroundResource(R.drawable.second_getstarted)
        }
    }

    private fun startSlideInAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.clAnimConstraint.startAnimation(slideInAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleBackPressed() {
        findNavController().popBackStack(R.id.TargetFragment, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SharedSelectedItem", SharedSelectedItem)
    }

    private fun navigateToLoaderScreen() {
        binding.root.removeAllViews()
        val processingView = layoutInflater.inflate(R.layout.loader_screen, binding.root, false)
        binding.root.addView(processingView)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }, 5000)
    }
}
