package com.student.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.databinding.FragmentProfileLogoutBinding
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.utils.SharedPreferencesManager

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

}