package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.databinding.FragmentProfileBinding
import com.student.competishun.databinding.FragmentReferenceBinding
import com.student.competishun.utils.SharedPreferencesManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        binding.etProfileHelp.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.llMyCart.setOnClickListener {
            findNavController().navigate(R.id.myCartFragment)
        }

        binding.llLogout.setOnClickListener {
            sharedPreferencesManager.clearAccessToken()
            findNavController().navigate(R.id.loginFragment2)
        }

        binding.ProfileUserName.text = sharedPreferencesManager.name
        binding.ProfileEmail.text = sharedPreferencesManager.mobileNo
        binding.tvExamType.text = sharedPreferencesManager.preparingFor
        binding.tvYear.text= sharedPreferencesManager.targetYear.toString()


    }

}