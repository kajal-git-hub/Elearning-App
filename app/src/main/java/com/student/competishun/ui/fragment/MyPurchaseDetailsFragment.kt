package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.data.model.CoursePaymentDetails
import com.student.competishun.databinding.FragmentMyPurchaseDetailsBinding
import com.student.competishun.ui.viewmodel.GetCourseByIDViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPurchaseDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyPurchaseDetailsBinding
    private val userViewModel: UserViewModel by viewModels()
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private val helperFunctions = HelperFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyPurchaseDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etBTPurchase.setOnClickListener {
            findNavController().navigate(R.id.MyPurchase)
        }

        observeUserDetails()
        userViewModel.fetchUserDetails()

        val courseId = arguments?.getString("PurchaseCourseId")
        val courseUserId = arguments?.getString("PurchaseUserId")

        Log.d("MyPurchaseDetailsFragment", "Course ID: $courseId")
        Log.d("MyPurchaseDetailsFragment", "Course Name: $courseUserId")

        if (courseId != null) {
            getCourseByIDViewModel.fetchCourseById(courseId)
            getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { courses ->


                if (courses?.with_installment_price == null) {
                    binding.clInstallmentCharge.visibility = View.GONE
                    binding.clFirstInstallment.visibility = View.GONE
                    binding.clSecondInstallment.visibility = View.GONE
                } else {
                    binding.clInstallmentCharge.visibility = View.VISIBLE
                    binding.etPurInstallmentCharge.text = "NA"
                    binding.clFirstInstallment.visibility = View.VISIBLE
                    binding.etPurFirstInstallment.text = "NA"
                    binding.clSecondInstallment.visibility = View.VISIBLE
                    binding.etPurSecondInstallment.text = "NA"

                }


                courses?.let {
                    binding.etPurtotalPrice.text = it.price?.toString() ?: "NA"
                    binding.etPurFinalPay.text = it.discount?.toString() ?: "NA"
                    val finalPrice = it.price?.let { price ->
                        it.discount?.let { discount -> price - discount }
                    } ?: "NA"
                    binding.etPurDiscountPay.text = "₹${finalPrice}"


                    //Discount Percentage
                    binding.etPurDiscountPer.text = "Discount (${
                        purchaseDiscountPercentage(it.price ?: 0, it.discount ?: 0).toInt()
                    }%)"
                }
            })
        }


    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->

                binding.tvUserName.text = data.getMyDetails.fullName
                binding.tvUserAddress.text = data.getMyDetails.userInformation.address?.addressLine1
                data.getMyDetails.courses.mapNotNull { course ->
                    if (course?.paymentStatus == "captured") {
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
    private fun purchaseDiscountPercentage(originalPrice: Int, discountPrice: Int): Double {
        return if (originalPrice > 0) {
            val discount = (originalPrice - discountPrice).toDouble()
            val percentage = (discount / originalPrice) * 100
            String.format("%.2f", percentage).toDouble() // formats to 2 decimal places
        } else {
            0.0
        }
    }



}