package com.student.competishun.ui.fragment

import RazorpayPaymentManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private val userViewModel:UserViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val cartViewModel: CreateCartViewModel by viewModels()
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var cartAdapter: MyCartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        paymentsClient = Wallet.getPaymentsClient(
            requireActivity(),

            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // or ENVIRONMENT_PRODUCTION
                .build()
        )
//        requireActivity().onBackPressedDispatcher.addCallback(this) {
//            handleBackPressed()
//        }
    }

//    private fun handleBackPressed() {
//        findNavController().navigate(R.id.exploreFragment)
//    }

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
        var contact:String = ""
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        Log.e("haredids",sharedPreferencesManager.userId.toString())
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                val userID = data.getMyDetails.userInformation.id
                userId = userID
                Log.e("sharedids",userId.toString())
                sharedPreferencesManager.mobileNo = userDetails.mobileNumber
                contact = userDetails.mobileNumber
//                userInformationTextView.text = userDetails.userInformation.toString() // Customize as needed
            }.onFailure { exception ->
                // Handle error
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
        userViewModel.fetchUserDetails()

        binding.btnProceedToPay.setOnClickListener {
                // Sample input
                val input = CreateOrderInput(
                    amountPaid = 29999.0,
                    entityId = "250bceb2-45e4-488e-aa02-c9521555b424",
                    entityType = "COURSE",
                    isPaidOnce = true,
                    paymentMode = "online",
                    paymentType = "full",
                    totalAmount = 29999.0,
                    userId = userId

                )
            Log.e("sharedid",userId)
            orderViewModel.createOrder(input)

            // Observe the result
            orderViewModel.orderResult.observe(viewLifecycleOwner, Observer { result ->
                Log.e("orederde",result.toString())
                result.onSuccess { data ->
                    Log.d("OrderFragment", "Order created successfully: ${data.createOrder}")
                    val rzpOrderId = data.createOrder.rzpOrderId
                    var amount = data.createOrder.totalAmount
                    if (amount>0) {
                        amount = Math.round(amount * 100).toInt().toDouble()
                    }
                    val currency = "INR"
                    val checkout = Checkout()
                    checkout.setKeyID("rzp_test_3fLRNvl0KpHfwK")
                    val obj =  JSONObject()
                    try {
                        obj.put("name","Competishun")
                        obj.put("description","Test Payment")
                        obj.put("currency",currency)
                        obj.put("amount",amount)
                        obj.put("prefill.contact",contact)
                        obj.put("prefill.email", "chaitanyamunje@gmail.com")
                        Log.e("getdatarazor",obj.toString())
                        checkout.open(requireActivity(), obj)
                    }catch (e:JSONException){
                        Log.e("payment ex",e.toString())
                        e.printStackTrace()
                    }

                    Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_SHORT).show()
                }.onFailure { exception ->
                    // Handle the error
                    navigatePaymentFail()
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                }
            })

        }
        // Observe the result of findAllCartItems
        cartViewModel.findAllCartItemsResult.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { data ->
                Toast.makeText(requireContext(), result.isSuccess.toString(), Toast.LENGTH_SHORT)
                    .show()
                Log.e("gerafad",data.findAllCartItems.toString())
                val cartItems = data.findAllCartItems.mapNotNull  { cartItemData ->
                    val course = cartItemData.course
                    if (course != null && cartItemData.cartItem != null) {
                        CartItem(
                            profileImageResId = R.drawable.frame_1707480855, // Replace with actual logic for image
                            name = course.name,
                            viewDetails = "View Details",
                            forwardDetails = R.drawable.cart_arrow_right,
                            discount = course.discount?:0,
                            price = course.price?:0,
                            entityId = cartItemData.cartItem.entity_id,
                            cartId = cartItemData.cartItem.cart_id,
                            courseId = course.id,
                            withInstallmentPrice = course.with_installment_price?:0,
                            categoryId = course.category_id.toString()
                        )

                        } else
                    {
                        null
                    }
                }

                // Update the adapter with the new data
                cartAdapter.updateCartItems(cartItems)
//
            }.onFailure { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
        })


        // Call the findAllCartItems function
        cartViewModel.findAllCartItems(userId)





        binding.CartTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.clPriceDetails.visibility = View.VISIBLE
                        binding.clPriceInstallmentDetails.visibility = View.GONE
                    }
                    1 -> {
                        binding.clPriceDetails.visibility = View.GONE
                        binding.clPriceInstallmentDetails.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselected
            }
        })

        binding.clPriceDetails.visibility = View.VISIBLE
        binding.clPriceInstallmentDetails.visibility = View.GONE


        binding.igToolbarBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

//        val cartItemsview = listOf(
//            CartItem(R.drawable.frame_1707480855, "Prakhar Integrated (Fast Lane-2) 2024-25", R.drawable.frame_1707480886, "View Details", R.drawable.cart_arrow_right),
//            CartItem(R.drawable.frame_1707480855, "Prakhar Integrated (Fast Lane-2) 2024-25", R.drawable.frame_1707480886, "View Details", R.drawable.cart_arrow_right)
//        )

    }


    private fun navigateToLoaderScreen() {
        findNavController().navigate(R.id.action_mycartFragment_to_paymentLoaderFragment)

        Handler(Looper.getMainLooper()).postDelayed({
            if (findNavController().currentDestination?.id == R.id.paymentLoaderFragment) {
                findNavController().navigate(R.id.action_paymentLoaderFragment_to_paymentFragment)
            }
        }, 2000)
    }

    private fun navigatePaymentFail(){
        findNavController().navigate(R.id.action_mycartFragment_to_paymentFailedFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


