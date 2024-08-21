package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.data.model.CartItem
import com.student.competishun.databinding.FragmentMyCartBinding
import com.student.competishun.ui.adapter.MyCartAdapter
import com.student.competishun.ui.viewmodel.CreateCartViewModel
import androidx.lifecycle.Observer
import com.apollographql.apollo3.api.or
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.razorpay.Checkout
import com.student.competishun.coinkeeper.CreateOrderMutation
import com.student.competishun.coinkeeper.type.CreateOrderInput
import com.student.competishun.curator.FindAllCartItemsQuery
import com.student.competishun.ui.viewmodel.OrderViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
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
    private var originalCartItems: List<CartItem> = listOf()
    private var input: CreateOrderInput? = null
    private var paymentType: String = "full"
    var totalAmount:Int = 0
    private lateinit var helperFunctions: HelperFunctions
    var instAmountpaid = 0.0
    var fullAmount = 0.0
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

        helperFunctions = HelperFunctions()
        binding.CartTabLayout.visibility = View.GONE
        binding.rvAllCart.visibility = View.GONE
        binding.btnProceedToPay.visibility = View.GONE
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
            Log.e("cartAdaptrcartITems",cartItem.toString())
            handleItemClick(cartItem, userId)

            val amountPaid = if (paymentType == "full") {fullAmount
            } else  {
                instAmountpaid
            }
            Log.e("getamountpaid ${cartItem.price.toDouble()}",amountPaid.toString() )
            input = CreateOrderInput(
                amountPaid = amountPaid * 100,

                entityId = cartItem.entityId,
                entityType = "course",
                isPaidOnce = paymentType == "full",
                paymentMode = "online",
                paymentType = paymentType,
                totalAmount = totalAmount.toDouble() * 100,
                userId = userId
            )
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
                   // instAmountpaid = ((course.price ?: 0) + (course.with_installment_price?:0) - (course.discount?:0))* 0.6
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

                }.takeLast(1)
                cartAdapter.updateCartItems(cartItems)
                originalCartItems = cartItems
                if (cartItems.isNotEmpty()) {
                    originalCartItems = cartItems
                    cartAdapter.updateCartItems(cartItems)

                    // Show the tab layout and related views
                    binding.CartTabLayout.visibility = View.VISIBLE
                    binding.rvAllCart.visibility = View.VISIBLE
                    binding.btnProceedToPay.visibility = View.VISIBLE

                    // Show full payment data by default
                    showFullPayment()
                } else {
                    // Optionally show a message indicating no items are available
                    Toast.makeText(requireContext(), "No items available in the cart", Toast.LENGTH_SHORT).show()
                }
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
                    0 -> {paymentType = "full"
                        showFullPayment()
                    }
                    1 ->{ paymentType = "partial"
                        showPartialPayment()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun handleItemClick(cartItem: CartItem, userId: String) {
        Log.e("cartitemss",cartItem.price.toString())

    }

    private fun showFullPayment() {
        cartAdapter.updateCartItems(originalCartItems)
        val discountPrice = (originalCartItems.get(0).price.toDouble() * originalCartItems.get(0).discount.toDouble()) / 100
        binding.tvInstCoursePrice.text = "₹${originalCartItems.get(0).price}"
        binding.tvInstallmentPrice.visibility = View.GONE
        binding.tvInstallmentLabel.visibility = View.GONE
        binding.tvInstallmentPrice2.visibility = View.GONE
        binding.tvInstallmentLabel2.visibility = View.GONE
        binding. tvInstallmentChargePrice.visibility =  View.GONE
        binding. tvInstallmentCharge.visibility =  View.GONE
        totalAmount = originalCartItems.get(0).price.toInt()
        binding.tvInstDiscountLabel.text = "Discount (${helperFunctions.calculateDiscountDetails(originalCartItems.get(0).price.toDouble(),originalCartItems.get(0).discount.toDouble()).first}%)"
        binding.tvInstDiscount.text = "- ₹${originalCartItems.get(0).discount.toDouble()}"
        fullAmount = (helperFunctions.calculateDiscountDetails(originalCartItems.get(0).price.toDouble(),originalCartItems.get(0).discount.toDouble()).second)
        binding.tvPrice.text =  "₹${(helperFunctions.calculateDiscountDetails(originalCartItems.get(0).price.toDouble(),originalCartItems.get(0).discount.toDouble()).second)}"
        binding.tvInstTotalAmount.text = "₹${fullAmount}"

    }

    private fun showPartialPayment() {
        val partialPaymentItems = originalCartItems.filter { it.withInstallmentPrice > 0 }
        cartAdapter.updateCartItems(partialPaymentItems)
        val discountPrice = (originalCartItems.get(0).price.toDouble() * originalCartItems.get(0).discount.toDouble()) / 100
        var discountPriceVal = (helperFunctions.calculateDiscountDetails(originalCartItems.get(0).price.toDouble(),discountPrice).second)
        var firstInstallment = ((originalCartItems.get(0).price.toDouble() + originalCartItems.get(0).withInstallmentPrice.toDouble()) - originalCartItems.get(0).discount.toDouble())*0.6
        var secondInstallment = (originalCartItems.get(0).price.toDouble()) - firstInstallment
        totalAmount = originalCartItems.get(0).price
        binding.tvInstallmentPrice.visibility = View.VISIBLE
        binding.tvInstallmentLabel.visibility = View.VISIBLE
        binding.tvInstallmentPrice2.visibility = View.VISIBLE
        binding.tvInstallmentLabel2.visibility = View.VISIBLE
        binding. tvInstallmentCharge.visibility =  View.VISIBLE
        binding.tvInstallmentChargePrice.visibility =  View.VISIBLE
        binding.tvPrice.text =  "₹${firstInstallment}"
        instAmountpaid = firstInstallment
        binding.tvInstTotalAmount.text =  "₹${firstInstallment + secondInstallment}"
        binding.tvInstCoursePrice.text = "₹${originalCartItems.get(0).price}"
        binding.tvInstallmentPrice.text = "₹${firstInstallment}"
        binding.tvInstDiscountLabel.text = "Discount (${helperFunctions.calculateDiscountDetails(originalCartItems.get(0).price.toDouble(),originalCartItems.get(0).discount.toDouble()).first}%)"
        binding.tvInstDiscount.text = "- ₹${originalCartItems.get(0).discount.toDouble()}"
        binding.tvInstallmentPrice2.text = "₹${secondInstallment}"
        binding. tvInstallmentChargePrice.text =  "₹${originalCartItems.get(0).withInstallmentPrice}"
    }

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
        val finalPrice = discountedAmount * (discountPercentage)

        return finalPrice
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



