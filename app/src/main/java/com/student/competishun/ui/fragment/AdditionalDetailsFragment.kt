package com.student.competishun.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentAdditionalDetailBinding

class AdditionalDetailsFragment : Fragment() {

    private val binding by lazy {
        FragmentAdditionalDetailBinding.inflate(layoutInflater)
    }

    companion object {
        private const val REQUEST_CODE_PICK_FILE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clUploadId.setOnClickListener {
            pickFile()
        }

        binding.clUploadPhoto.setOnClickListener {
            pickFile()
        }

        binding.btnAddDetails.setOnClickListener {
            findNavController().navigate(R.id.action_AdditionalDetail_to_AddressDetail)
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
                displaySelectedFile(uri)
            }
        }
    }

    private fun displaySelectedFile(uri: Uri) {
        binding.imageIv.setImageURI(uri)
    }
}
