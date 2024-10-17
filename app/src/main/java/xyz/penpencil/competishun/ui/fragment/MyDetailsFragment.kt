package xyz.penpencil.competishun.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.student.competishun.gatekeeper.type.UpdateUserInput
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentMyDetailsBinding
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import java.util.Calendar


@AndroidEntryPoint
class MyDetailsFragment : Fragment() {

    private lateinit var binding : FragmentMyDetailsBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var etDob: EditText
    private var helperFunctions  = HelperFunctions()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private var gender = ""

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
        binding = FragmentMyDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
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

            binding.etAddress.isEnabled = true
            binding.etAddress.setBackgroundResource(R.drawable.rounded_edittext_background)
            binding.etDob.isEnabled = true
            binding.etGender.isEnabled = true
        }
        binding.clSaveChanges.setOnClickListener {
            Toast.makeText(requireContext(),"Update Successfully",Toast.LENGTH_LONG).show()
        }
    }

    private fun showDatePickerDialog() {
        // Get current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            etDob.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showGenderDropdown(editText: EditText) {
        val popupMenu: PopupMenu = PopupMenu(requireContext(), editText, Gravity.NO_GRAVITY,0, xyz.penpencil.competishun.R.style.CustomPopupMenu)
        popupMenu.getMenu().add("Male")
        popupMenu.getMenu().add("Female")
        popupMenu.getMenu().add("Prefer not to say")

        popupMenu.setOnMenuItemClickListener { item ->
            val selectedGender: String = item.getTitle().toString()
            editText.setText(selectedGender)
            true
        }

        popupMenu.show()
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val name = data.getMyDetails.fullName
                val rollNo = data.getMyDetails.userInformation.rollNumber
                val phoneNo  = data.getMyDetails.mobileNumber
                val emailId = data.getMyDetails.email
                val joiningDate = helperFunctions.formatCourseDate(data.getMyDetails.createdAt.toString())
                val address = data.getMyDetails.userInformation.address?.addressLine1

                if(name!=null || rollNo!=null || phoneNo!=null || emailId!=null || joiningDate!=null || address!=null){
                    binding.etFullName.setText(name)
                    binding.etFullName.setBackgroundResource(R.drawable.rounded_filled_bg)
                    binding.etRollNumber.setText(rollNo)
                    binding.etRollNumber.setBackgroundResource(R.drawable.rounded_filled_bg)
                    binding.etEnterNoText.setText(phoneNo)
                    binding.etEnterNoText.setBackgroundResource(R.drawable.rounded_filled_bg)
                    binding.etEmail.setText(emailId)
                    binding.etEmail.setBackgroundResource(R.drawable.rounded_filled_bg)
                    binding.etJoiningDate.setText(joiningDate)
                    binding.etJoiningDate.setBackgroundResource(R.drawable.rounded_filled_bg)
                    binding.etAddress.setText(address)
                    binding.etAddress.setBackgroundResource(R.drawable.rounded_filled_bg)

                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


}