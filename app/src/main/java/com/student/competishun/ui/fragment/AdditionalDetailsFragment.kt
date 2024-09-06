package com.student.competishun.ui.fragment

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.google.android.gms.common.util.IOUtils.copyStream
import com.google.android.material.snackbar.Snackbar
import com.student.competishun.R
import com.student.competishun.databinding.FragmentAdditionalDetailBinding
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.UpdateUserViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@AndroidEntryPoint
class AdditionalDetailsFragment : Fragment() {

    private val binding by lazy {
        FragmentAdditionalDetailBinding.inflate(layoutInflater)
    }

    private val userViewModel: UserViewModel by viewModels()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    companion object {
        private const val REQUEST_CODE_PICK_FILE = 1
        private const val MAX_FILE_SIZE_MB = 5
        private var currentFileType: String = ""
    }

    private var uploadedIdUri: Uri? = null
    private var uploadedPhotoUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                )

                val documentPhotoFile: File? = uploadedIdUri?.let { getFileFromUri(requireContext(), it) }
                val passportPhotoFile: File? = uploadedPhotoUri?.let { getFileFromUri(requireContext(), it) }
                userUpdate(updateUserInput,null,null)
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error fetching details: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.etBTHomeAddress.setOnClickListener {
            findNavController().navigate(R.id.PersonalDetailsFragment)
        }

        binding.clUploadId.setOnClickListener {
            currentFileType = "ID"
            pickFile()
        }

        binding.clUploadPhoto.setOnClickListener {
            currentFileType = "PHOTO"
            pickFile()
        }


        binding.btnAddaddressDetails.setOnClickListener {
            findNavController().navigate(R.id.action_AdditionalDetail_to_AddressDetail)
        }

        binding.closeButton.setOnClickListener {
            binding.clUploadedAadhar.visibility = View.GONE
            binding.clUploadId.visibility = View.VISIBLE
            uploadedIdUri = null
            updateButtonState()
        }

        binding.closeButtonPass.setOnClickListener {
            binding.clUploadedPassport.visibility = View.GONE
            binding.clUploadPhoto.visibility = View.VISIBLE
            uploadedPhotoUri = null
            updateButtonState()
        }

        binding.fileName.setOnClickListener {
            uploadedIdUri?.let { uri -> openFile(uri) }
        }

        binding.fileNamePass.setOnClickListener {
            uploadedPhotoUri?.let { uri -> openFile(uri) }
        }

        updateButtonState()
    }
    // Function to get a File from a Uri
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val fileName = getFileName(uri, context) ?: return null
        val file = File(context.cacheDir, fileName)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream: OutputStream = FileOutputStream(file)
            copyStream(inputStream!!, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file
    }

    // Helper function to get the file name from the Uri
    private fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                val fileSizeMB = getFileSizeMB(uri)
                if (fileSizeMB > MAX_FILE_SIZE_MB) {
                    Snackbar.make(
                        binding.root,
                        "File size must be less than 5MB",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    val fileSizeFormatted = getFileSizeFormatted(uri)
                    displaySelectedFile(uri, fileSizeFormatted)
                }
            }
        }
        (activity as? HomeActivity)?.updateUiVisibility(this)
    }

    private fun displaySelectedFile(uri: Uri, fileSize: String) {
        val fileName = getFileName(uri)
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
        updateButtonState()
    }

    private fun updateButtonState() {
        if (uploadedIdUri != null && uploadedPhotoUri != null) {
            binding.btnAddaddressDetails.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue_3E3EF7
                )
            )
            binding.btnAddaddressDetails.isEnabled = true
        } else {
            binding.btnAddaddressDetails.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_border
                )
            )
            binding.btnAddaddressDetails.isEnabled = false
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor = context?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = cut?.let { result?.substring(it + 1) }
            }
        }
        return result ?: "Unknown"
    }

    private fun getFileSizeMB(uri: Uri): Long {
        var fileSize: Long = 0
        val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    fileSize = it.getLong(sizeIndex)
                }
            }
        }
        return fileSize / (1024 * 1024)
    }

    private fun getFileSizeFormatted(uri: Uri): String {
        var fileSize: Long = 0
        val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    fileSize = it.getLong(sizeIndex)
                }
            }
        }

        return when {
            fileSize >= 1024 * 1024 -> String.format("%.2f MB", fileSize / (1024.0 * 1024.0))
            fileSize >= 1024 -> String.format("%.2f KB", fileSize / 1024.0)
            else -> String.format("%d B", fileSize)
        }
    }

    private fun openFile(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }


    fun userUpdate(updateUserInput: UpdateUserInput, documentPhotoFile: File?, passportPhotoFile: File?){
        updateUserViewModel.updateUser(updateUserInput,documentPhotoFile,passportPhotoFile)
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result?.user != null) {
                Log.e("gettingUserUpdateTarget", result.user.userInformation.targetYear.toString())
                Log.e("gettingUserUpdaterefer", result.user.userInformation.reference.toString())
                Log.e("gettingUserUpdateprep", result.user.userInformation.preparingFor.toString())
                Log.e("gettingUserUpdatecity", result.user.userInformation.address?.city.toString())

            } else {
                Log.e("gettingUserUpdatefail", result.toString())
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
