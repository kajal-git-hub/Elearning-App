package xyz.penpencil.competishun.ui.fragment

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
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentMyPurchaseDetailsBinding
import xyz.penpencil.competishun.ui.viewmodel.CoursePaymentsViewModel
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.OrdersViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel

@AndroidEntryPoint
class MyPurchaseDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyPurchaseDetailsBinding
    private val userViewModel: UserViewModel by viewModels()
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private val ordersViewModel: OrdersViewModel by viewModels()
    private var paymentType = ""
    private var rzpOrderId = ""
    private var amountPaid = ""
    private var paymentStatus = ""

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

        if (courseId != null && courseUserId != null) {
            // Fetch payment breakdown and observe results
            ordersViewModel.getPaymentBreakdown(courseId, courseUserId)
            observeCoursePayments()

            // Fetch course details after fetching payment information
            observeCourseDetails(courseId)
        }
    }

    private fun observeCoursePayments() {
        // Observe the course payments data
        ordersViewModel.paymentResult.observe(viewLifecycleOwner) { payments ->
            Log.d("payments", payments.toString())
            if (payments != null) {
                rzpOrderId = payments.firstOrNull()?.rzpOrderId ?: ""
                paymentType = payments.firstOrNull()?.paymentType ?: ""
                paymentStatus = payments.firstOrNull()?.status?:""
                amountPaid = (payments.firstOrNull()?.amount ?: "").toString()

                Log.d("PaymentType", "Payment Type: $paymentType")
                Log.d("rzpOrderId", "rzpOrderId: $rzpOrderId")

                // After payment details are available, fetch course details based on payment type
                val courseId = arguments?.getString("PurchaseCourseId")
                if (courseId != null) {
                    getCourseByIDViewModel.fetchCourseById(courseId)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error fetching payment details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeCourseDetails(courseId: String) {
        // Observe course details only after the payment data has been fetched
        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { course ->
            course?.let {
                if (paymentType == "full" && paymentStatus == "captured") {
                    // Full payment logic
                    binding.clInstallmentCharge.visibility = View.GONE
                    binding.clFirstInstallment.visibility = View.GONE
                    binding.clSecondInstallment.visibility = View.GONE

                    binding.etPurtotalPrice.text = it.price?.toString() ?: "NA"
                    binding.etPurFinalPay.text = it.discount?.toString() ?: "NA"

                    val finalPrice = it.price?.let { price ->
                        it.discount?.let { discount -> price - discount }
                    } ?: "NA"
                    binding.etPurDiscountPay.text = "₹${finalPrice}"

                    // Discount Percentage
                    binding.etPurDiscountPer.text = "Discount (${
                        purchaseDiscountPercentage(it.price ?: 0, it.discount ?: 0).toInt()
                    }%)"

                    binding.clBuyCourseSection.visibility = View.GONE
                    binding.clInstallmentDetails.visibility = View.GONE

                } else {
                    // Installment logic (if paymentType is not "full")
                    binding.clBuyCourseSection.visibility = View.VISIBLE
                    binding.clDiscount.visibility = View.GONE

                    val totalPrice = it.price ?: 0
                    binding.etPurtotalPrice.text = totalPrice.toString()
                    binding.etPurFinalPay.text = totalPrice.toString()

                    binding.clInstallmentCharge.visibility = View.GONE
                    binding.etPurInstallmentCharge.visibility = View.GONE

                    binding.clFirstInstallment.visibility = View.VISIBLE
                    binding.etPurFirstInstallment.text = amountPaid

                    binding.clSecondInstallment.visibility = View.VISIBLE
                    val paidAmount = amountPaid.toIntOrNull() ?: 0
                    val remainingAmount = totalPrice - paidAmount
                    binding.etPurSecondInstallment.text = remainingAmount.toString()
                }
            }
        })
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
            String.format("%.2f", percentage).toDouble()
        } else {
            0.0
        }
    }
}
