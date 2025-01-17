package xyz.penpencil.competishun.ui.fragment


import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.student.competishun.gatekeeper.MyDetailsQuery
import com.student.competishun.gatekeeper.type.UpdateUserInput
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentPersonalDetailBinding
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.MyCoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager


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
    private var fatherNumber = ""
    private var updateUserInput: UpdateUserInput? = null
    private var fieldsToVisible = mutableListOf<String>()
    private var bottomSheetTSizeFragment = BottomSheetTSizeFragment()
    private var courseId: String = ""
    var userDetails: MyDetailsQuery.GetMyDetails?=null
    var isActive = false

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
        bottomSheetTSizeFragment.dismiss()
        updateButtonState()
    }

    private fun formRootStatus(isRunning: Boolean){
        if (isRunning){
            binding.formRoot.visibility = View.GONE
            binding.progress.visibility = View.VISIBLE
        }else {
            binding.formRoot.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        fieldsToVisible = arguments?.getStringArray("FIELD_REQUIRED")?.toMutableList()?: mutableListOf()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        Log.e("dsaashdsad", "onViewCreated: " +fieldsToVisible )
        updateUIVisibility()
        initObserver()
        populateSavedData()

        // Show bottom sheet description if not shown before
        showBottomSheetIfFirstTime()
        fetchData()


        binding.spinnerTshirtSize.setOnClickListener {
            showBottomSheetTSize()
        }

        binding.btnAddDetails.setOnClickListener {
            if (isFormValid()) {
                isActive = true
                updateData()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etFullName.addTextChangedListener(textWatcher)
        binding.etFathersNumber.addTextChangedListener(textWatcher)
        binding.etFathersName.addTextChangedListener(textWatcher)
        binding.etWhatsappNumber.addTextChangedListener(mobileNumberTextWatcher)

        updateButtonState()


        view.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    findNavController().let { nav->
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(nav.graph.startDestinationId, true)
                            .build()
                        nav.navigate(R.id.courseEmptyFragment, Bundle().apply {
                        }, navOptions)

                    }
                    return true
                }
                return false
            }
        })
    }

    private fun initObserver(){
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                userDetails = data.getMyDetails
                initializeUpdateUI()
            }.onFailure {
                Log.e("Error fetching details", it.message.toString())
            }
        }

        formRootStatus(isRunning = true)
        myCoursesViewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                formRootStatus(isRunning = false)
                updateUIVisibility()
            }.onFailure {
                formRootStatus(isRunning = false)
                Log.e("MyCoursesFail", it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner) { result ->
            if (result?.user != null && isActive) {
                isActive = false
                findNavController().let { nav ->
                    nav.navigate(getFragmentId(), Bundle().apply {
                        putStringArray("FIELD_REQUIRED", fieldsToVisible.toTypedArray())
                    })
                }
                Log.e("User update success", result.user.toString())
            } else {
                Log.e("User update failed", result.toString())
            }
        }
    }

    private fun removeObserver(){
        updateUserViewModel.updateUserResult.removeObservers(viewLifecycleOwner)
    }

    override fun onStop() {
        removeObserver()
        super.onStop()
    }

    private fun updateData(){
        updateUserInput = UpdateUserInput(
            city = Optional.Present(userDetails?.userInformation?.city),
            fullName = Optional.Present(userDetails?.fullName),
            preparingFor = Optional.Present(userDetails?.userInformation?.preparingFor),
            reference = Optional.Present(userDetails?.userInformation?.reference),
            targetYear = Optional.Present(userDetails?.userInformation?.targetYear),
            waCountryCode = Optional.Present("+91"),
            waMobileNumber = Optional.Present(whatsappNumber),
            fatherName = Optional.Present(fatherName),
            tShirtSize = Optional.Present(tShirtSize),
            fatherMobileNumber = Optional.Present(fatherNumber)
        )
        userUpdate(updateUserInput, null, null)
    }

    private fun fetchData(){
        userViewModel.fetchUserDetails()
        myCoursesViewModel.fetchMyCourses()
    }

    private fun getFragmentId(): Int {
        if (fieldsToVisible.isEmpty()){
            sharedPreferencesManager.putBoolean(courseId, true)
            sharedPreferencesManager.putString("current$courseId", "")
            R.id.courseEmptyFragment
        }
        return if (fieldsToVisible.contains("PASSPORT_SIZE_PHOTO") || fieldsToVisible.contains("AADHAR_CARD")) {
            sharedPreferencesManager.putString("current$courseId", "document")
            R.id.AdditionalDetailsFragment
        }else if (fieldsToVisible.contains("FULL_ADDRESS")){
            sharedPreferencesManager.putString("current$courseId", "address")
            R.id.AddressDetailFragment
        }else {
            sharedPreferencesManager.putString("current$courseId", "")
            sharedPreferencesManager.putBoolean(courseId, true)
            R.id.courseEmptyFragment
        }
    }

    private fun updateUIVisibility() {
        // Update visibility based on fieldsToVisible list
/*        binding.tvFathersNameLabel.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE
        binding.etFathersName.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE
        binding.tvWhatsappNumberLabel.visibility = if (fieldsToVisible.contains("WHATSAPP_NUMBER")) View.VISIBLE else View.GONE
        binding.etWhatsappNumber.visibility = if (fieldsToVisible.contains("WHATSAPP_NUMBER")) View.VISIBLE else View.GONE

        binding.tvFathersNumberLabel.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE
        binding.etFathersNumber.visibility = if (fieldsToVisible.contains("FATHERS_NAME")) View.VISIBLE else View.GONE

        binding.tvTshirtSizeLabel.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE
        binding.spinnerTshirtSize.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE
        binding.tvTshirtNote.visibility = if (fieldsToVisible.contains("T_SHIRTS")) View.VISIBLE else View.GONE*/

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
            bottomSheetTSizeFragment = BottomSheetTSizeFragment().apply {
                setOnTSizeSelectedListener(this@PersonalDetailsFragment)
                arguments = Bundle().apply {
                    putString("selectedSize", tShirtSize)
                }
            }
            bottomSheetTSizeFragment.show(childFragmentManager, "BottomSheetTSizeFragment")
        }
    }

    /**
     * populate UI
     * */
    private fun initializeUpdateUI(){
        binding.etFullName.setText(userDetails?.fullName?:"")
        binding.etFathersName.setText(userDetails?.userInformation?.fatherName?:"")
        binding.etFathersNumber.setText(userDetails?.userInformation?.fatherMobileNumber?:"")
        binding.etWhatsappNumber.setText(userDetails?.mobileNumber?:"")
        binding.spinnerTshirtSize.text = "Select size"
        if (tShirtSize.isNotEmpty()) {
            userDetails?.userInformation?.tShirtSize?.let {
                binding.spinnerTshirtSize.text = it
                isTshirtSizeSelected = true
            }
        }
    }

    /**
     * send data to server
     *  and move to next screen
     * */
    private fun userUpdate(updateUserInput: UpdateUserInput?, documentPhotoFile: String?, passportPhotoFile: String?) {
        updateUserInput?.let {
            updateUserViewModel.updateUser(it, documentPhotoFile, passportPhotoFile)
        }
    }

    private fun isFormValid(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        fatherNumber = binding.etFathersNumber.text.toString().trim()
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
        val isFatherNumberValid = if (binding.etFathersNumber.visibility == View.VISIBLE) fatherNumber.isNotEmpty() else true

        return fullName.isNotEmpty()
                && isFatherNameValid
                && isWhatsappNumberValid
                && isTshirtSizeValid
                && isFatherNumberValid
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
