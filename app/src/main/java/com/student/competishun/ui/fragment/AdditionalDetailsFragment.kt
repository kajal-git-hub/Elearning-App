package com.student.competishun.ui.fragment

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.student.competishun.R
import com.student.competishun.databinding.FragmentAdditionalDetailBinding

class AdditionalDetailsFragment : Fragment() {

    private val binding by lazy {
        FragmentAdditionalDetailBinding.inflate(layoutInflater)
    }

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

        binding.clUploadId.setOnClickListener {
            currentFileType = "ID"
            pickFile()
        }

        binding.clUploadPhoto.setOnClickListener {
            currentFileType = "PHOTO"
            pickFile()
        }

        binding.btnAddDetails.setOnClickListener {
            findNavController().navigate(R.id.action_AdditionalDetail_to_AddressDetail)
        }

        binding.closeButton.setOnClickListener {
            binding.clUploadedAadhar.visibility = View.GONE
            binding.clUploadId.visibility = View.VISIBLE
        }

        binding.closeButtonPass.setOnClickListener {
            binding.clUploadedPassport.visibility = View.GONE
            binding.clUploadPhoto.visibility = View.VISIBLE
        }

        binding.fileName.setOnClickListener {
            uploadedIdUri?.let { uri -> openFile(uri) }
        }

        binding.fileNamePass.setOnClickListener {
            uploadedPhotoUri?.let { uri -> openFile(uri) }
        }
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
                    Snackbar.make(binding.root, "File size must be less than 5MB", Snackbar.LENGTH_LONG).show()
                } else {
                    val fileSizeFormatted = getFileSizeFormatted(uri)
                    displaySelectedFile(uri, fileSizeFormatted)
                }
            }
        }
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
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor = context?.contentResolver?.query(uri, null, null, null, null)
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
}
