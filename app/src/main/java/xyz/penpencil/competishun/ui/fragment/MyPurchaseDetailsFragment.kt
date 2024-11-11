package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.razorpay.Checkout
import com.student.competishun.coinkeeper.CreateOrderMutation
import com.student.competishun.coinkeeper.type.CreateOrderInput
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentMyPurchaseDetailsBinding
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.OrderViewModel
import xyz.penpencil.competishun.ui.viewmodel.OrdersViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.Constants
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.SharedPreferencesManager

@AndroidEntryPoint
class MyPurchaseDetailsFragment : DrawerVisibility() {
    private lateinit var binding: FragmentMyPurchaseDetailsBinding
    private val userViewModel: UserViewModel by viewModels()
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private val ordersViewModel: OrdersViewModel by viewModels()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var helperFunctions = HelperFunctions()
    private var paymentType = ""
    private var input: CreateOrderInput? = null
    private val orderViewModel: OrderViewModel by viewModels()
    var courseId = ""
    var courseUserId = ""
    var userName = ""
    var courseName = ""
    private var rzpOrderId = ""
    private var amountPaid = 0.0
    private var paymentStatus = ""
    private var firstPurchase = ""
    private var transactionId = ""
    private var fullTransactionId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyPurchaseDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setFocusableInTouchMode(true)
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    v?.findNavController()?.popBackStack()
                    return true
                }
                return false
            }

        })

        binding.etBTPurchase.setOnClickListener {
            findNavController().popBackStack()
        }
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        observeUserDetails()
        userViewModel.fetchUserDetails()

        courseId = arguments?.getString("PurchaseCourseId")?: ""
        courseUserId = arguments?.getString("PurchaseUserId")?: ""

        firstPurchase = arguments?.getString("FirstPurchase").toString()

        Log.d("MyPurchaseDetailsFragment", "Course ID: $courseId")
        Log.d("MyPurchaseDetailsFragment", "Course Name: $courseUserId")

        if (courseId != null && courseUserId != null) {
            // Fetch payment breakdown and observe results
            ordersViewModel.getPaymentBreakdown(courseId, courseUserId)
            observeCoursePayments()

            // Fetch course details after fetching payment information
        }

        binding.btGenerateReceipt.setOnClickListener {
            if(fullTransactionId.isNotEmpty()){
                downloadReceipt(fullTransactionId)
            }else{
                downloadReceipt(transactionId)
            }
        }
    }

    private fun observeCoursePayments() {
        binding.clBuyCourseSection.visibility = View.GONE
        binding.clInstallmentDetails.visibility = View.GONE
        binding.clOrderReceipt.visibility = View.GONE
        ordersViewModel.paymentResult.observe(viewLifecycleOwner) { payments ->
            if (payments != null) {
                Log.d("PaymentTypen", "Payment Type: ${payments}")
                val firstPayment = payments.firstOrNull()
                val secondPayment = if (payments.isNotEmpty()) payments else null
                if((secondPayment?.size ?: 0) > 1){
                    fullTransactionId = secondPayment?.get(1)?.rzpOrderId.toString()
                }
                binding.clOrderReceipt.visibility = View.VISIBLE
                rzpOrderId = firstPayment?.rzpOrderId ?: ""
                paymentType = firstPayment?.paymentType ?: ""
                transactionId = firstPayment?.rzpOrderId?:""
                paymentStatus = firstPayment?.status ?: ""
                amountPaid = ((firstPayment?.amount ?: "") as Double)


                Log.d("rzpOrderId", "rzpOrderId: $rzpOrderId")

                // After payment details are available, fetch course details based on payment type
                val courseId = arguments?.getString("PurchaseCourseId")
                if (courseId != null) {
                    getCourseByIDViewModel.fetchCourseById(courseId)
                }

                // Handling "full" payment
                if (paymentType == "full" && paymentStatus == "captured") {
                    // Full payment logic
                    binding.clInstallmentCharge.visibility = View.GONE
                    binding.clFirstInstallment.visibility = View.GONE
                    binding.clSecondInstallment.visibility = View.GONE
                    getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner) { course ->
                        course?.let {
                            binding.etPurtotalPrice.text = "₹ ${it.price?.toString() ?: "0"}"
                            binding.etPurFinalPay.text = "₹ ${it.discount?.toString() ?: "0"}"

                            val finalPrice = it.price?.let { price ->
                                it.discount?.let { discount -> price - discount }
                            } ?: "NA"
                            binding.etPurDiscountPay.text = "- ₹ ${finalPrice}"

                            // Discount Percentage
                            binding.etPurDiscountPer.text = "Discount (${
                                purchaseDiscountPercentage(it.price ?: 0, it.discount ?: 0).toInt()
                            }%)"

                            binding.clBuyCourseSection.visibility = View.GONE
                            binding.clInstallmentDetails.visibility = View.GONE
                        }
                    }
                } else {
                    // Installment logic (if paymentType is not "full")
                    binding.clBuyCourseSection.visibility = View.VISIBLE
                    binding.clInstallmentDetails.visibility = View.VISIBLE
                    binding.clDiscount.visibility = View.GONE

                    getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner) { course ->
                        course?.let {
                            val totalPrice = it.with_installment_price ?: 0
                            Log.d("totalPrice",totalPrice.toString())
                            courseName = it.name ?: ""
                            binding.etPurtotalPrice.text = "₹ ${totalPrice}"
                            binding.etPurFinalPay.text = "₹ ${totalPrice}"
                            binding.clInstallmentCharge.visibility = View.GONE
                            binding.etPurInstallmentCharge.visibility = View.GONE

                            binding.clFirstInstallment.visibility = View.VISIBLE
                            binding.etPurFirstInstallment.text = "₹ ${amountPaid}"

                            // If there's a second payment (installment)
                            if (secondPayment != null ) {
                                val secondPaymentAmount = secondPayment[0].amount ?: 0
                                binding.clSecondInstallment.visibility = View.VISIBLE
//                                binding.btGenerateReceipt.setOnClickListener {
//                                    downloadReceipt(secondPayment[0].rzpOrderId.toString())
//                                }
                                binding.tvInstallmentAmount.text ="₹ ${secondPaymentAmount.toInt()}"
                                Log.e("courseIddn", courseUserId)
                                binding.etPurFirstInstallment.text = "₹ ${secondPaymentAmount.toInt()}"
                                binding.tvInstallmentDate.text = firstPurchase
                                val paidAmount = secondPayment[0].amount.toInt()
                                val remainingAmount = totalPrice - paidAmount
                                binding.etPurSecondInstallment.text = "₹ ${remainingAmount}"
                                binding.tvPriceRemaining.text = "₹ ${remainingAmount}"
                                binding.clBuynow.setOnClickListener {
                                    createOrder(totalPrice,remainingAmount,"installment",rzpOrderId,)
                                }
                                binding.tv2ndInstallmentAmount.text = "₹ ${remainingAmount}"
                               val secondInstallment =   secondPayment.forEach { payment ->
                                   Log.e("secondInstallments",payment.paymentType.toString())
                                   if ( payment.paymentType == "partial") {
                                       binding.clBuyCourseSection.visibility = View.VISIBLE
                                       binding.tv2ndInstallmentStatus.text = "Pending"
                                       binding.tv2ndInstallmentStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color._B3261E))

                                   }else
                                       if ( payment.paymentType == "installment"){
                                           binding.clBuyCourseSection.visibility = View.GONE
                                           Log.e("secondinstal",secondPayment[1].toString())
                                           binding.tv2ndInstallmentStatus.text = "Successful"
                                           binding.tv2ndInstallmentStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color._4BB543))
                                           binding.secondInstSlip.text = "2nd Installment (Paid)"
                                           binding.tv2ndInstallmentDateTxt.text = "Paid on:"
                                           binding.tv2ndInstallmentDate.text =  helperFunctions.formatCourseDate(secondPayment[1].createdAt.toString())
                                           binding.ivGreyTick.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.green_tick_installment))
                                           binding.ivLine.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.vector_verticle_line_green))
                                           observeCourseDetails(course.id)
                                       } }
                                Log.e("secondInstallmentss",secondInstallment.toString())
                            } else {
                                //

                            }

                        }
                    }
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

    private fun createOrder(totalAmount:Int,amountPaid: Int,paymentType :String,rzpOrderId :String){
        input = CreateOrderInput(
            amountPaid = amountPaid.toDouble(),
            entityId = courseId,
            entityType = "course",
            isPaidOnce = paymentType == "full",
            paymentMode = "online",
            paymentType = paymentType,
            totalAmount = totalAmount.toDouble(),
            userId = courseUserId,
            userName = userName,
            rzpOrderId = Optional.present(rzpOrderId),
            courseName = courseName
        )
        input?.let { orderInput ->
            orderViewModel.createOrder(orderInput)
            Log.e("orderAmountss",orderInput.toString())
            orderViewModel.orderResult.observe(viewLifecycleOwner, Observer { result ->
                result.onSuccess { data ->
                    processPayment(data.createOrder)
                }.onFailure { exception ->
                    Log.e("payemen",exception.message.toString(),exception.cause)
                    navigatePaymentFail()
                    // Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                }
            })
        }?: run {
            Log.e("clicked enable","procedd")
            // Re-enable the button if input is null (edge case)
            binding.clBuynow.isEnabled = true
        }
    }

    private fun navigatePaymentFail() {
        findNavController().navigate(R.id.PaymentFailedFragment)
    }

    private fun processPayment(order: CreateOrderMutation.CreateOrder) {
        val rzpOrderId = order.rzpOrderId
        sharedPreferencesManager.rzpOrderId = rzpOrderId
        Log.e("razorpaydi",rzpOrderId.toString())
        var amount = order.amountPaid
        Log.e("chcekcnou",amount.toString())
        val currency = "INR"
        val checkout = Checkout()
        checkout.setKeyID(Constants.RazorpayKeyId_Prod)
        Log.e("user/id=",courseUserId)
        Log.e("user/tokem=",sharedPreferencesManager.accessToken.toString())
        val obj = JSONObject()
        try {
            obj.put("name", "Competishun")
            obj.put("currency", currency)
            obj.put("amount", amount)
            obj.put("order_id", rzpOrderId)
            val prefill = JSONObject()
            prefill.put("userId", courseUserId)
            prefill.put("contact", sharedPreferencesManager.mobileNo)
            obj.put("prefill", prefill)
            Log.e("orderData",obj.toString())
            checkout.open(requireActivity(), obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun observeCourseDetails(courseId: String) {
        // Observe course details only after the payment data has been fetched
        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { course ->
            Log.e("paymentTypeCour",paymentType)
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

                } else if (paymentType == "installment" && paymentStatus == "captured"){
                    binding.clBuyCourseSection.visibility = View.GONE
                    binding.tv2ndInstallmentStatus.text = "Successful"
                } else {
                    // Installment logic (if paymentType is not "full")
                   // binding.clBuyCourseSection.visibility = View.VISIBLE
                    binding.clDiscount.visibility = View.GONE

                    val totalPrice = it.with_installment_price ?: 0
                    binding.etPurtotalPrice.text = totalPrice.toString()
                    binding.etPurFinalPay.text = totalPrice.toString()

                    binding.clInstallmentCharge.visibility = View.GONE
                    binding.etPurInstallmentCharge.visibility = View.GONE

                    binding.clFirstInstallment.visibility = View.VISIBLE
                    binding.etPurFirstInstallment.text = amountPaid.toString()

                    binding.clSecondInstallment.visibility = View.VISIBLE
                    val paidAmount = amountPaid ?: 0.0
                    val remainingAmount = totalPrice - paidAmount
                    binding.etPurSecondInstallment.text = remainingAmount.toString()
                }
            }
        })
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                userName = data.getMyDetails.fullName?:""
                Log.e("courseIddu ${data.getMyDetails.id}",sharedPreferencesManager.accessToken.toString() )
                binding.tvUserName.text = data.getMyDetails.fullName
                val addressLine = data.getMyDetails.userInformation.address?.addressLine1 ?: "No Address Available"
                binding.tvUserAddress.text = addressLine

                var isExpanded = false
                binding.tvReadMore.setOnClickListener {
                    if (isExpanded) {
                        binding.tvUserAddress.maxLines = 1
                        binding.tvReadMore.text = "Read More"
                        binding.tvReadMore.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0)
                    } else {
                        binding.tvUserAddress.maxLines = Int.MAX_VALUE
                        binding.tvReadMore.text = "Read Less"
                        binding.tvReadMore.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0)
                    }
                    isExpanded = !isExpanded
                }


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
    private fun downloadReceipt( transactionId:String){
        ordersViewModel.generateReceipt(transactionId)
        ordersViewModel.receiptResult.observe(viewLifecycleOwner)
        { result ->

            result.onSuccess {
                var receiptLink = it.generateReceipt
                Log.e("ReceiptLink",receiptLink)
                Toast.makeText(requireContext(), "Download started", Toast.LENGTH_SHORT).show()

                helperFunctions.downloadPdfOld(requireContext(),receiptLink,"Payment Invoice")
                Toast.makeText(requireContext(), "Download completed successfully", Toast.LENGTH_SHORT).show()
            }.onFailure {
                // Handle failure, e.g., show an error message
                Log.e("Failed to download ReceiptLink: ",it.message.toString())
            }
        }
    }
}
