package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.student.competishun.R
import com.student.competishun.data.model.CoursePaymentDetails
import com.student.competishun.databinding.FragmentMyPurchaseDetailsBinding
import com.student.competishun.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPurchaseDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyPurchaseDetailsBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyPurchaseDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeUserDetails()
        userViewModel.fetchUserDetails()

        val courseId = arguments?.getString("PurchaseCourseId")
        val courseName = arguments?.getString("PurchaseUserId")

        Log.d("MyPurchaseDetailsFragment", "Course ID: $courseId")
        Log.d("MyPurchaseDetailsFragment", "Course Name: $courseName")

    }
    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->

                binding.tvUserName.text = data.getMyDetails.fullName
                binding.tvUserAddress.text = data.getMyDetails.userInformation.address?.addressLine1
                data.getMyDetails.courses.mapNotNull { course ->
                    if (course?.paymentStatus=="captured"){
                        binding.clBuyCourseSection.visibility = View.GONE
                    }
                }


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