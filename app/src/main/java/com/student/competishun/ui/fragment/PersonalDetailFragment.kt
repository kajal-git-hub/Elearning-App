package com.student.competishun.ui.fragment


import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.R
import com.student.competishun.data.model.ExploreCourse
import com.student.competishun.databinding.FragmentPersonalDetailBinding
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.ui.adapter.ExploreCourseAdapter
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import com.student.competishun.ui.viewmodel.UpdateUserViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PersonalDetailsFragment : Fragment(), BottomSheetTSizeFragment.OnTSizeSelectedListener {

    private val userViewModel: UserViewModel by viewModels()
    private val myCoursesViewModel: MyCoursesViewModel by viewModels()

    private var _binding: FragmentPersonalDetailBinding? = null
    private val binding get() = _binding!!
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private var isTshirtSizeSelected = false
    private var selectedTShirtSize: String? = null
    var isBottomSheetShowing = false
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var fatherName = ""
    private var whatsappNumber = ""
    private var tShirtSize = ""
    private var fieldsToVisible = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onTSizeSelected(size: String) {
        binding.spinnerTshirtSize.text = size
        tShirtSize = size
        isTshirtSizeSelected = true
        updateButtonState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myCourses()
        Log.d("fieldsToVisible",fieldsToVisible.toString())

        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val bottomSheetDescriptionFragment = BottomSheetPersonalDetailsFragment()
        bottomSheetDescriptionFragment.show(childFragmentManager, "BottomSheetDescriptionFragment")

        userViewModel.fetchUserDetails()

        binding.spinnerTshirtSize.setOnClickListener {
            if (!isBottomSheetShowing) {
                isBottomSheetShowing = true
                val bottomSheet = BottomSheetTSizeFragment().apply {
                    setOnTSizeSelectedListener(this@PersonalDetailsFragment)
                    arguments = Bundle().apply {
                        putString("selectedSize", tShirtSize)
                    }
                }
                bottomSheet.show(childFragmentManager, "BottomSheetTSizeFragment")
            }
        }

        binding.btnAddDetails.setOnClickListener {
            if (isFormValid()) {
                updateUserDetails()
                findNavController().navigate(R.id.action_PersonalDetails_to_AdditionalDetail)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etFullName.addTextChangedListener(textWatcher)
        binding.etFathersName.addTextChangedListener(textWatcher)
        binding.etWhatsappNumber.addTextChangedListener(mobileNumberTextWatcher)

        updateUIVisibility()
    }

    private fun updateUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                Log.e("userDetails",userDetails.toString())
                val updateUserInput = UpdateUserInput(
                    city = Optional.Present(userDetails.userInformation.city),
                    fullName = Optional.Present(userDetails.fullName),
                    preparingFor = Optional.Present(userDetails.userInformation.preparingFor),
                    reference = Optional.Present(userDetails.userInformation.reference),
                    targetYear = Optional.Present(userDetails.userInformation.targetYear),
                    waCountryCode = Optional.Present("+91"),
                    waMobileNumber = Optional.Present(whatsappNumber),
                    fatherName = Optional.Present(fatherName),
                    tShirtSize = Optional.Present(tShirtSize)
                )
                Log.d("updateUserInput",updateUserInput.toString())
                sharedPreferencesManager.name = userDetails.fullName

                userUpdate(updateUserInput,null,null)
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error fetching details: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun userUpdate(updateUserInput:UpdateUserInput,documentPhotoFile: File?,passportPhotoFile: File?){
        updateUserViewModel.updateUser(updateUserInput,documentPhotoFile,passportPhotoFile)
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result?.user != null) {
                Log.e("gettingUserUpdateTarget", result.user.userInformation.targetYear.toString())
                Log.e("gettingUserUpdaterefer", result.user.userInformation.reference.toString())
                Log.e("gettingUserUpdateprep", result.user.userInformation.preparingFor.toString())
                Log.e("gettingUserUpdatecity", result.user.userInformation.city.toString())

            } else {
                Log.e("gettingUserUpdatefail", result.toString())
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isFormValid(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        fatherName = binding.etFathersName.text.toString().trim()
        whatsappNumber = binding.etWhatsappNumber.text.toString().trim()
        tShirtSize = binding.spinnerTshirtSize.text.toString().trim()
        binding.etWhatsappNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))

        return fullName.isNotEmpty() && fatherName.isNotEmpty() && whatsappNumber.isNotEmpty() && isTshirtSizeSelected && tShirtSize.isNotEmpty()
    }

    private fun updateButtonState() {
        if (isFormValid()) {
            binding.btnAddDetails.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue_3E3EF7
                )
            )
            binding.btnAddDetails.isEnabled = true
        } else {
            binding.btnAddDetails.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_border
                )
            )
            binding.btnAddDetails.isEnabled = false
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val mobileNumberTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null && s.length > 10) {
                Toast.makeText(requireContext(), "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                val trimmed = s.substring(0, 10)
                binding.etWhatsappNumber.setText(trimmed)
                binding.etWhatsappNumber.setSelection(trimmed.length)
            }
        }
    }

    fun myCourses(){
        myCoursesViewModel.myCourses.observe(viewLifecycleOwner) { result ->
            Log.e("getMyresule",result.toString())
            result.onSuccess { data ->
                Log.e("getMyCourses",data.toString())
                data.myCourses?.forEach { courselist ->
                    courselist.course.other_requirements?.let { requirements ->
                        fieldsToVisible.addAll(requirements.map { it.toString() })
                    }
                }
                updateUIVisibility()

            }.onFailure {
                Log.e("MyCoursesFail",it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        myCoursesViewModel.fetchMyCourses()
    }

    private fun updateUIVisibility() {
        binding.tvFathersNameLabel.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE
        binding.etFathersName.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE
        binding.tvWhatsappNumberLabel.visibility = if (fieldsToVisible.contains("WHATSAPP_NUMBER")) View.VISIBLE else View.GONE
        binding.etWhatsappNumber.visibility = if (fieldsToVisible.contains("WHATSAPP_NUMBER")) View.VISIBLE else View.GONE
        binding.tvTshirtSizeLabel.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE
        binding.spinnerTshirtSize.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
