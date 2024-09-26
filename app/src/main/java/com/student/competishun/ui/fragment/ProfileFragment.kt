package com.student.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.databinding.FragmentProfileBinding
import com.student.competishun.databinding.FragmentReferenceBinding
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val userViewModel: UserViewModel by viewModels()

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


        binding.igEditProfile.setOnClickListener {
            val bottomSheetDescriptionFragment = ProfileEditFragment()
            bottomSheetDescriptionFragment.show(childFragmentManager, "BottomSheetDescriptionFragment")

        }

        observeUserDetails()
        userViewModel.fetchUserDetails()


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        binding.etBTUpload.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.llMyCart.setOnClickListener {
            findNavController().navigate(R.id.myCartFragment)
        }

        binding.llMyPurchase.setOnClickListener{
            findNavController().navigate(R.id.MyPurchase)
        }
        binding.llLogout.setOnClickListener {
            val bottomSheetDescriptionFragment = ProfileLogoutFragment()
            bottomSheetDescriptionFragment.show(childFragmentManager, "BottomSheetDescriptionFragment")

//            sharedPreferencesManager.clearUserData()
//
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            intent.putExtra("navigateToLogin", true)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            requireActivity().finish()
        }

        binding.ProfileUserName.text = sharedPreferencesManager.name
        binding.ProfileEmail.text = sharedPreferencesManager.mobileNo
        binding.tvExamType.text = sharedPreferencesManager.preparingFor
        binding.tvYear.text= "| "+sharedPreferencesManager.targetYear.toString()
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.d("userDetails",data.getMyDetails.fullName.toString())
                Log.d("userDetails",data.getMyDetails.userInformation.address?.city.toString())
                val name = data.getMyDetails.fullName
                val target = data.getMyDetails.userInformation.targetYear

                if (!name.isNullOrEmpty()) {
                    binding.ProfileUserName.setText(name)
                }
                if (target != 0) {
                    binding.tvYear.setText(" | "+target.toString())
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


}