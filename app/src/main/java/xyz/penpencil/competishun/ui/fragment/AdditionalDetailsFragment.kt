package xyz.penpencil.competishun.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.google.android.material.snackbar.Snackbar
import com.student.competishun.gatekeeper.MyDetailsQuery
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentAdditionalDetailBinding
import xyz.penpencil.competishun.utils.FileUtils
import xyz.penpencil.competishun.utils.FileUtils.getFileName
import xyz.penpencil.competishun.utils.FileUtils.getFileSizeMB
import xyz.penpencil.competishun.utils.SharedPreferencesManager

@AndroidEntryPoint
class AdditionalDetailsFragment : DrawerVisibility() {

    private val binding by lazy { FragmentAdditionalDetailBinding.inflate(layoutInflater) }

    private val userViewModel: UserViewModel by viewModels()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private var fieldsToVisible = arrayOf<String>()
    private var courseId: String = ""
    private val MAX_FILE_SIZE_MB = 5

    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var currentFileType: String = ""
    private var uploadedIdUri: Uri? = null
    private var uploadedPhotoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListeners()
        retrieveArguments()
    }

    private fun initUI() {
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        (activity as? HomeActivity)?.apply {
            showBottomNavigationView(false)
            showFloatingButton(false)
        }
    }

    private fun initListeners() {
        binding.etBTHomeAddress.setOnClickListener {
            it.findNavController().navigate(R.id.PersonalDetailsFragment)
        }

        binding.clUploadId.setOnClickListener {
            currentFileType = "ID"
            mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.clUploadPhoto.setOnClickListener {
            currentFileType = "PHOTO"
            mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnAddaddressDetails.setOnClickListener {
            handleUserUpdate()
        }

        binding.closeButton.setOnClickListener {
            resetIdUpload()
        }

        binding.closeButtonPass.setOnClickListener {
            resetPhotoUpload()
        }
    }

    private fun retrieveArguments() {
        arguments?.let {
            fieldsToVisible = it.getStringArray("FIELD_REQUIRED") ?: emptyArray()
            Log.e("LIST_OF_FIELDS", "retrieveArguments: $fieldsToVisible")
            toggleFieldVisibility()
        }
    }

    private fun toggleFieldVisibility() {
        binding.clUploadPhoto.isVisible = fieldsToVisible.contains("PASSPORT_SIZE_PHOTO")
        binding.clUploadId.isVisible = fieldsToVisible.contains("AADHAR_CARD")
    }

    private fun handleUserUpdate() {
        binding.progress.visibility = View.VISIBLE
        updateButtonState(false)
        userViewModel.fetchUserDetails()
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                updateUser(data.getMyDetails)
            }
        }
    }

    private fun updateUser(input: MyDetailsQuery.GetMyDetails) {

        val updateUserInput = UpdateUserInput(
            city = Optional.Present(input.userInformation.city),
            fullName = Optional.Present(input.fullName),
            preparingFor = Optional.Present(input.userInformation.preparingFor),
            reference = Optional.Present(input.userInformation.reference),
            targetYear = Optional.Present(input.userInformation.targetYear),
        )
        userUpdate(
            updateUserInput,
            uploadedIdUri?.let { FileUtils.getFileFromUri(requireContext(), it)?.absolutePath },
            uploadedPhotoUri?.let { FileUtils.getFileFromUri(requireContext(), it)?.absolutePath }
        )
    }

    private fun userUpdate(
        updateUserInput: UpdateUserInput,
        documentPhotoFile: String?,
        passportPhotoFile: String?
    ) {
        updateUserViewModel.updateUser(updateUserInput, documentPhotoFile, passportPhotoFile)
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner) { result ->
            if (result!=null){
                binding.progress.visibility = View.GONE
                findNavController().let { nav ->
                    nav.navigate(getFragmentId(), Bundle().apply {
                        putStringArray("FIELD_REQUIRED", fieldsToVisible)
                    })
                }
                Log.d("User Update Success", result.toString())
            }else{
                binding.progress.visibility = View.GONE
                Log.e("User Update Failure", "Unknown error")
            }
        }
    }

    private fun resetIdUpload() {
        binding.clUploadedAadhar.visibility = View.GONE
        binding.clUploadId.visibility = View.VISIBLE
        uploadedIdUri = null
        updateButtonState(uploadedIdUri != null || uploadedPhotoUri != null)
    }

    private fun resetPhotoUpload() {
        binding.clUploadedPassport.visibility = View.GONE
        binding.clUploadPhoto.visibility = View.VISIBLE
        uploadedPhotoUri = null
        updateButtonState(uploadedIdUri != null || uploadedPhotoUri != null)
    }

    private fun updateButtonState(isEnabled: Boolean) {
        binding.btnAddaddressDetails.apply {
            setBackgroundColor(
                ContextCompat.getColor(requireContext(), if (isEnabled) R.color.blue_3E3EF7 else R.color.gray_border)
            )
        }
    }

    private fun getFragmentId(): Int {
        return if (fieldsToVisible.contains("FULL_ADDRESS")) {
            sharedPreferencesManager.putString("current$courseId", "address")
            R.id.action_AdditionalDetail_to_AddressDetail
        } else {
            sharedPreferencesManager.putString("current$courseId", "")
            sharedPreferencesManager.putBoolean(courseId, true)
            R.id.courseEmptyFragment
        }
    }

    private val mediaPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri!=null) {
                if (currentFileType == "ID"){
                    uploadedIdUri = uri
                }
                if (currentFileType == "PHOTO"){
                    uploadedPhotoUri = uri
                }

                val fileSizeMB = getFileSizeMB(requireContext(), uri)
                if (fileSizeMB > MAX_FILE_SIZE_MB) {
                    Snackbar.make(binding.root, "File size must be less than 5MB", Snackbar.LENGTH_LONG).show()
                } else {
                    val fileSizeFormatted = FileUtils.getFileFromUri(requireContext(), uri)
                    displaySelectedFile(uri, fileSizeFormatted?.toString()?:"")
                }
                Log.d("PhotoPicker", "Number of items selected: ${uri}")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun displaySelectedFile(uri: Uri, fileSize: String) {
        val fileName = getFileName(requireContext(), uri)
        if (currentFileType == "ID") {
            binding.clUploadId.visibility = View.GONE
            binding.clUploadedAadhar.visibility = View.VISIBLE
            binding.fileName.text = fileName
            binding.fileSize.text = fileSize
            uploadedIdUri = uri
        } else if (currentFileType == "PHOTO") {
            binding.clUploadPhoto.visibility = View.GONE
            binding.clUploadedPassport.visibility = View.VISIBLE
            binding.fileNamePass.text = fileName
            binding.fileSizePass.text = fileSize
            uploadedPhotoUri = uri
        }
        updateButtonState(uploadedIdUri != null || uploadedPhotoUri != null)
    }

}
