package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentProfileLogoutBinding
import xyz.penpencil.competishun.ui.main.MainActivity
import xyz.penpencil.competishun.utils.SharedPreferencesManager

class ProfileLogoutFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentProfileLogoutBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

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
        binding = FragmentProfileLogoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        binding.mbLogoutButton.setOnClickListener {

            sharedPreferencesManager.clearUserData()

            deleteLocalFiles()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("navigateToLogin", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        binding.mbCancel.setOnClickListener {
            findNavController().navigate(R.id.ProfileFragment)
        }
    }
    private fun deleteLocalFiles() {
        val filesDir = requireActivity().filesDir

        val files = filesDir.listFiles()

        files?.forEach { file ->
            if (file.name.endsWith(".mp4", ignoreCase = true) || file.name.endsWith("PDF",ignoreCase = true)) {
                Log.d("LogoutDelete", "Deleting file: ${file.name}")
                file.delete()
            }
        }
    }

}