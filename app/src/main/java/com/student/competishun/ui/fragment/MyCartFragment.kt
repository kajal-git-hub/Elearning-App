package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.data.model.CartItem
import com.student.competishun.databinding.FragmentMyCartBinding
import com.student.competishun.ui.adapter.MyCartAdapter
import com.student.competishun.ui.viewmodel.CreateCartViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.student.competishun.coinkeeper.CreateOrderMutation
import com.student.competishun.coinkeeper.type.CreateOrderInput
import com.student.competishun.curator.type.CreateCartItemDto
import com.student.competishun.curator.type.EntityType
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.OrderViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class MyCartFragment : Fragment() {
    private var _binding: FragmentMyCartBinding? = null
    private val orderViewModel: OrderViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val cartViewModel: CreateCartViewModel by viewModels()
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var cartAdapter: MyCartAdapter

    private var input: CreateOrderInput? = null
    private var paymentType: String = "full"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentsClient = Wallet.getPaymentsClient(
            requireActivity(),
            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // or ENVIRONMENT_PRODUCTION
                .build()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userId: String = ""
        var instAmountpaid = 0.0
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                userId = userDetails.userInformation.id
                sharedPreferencesManager.mobileNo = userDetails.mobileNumber
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
        userViewModel.fetchUserDetails()
        Log.e("cartAdaptercartITems","cartItem.toString()")

        cartAdapter = MyCartAdapter(mutableListOf()) { cartItem ->
            Log.e("cartAdaptercartITems",cartItem.toString())
            handleItemClick(cartItem, userId)
        }
        binding.rvAllCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
        }

        cartViewModel.findAllCartItemsResult.observe(viewLifecycleOwner, Observer { result ->

            result.onSuccess { data ->
                Log.e("CartItems", data.findAllCartItems.toString() )
                val cartItems = data.findAllCartItems.mapNotNull { cartItemData ->
                    val course = cartItemData.course
                    Log.e("coursevalue",course.toString())
                    instAmountpaid = ((course.price ?: 0) + (course.with_installment_price?:0) - (course.discount?:0))* 0.6
                    CartItem(
                        profileImageResId = R.drawable.frame_1707480855, // Replace with actual logic for image
                        name = course.name,
                        viewDetails = "View Details",
                        forwardDetails = R.drawable.cart_arrow_right,
                        discount = course.discount ?: 0,
                        price = course.price ?: 0,
                        entityId = cartItemData.cartItem.entity_id,
                        cartId = cartItemData.cartItem.cart_id,
                        courseId = course.id,
                        withInstallmentPrice = course.with_installment_price ?: 0,
                        categoryId = course.category_id.toString()
                    )

                }
                cartAdapter.updateCartItems(cartItems)
            }.onFailure { exception ->
                Log.e("exception in cart",exception.toString())
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
        })

        cartViewModel.findAllCartItems(userId)

        binding.btnProceedToPay.setOnClickListener {

            input?.let { orderInput ->
                orderViewModel.createOrder(orderInput)

                orderViewModel.orderResult.observe(viewLifecycleOwner, Observer { result ->
                    result.onSuccess { data ->
                        processPayment(data.createOrder)
                    }.onFailure { exception ->
                        navigatePaymentFail()
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                    }
                })
            } ?: run {
                Toast.makeText(requireContext(), "Please select a cart item before proceeding.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.CartTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> paymentType = "full"
                    1 -> paymentType = "partial"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun handleItemClick(cartItem: CartItem, userId: String) {
        Log.e("cartitem",cartItem.price.toString())
        binding.tvPrice.text = "₹${cartItem.price}"
        binding.tvCoursePrice.text = "₹${cartItem.price}"
        binding.tvInstDiscountLabel.text = cartItem.discount.toString()
        binding.tvDiscount.text = "- ₹${cartItem.discount}"
        binding.tvTotalAmount.text = "₹${cartItem.price}"

        val amountPaid = if (paymentType == "full") {
            cartItem.price.toDouble()
        } else  {
            calculateDiscountedPrice(cartItem.price.toDouble(), cartItem.withInstallmentPrice.toDouble(), cartItem.discount.toDouble())
        }
       Log.e("getamountpaid",amountPaid.toString())
        input = CreateOrderInput(
            amountPaid = amountPaid,
            entityId = cartItem.entityId,
            entityType = "course",
            isPaidOnce = paymentType == "full",
            paymentMode = "online",
            paymentType = paymentType,
            totalAmount = cartItem.price.toDouble(),
            userId = userId
        )
    }
                // Sample input
                val input = CreateOrderInput(
                    amountPaid = 29999.0,
                    entityId = "250bceb2-45e4-488e-aa02-c9521555b424",
                    entityType = "course",
                    isPaidOnce = true,
                    paymentMode = "online",
                    paymentType = "full",
                    totalAmount = 29999.0,
                    userId = userId

                )
            Log.e("sharedid",userId)
            orderViewModel.createOrder(input)

    private fun processPayment(order: CreateOrderMutation.CreateOrder) {
        val rzpOrderId = order.rzpOrderId
        var amount = order.amountPaid
        if (amount > 0) {
            amount = Math.round(amount * 100).toInt().toDouble()
        }
        val currency = "INR"
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_DcVrk6NysFj71r")
        val obj = JSONObject()
        try {
            obj.put("name", "Competishun")
            obj.put("currency", currency)
            obj.put("amount", amount)
            obj.put("order_id", rzpOrderId)
            val prefill = JSONObject()
            prefill.put("email", "gaurav.kumar@example.com")
            prefill.put("contact", "8888888888")
            obj.put("prefill", prefill)
            checkout.open(requireActivity(), obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun navigatePaymentFail() {
        findNavController().navigate(R.id.action_mycartFragment_to_paymentFailedFragment)
    }
    fun calculateDiscountedPrice(price: Double, withInstallmentPrice: Double, discountPrice: Double): Double {
        val totalPrice = price + withInstallmentPrice

        val discountedAmount = totalPrice - discountPrice

        val discountPercentage = 0.60
        val finalPrice = discountedAmount * (1 - discountPercentage)

        return finalPrice
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



