package xyz.penpencil.competishun.ui.fragment


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
import com.apollographql.apollo3.api.Optional
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.MyCoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentPersonalDetailBinding

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
    private var updateUserInput: UpdateUserInput? = null
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

        sharedPreferencesManager.shirtSize = tShirtSize
        updateButtonState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        // Populate saved data from SharedPreferences
        populateSavedData()

        // Show bottom sheet description if not shown before
        showBottomSheetIfFirstTime()

        // Fetch courses and then update visibility
        fetchCoursesAndUpdateUI()

        userViewModel.fetchUserDetails()

        binding.spinnerTshirtSize.setOnClickListener {
            showBottomSheetTSize()
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

        updateButtonState()
    }

    private fun fetchCoursesAndUpdateUI() {
        myCoursesViewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                fieldsToVisible.clear()
                data.myCourses?.forEach { courselist ->
                    courselist.course.other_requirements?.let { requirements ->
                        fieldsToVisible.addAll(requirements.map { it.toString() })
                    }
                    Log.d("fieldsToVisible",fieldsToVisible.toString())
                }
                // Update UI visibility after fetching courses
                updateUIVisibility()
            }.onFailure {
                Log.e("MyCoursesFail", it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch the courses
        myCoursesViewModel.fetchMyCourses()
    }

    private fun updateUIVisibility() {
        // Update visibility based on fieldsToVisible list
        binding.tvFathersNameLabel.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE
        binding.etFathersName.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE
        binding.tvWhatsappNumberLabel.visibility = if (fieldsToVisible.contains("WHATSAPP_NUMBER")) View.VISIBLE else View.GONE
        binding.etWhatsappNumber.visibility = if (fieldsToVisible.contains("WHATSAPP_NUMBER")) View.VISIBLE else View.GONE
        binding.tvTshirtSizeLabel.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE
        binding.spinnerTshirtSize.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE
        binding.tvTshirtNote.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE

        updateButtonState()
    }

    private fun populateSavedData() {
        binding.etFullName.setText(sharedPreferencesManager.name ?: "")
        binding.etFathersName.setText(sharedPreferencesManager.fatherName ?: "")
        binding.etWhatsappNumber.setText(sharedPreferencesManager.whatsUpNo ?: "")
        tShirtSize = sharedPreferencesManager.shirtSize ?: ""
        if (tShirtSize.isNotEmpty()) {
            binding.spinnerTshirtSize.text = tShirtSize
            isTshirtSizeSelected = true
        }
    }

    private fun showBottomSheetIfFirstTime() {
        if (!sharedPreferencesManager.isBottomSheetShown) {
            val bottomSheetDescriptionFragment = BottomSheetPersonalDetailsFragment()
            bottomSheetDescriptionFragment.show(childFragmentManager, "BottomSheetDescriptionFragment")
            sharedPreferencesManager.isBottomSheetShown = true
        }
    }

    private fun showBottomSheetTSize() {
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

    private fun updateUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                updateUserInput = UpdateUserInput(
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
                userUpdate(updateUserInput, null, null)
                sharedPreferencesManager.name = userDetails.fullName
            }.onFailure {
                Log.e("Error fetching details", it.message.toString())
            }
        }
    }

    private fun userUpdate(updateUserInput: UpdateUserInput?, documentPhotoFile: String?, passportPhotoFile: String?) {
        updateUserInput?.let {
            updateUserViewModel.updateUser(it, documentPhotoFile, passportPhotoFile)
        }
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result?.user != null) {
                Log.e("User update success", result.user.toString())
            } else {
                Log.e("User update failed", result.toString())
            }
        })
    }

    private fun isFormValid(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        fatherName = binding.etFathersName.text.toString().trim()
        sharedPreferencesManager.fatherName = fatherName
        whatsappNumber = binding.etWhatsappNumber.text.toString().trim()
        sharedPreferencesManager.whatsUpNo = whatsappNumber
        tShirtSize = binding.spinnerTshirtSize.text.toString().trim()

        binding.etWhatsappNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))

        // Only check fields that are visible
        val isFatherNameValid = if (binding.etFathersName.visibility == View.VISIBLE) fatherName.isNotEmpty() else true
        val isWhatsappNumberValid = if (binding.etWhatsappNumber.visibility == View.VISIBLE) whatsappNumber.isNotEmpty() else true
        val isTshirtSizeValid = if (binding.spinnerTshirtSize.visibility == View.VISIBLE) tShirtSize.isNotEmpty() else true

        return fullName.isNotEmpty() && isFatherNameValid && isWhatsappNumberValid && isTshirtSizeValid
    }

    private fun updateButtonState() {
        if (isFormValid()) {
            binding.btnAddDetails.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.blue_3E3EF7)
            )
            binding.btnAddDetails.isEnabled = true
        } else {
            binding.btnAddDetails.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.gray_border)
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
            if (s?.length ?: 0 > 10) {
                Toast.makeText(requireContext(), "Enter valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
