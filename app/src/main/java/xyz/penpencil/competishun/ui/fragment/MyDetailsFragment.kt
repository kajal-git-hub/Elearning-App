package xyz.penpencil.competishun.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.student.competishun.gatekeeper.type.UpdateUserInput
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentMyDetailsBinding
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MyDetailsFragment : Fragment() {

    private lateinit var binding: FragmentMyDetailsBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var etDob: EditText
    private var helperFunctions = HelperFunctions()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private var gender = ""
    private var dob = ""

    private var name = ""
    private var rollNo = ""
    private var phoneNo = ""
    private var emailId = ""
    private var joiningDate = ""
    private var address = ""
    private var day = 0
    private var month = 0
    private var year = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        gender = binding.etGender.text.toString()
//        dob = binding.etDob.text.toString()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })





        observeUserDetails()
        userViewModel.fetchUserDetails()

        binding.etBTDetails.setOnClickListener {
            findNavController().popBackStack()
        }

        val etGender = binding.etGender

        binding.etGender.setOnClickListener {
            showGenderDropdown(etGender)
        }

        etDob = binding.etDob
        binding.etDob.setOnClickListener {
            showDatePickerDialog()
        }

        binding.clNowEdit.setOnClickListener {
            binding.clNowEdit.visibility = View.GONE
            binding.clSaveChanges.visibility = View.VISIBLE

            binding.etDob.isEnabled = true
            binding.etDob.setBackgroundResource(R.drawable.rounded_edittext_background)
            binding.etGender.isEnabled = true
            binding.etGender.setBackgroundResource(R.drawable.rounded_edittext_background)
        }
        binding.clSaveChanges.setOnClickListener {
            binding.etDob.setText(dob)
            binding.etGender.setText(gender)
            val updateUserInput = UpdateUserInput(
                dob = Optional.present(dob),
                gender = Optional.present(gender),
            )
            updateUserViewModel.updateUser(updateUserInput, null, null)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                dob = dateFormat.format(selectedDate.time)

                binding.etDob.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
            }, year, month, day)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun showGenderDropdown(editText: EditText) {
        val popupMenu: PopupMenu = PopupMenu(
            requireContext(),
            editText,
            Gravity.NO_GRAVITY,
            0,
            xyz.penpencil.competishun.R.style.CustomPopupMenu
        )
        popupMenu.menu.add("Male")
        popupMenu.menu.add("Female")
        popupMenu.menu.add("Prefer not to say")

        popupMenu.setOnMenuItemClickListener { item ->
            val selectedGender: String = item.title.toString()
            gender = selectedGender
            editText.setText(selectedGender)
            true
        }

        popupMenu.show()
    }

    private fun observeUserDetails() {
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result != null) {
                Toast.makeText(requireContext(), "Update Successfully", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_LONG).show()
            }
        })

        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                name = data.getMyDetails.fullName.toString()
                rollNo = data.getMyDetails.userInformation.rollNumber.toString()
                phoneNo = data.getMyDetails.mobileNumber.toString()
                emailId = data.getMyDetails.email.toString()
                joiningDate =
                    helperFunctions.formatCourseDate(data.getMyDetails.createdAt.toString())
                address = data.getMyDetails.userInformation.address?.addressLine1.toString()
                dob = data.getMyDetails.userInformation.dob.toString()
                gender = data.getMyDetails.userInformation.gender.toString()

                Log.d("address", address)



                binding.etDob.setText(if (dob != null && dob != "null") helperFunctions.formatCourseDate(dob) else "")
                binding.etDob.setBackgroundResource(if (dob != null && dob != "null") {
                    R.drawable.rounded_filled_bg
                } else {
                    R.drawable.rounded_edittext_background
                })
                binding.etGender.setText(if(gender!=null && gender!="null")gender else "")

                binding.etGender.setBackgroundResource(if (gender != null && gender != "null") {
                    R.drawable.rounded_filled_bg
                } else {
                    R.drawable.rounded_edittext_background
                })


                binding.etFullName.setText(if (name != null && name != "null") name else "")
                binding.etFullName.setBackgroundResource(R.drawable.rounded_filled_bg)

                binding.etRollNumber.setText(if (rollNo != null && rollNo != "null") rollNo else "")
                binding.etRollNumber.setBackgroundResource(R.drawable.rounded_filled_bg)

                binding.etEnterNoText.setText(if (phoneNo != null && phoneNo != "null") phoneNo else "")
                binding.etEnterNoText.setBackgroundResource(R.drawable.rounded_filled_bg)

                binding.etEmail.setText(if (emailId != null && emailId != "null") emailId else "")
                binding.etEmail.setBackgroundResource(R.drawable.rounded_filled_bg)

                binding.etJoiningDate.setText(if (joiningDate != null && joiningDate != "null") joiningDate else "")
                binding.etJoiningDate.setBackgroundResource(R.drawable.rounded_filled_bg)

                Log.d("addressBelow", address ?: "null")
                binding.etAddress.setText(if (address != null && address != "null") address else "")
                binding.etAddress.setBackgroundResource(R.drawable.rounded_filled_bg)

            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error fetching details: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}