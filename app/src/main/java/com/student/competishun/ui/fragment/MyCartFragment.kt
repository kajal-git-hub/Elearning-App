package com.student.competishun.ui.fragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.data.model.CartItem
import com.student.competishun.databinding.FragmentMyCartBinding
import com.student.competishun.ui.adapter.MyCartAdapter
import com.student.competishun.ui.viewmodel.CreateCartViewModel
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.razorpay.Checkout
import com.student.competishun.coinkeeper.CreateOrderMutation
import com.student.competishun.coinkeeper.type.CreateOrderInput
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.GetCourseByIDViewModel
import com.student.competishun.ui.viewmodel.OrderViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.OnCartItemRemovedListener
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MyCartFragment : Fragment(), OnCartItemRemovedListener,MyCartAdapter.OnCartItemClickListener {
    private lateinit var binding :  FragmentMyCartBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val cartViewModel: CreateCartViewModel by viewModels()
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var cartAdapter: MyCartAdapter
    private var originalCartItems: List<CartItem> = listOf()
    private var input: CreateOrderInput? = null
    private var paymentType: String = "full"
    var totalAmount:Int = 0
    private lateinit var cartItems: List<CartItem>
    private lateinit var helperFunctions: HelperFunctions
    var instAmountpaid = 0.0
    var fullAmount = 0.0
    var userId: String = ""
    var userName:String = ""
    var courseName:String = ""
   private lateinit var tabLayout : TabLayout
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
        binding = FragmentMyCartBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }
        return binding.root
    }

    private fun handleBackPressed() {
        findNavController().navigate(R.id.ProfileFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)


        tabLayout = view.findViewById(R.id.CartTabLayout)




        binding.igToolbarBackButton.setOnClickListener {
            findNavController().navigate(R.id.ProfileFragment)
        }
        helperFunctions = HelperFunctions()
        binding.parentData.visibility = View.GONE




      //  binding.clrvContainer.visibility = View.GONE
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                userId = userDetails.id
                userName = userDetails.fullName?:""
                sharedPreferencesManager.mobileNo = userDetails.mobileNumber
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
        userViewModel.fetchUserDetails()
        Log.e("cartAdaptercartITems","cartItem.toString()")
        myAllCart()

        cartAdapter = MyCartAdapter(mutableListOf(),cartViewModel,viewLifecycleOwner,userId,this,this) { selectedItem ->
            Log.e("cartAdaptrcartITems", selectedItem.toString())
            handleItemClick(selectedItem, userId)
           // val selectedItem = cartAdapter.getSelectedItem()
            if (selectedItem != null) {



//                if(selectedItem.withInstallmentPrice==0 &&selectedItem.withInstallmentPrice==null ){
//                    Log.e("selectedItemm",selectedItem.withInstallmentPrice.toString())
//                    binding.clSecondbottomInstallement.visibility = View.GONE
//                    tabLayout.removeTabAt(1)
//                }else{
//                    binding.clSecondbottomInstallement.visibility = View.GONE
//                    binding.clNotApplicable.visibility = View.GONE
//                }

                if (selectedItem.withInstallmentPrice==0 || selectedItem.withInstallmentPrice==null){
                    binding.tabLayoutContainer.visibility = View.GONE
                }else {
                    binding.tabLayoutContainer.visibility = View.VISIBLE
                }
                val amountPaid = if (paymentType == "full") {
                    showFullPayment(selectedItem)
                    Log.e("selectedITeprce ${selectedItem.discount}",selectedItem.name)
                    Log.e("selectedI ",fullAmount.toString())
                    fullAmount } else {
                    showPartialPayment(selectedItem)
                    Log.e("selectedIelse ${selectedItem.price}",selectedItem.name)
                    Log.e("selectedInst $paymentType ",instAmountpaid.toString())
                    instAmountpaid
                }
                Log.e("getamountpaid ${selectedItem.price.toDouble()}", amountPaid.toString())
                input = CreateOrderInput(
                    amountPaid = amountPaid,
                    entityId = selectedItem.entityId,
                    entityType = "course",
                    isPaidOnce = paymentType == "full",
                    paymentMode = "online",
                    paymentType = paymentType,
                    totalAmount = totalAmount.toDouble(),
                    userId = userId,
                    userName = userName,
                    courseName = courseName
                )
            }
        }

        binding.rvAllCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
        }

        binding.btnProceedToPay.setOnClickListener {
            if (input == null && cartAdapter.itemCount > 0) {
            // Automatically select the first item in the cart and set the input if input is null
            val selectedCartItem = if (cartAdapter.selectedItemPosition == RecyclerView.NO_POSITION) {
                // Automatically select the first item if none is selected
               // cartAdapter.setSelectedItemPosition(0)
                cartAdapter.getItemAt(0) // Get the first item in the cart
            } else {
                // Use the already selected item
                cartAdapter.getItemAt(cartAdapter.selectedItemPosition)
            }

            selectedCartItem?.let { cartItem ->
                val amountPaid = if (paymentType == "full") {
                    fullAmount
                } else {
                    instAmountpaid
                }

//                val firstCartItem = cartAdapter.getItemAt(0)
//                firstCartItem?.let { cartItem ->
//                    val amountPaid = if (paymentType == "full") { fullAmount } else { instAmountpaid }Log.e("fullpricec $amountPaid",totalAmount.toString())
                  Log.e("exceptionscc $totalAmount",amountPaid.toString())
                    input = CreateOrderInput(
                        amountPaid = amountPaid,
                        entityId = cartItem.entityId,
                        entityType = "course",
                        isPaidOnce = paymentType == "full",
                        paymentMode = "online",
                        paymentType = paymentType,
                        totalAmount = totalAmount.toDouble(),
                        userId = userId,
                        userName = userName,
                        courseName = courseName
                    )
                }
            }

            // Proceed to payment if input is not null
            Log.e("orderAinput",input.toString())
            input?.let { orderInput ->
                orderViewModel.createOrder(orderInput)
                Log.e("orderAmountss",orderInput.toString())
                orderViewModel.orderResult.observe(viewLifecycleOwner, Observer { result ->
                    result.onSuccess { data ->
                        processPayment(data.createOrder)
                    }.onFailure { exception ->
                        Log.e("payemen",exception.message.toString(),exception.cause)
                        navigatePaymentFail()
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }



        binding.CartTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {paymentType = "full"
                        if (cartItems.isNotEmpty()) {
                            showFullPayment(cartItems[0])
                            binding.clSecondbottomInstallement.visibility = View.GONE
                        }

                    }
                    1 ->{ paymentType = "partial"
                        Log.e("getselected",paymentType.toString())
                        Toast.makeText(requireContext(),"Select Course Again", Toast.LENGTH_SHORT).show()
                        val selectedItem = cartAdapter.getSelectedItem()
                        if (cartAdapter.selectedItemPosition != RecyclerView.NO_POSITION) {
                           // val selectedItem = cartItems[cartAdapter.selectedItemPosition]
                            Log.e("getselectedITem",selectedItem.toString())
                            if (selectedItem != null) {
                                showPartialPayment(selectedItem)
                            }
                        }

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

    private fun showFullPayment(selectedCartItem: CartItem) {
        paymentType = "full"
        binding.clSecondbottomInstallement.visibility = View.GONE
        binding.tvOneTimePayment.text = "One-Time Payment"
        cartAdapter.updateCartItems(originalCartItems)
        Log.e("getpaymentd",selectedCartItem.toString())
        val discountPrice = (selectedCartItem.price.toDouble() * selectedCartItem.discount.toDouble()) / 100
        binding.tvInstCoursePrice.text = "₹${selectedCartItem.price}"
        binding.tvInstallmentPrice.visibility = View.GONE
        binding.tvInstallmentLabel.visibility = View.GONE
        binding.tvInstallmentPrice2.visibility = View.GONE
        binding.tvInstallmentLabel2.visibility = View.GONE
        binding. tvInstallmentChargePrice.visibility =  View.GONE
        binding. tvInstallmentCharge.visibility =  View.GONE
        binding.tvInstDiscountLabel.visibility = View.VISIBLE
        binding.tvInstDiscount.visibility = View.VISIBLE
        totalAmount = selectedCartItem.price.toInt()
        binding.tvInstDiscountLabel.text = "Discount (${helperFunctions.calculateDiscountPercentage(selectedCartItem.price, selectedCartItem.discount).toInt()}%)"
        binding.tvInstDiscount.text = "-₹${(helperFunctions.calculateDiscountDetails(selectedCartItem.price.toDouble(),selectedCartItem.discount.toDouble()).second)}"
         //   "- ₹${(originalCartItems.get(0).price.toDouble()).minus(originalCartItems.get(0).discount.toDouble())}"
       // fullAmount = (helperFunctions.calculateDiscountDetails(originalCartItems.get(0).price.toDouble(),originalCartItems.get(0).discount.toDouble()).second.toDouble())
        binding.tvPrice.text = "₹${selectedCartItem.discount}"
        fullAmount = selectedCartItem.discount.toDouble()
        binding.tvInstTotalAmount.text = "₹${selectedCartItem.discount}"


    }

    fun secondInstallment():String{
        val calendar = Calendar.getInstance() // Get current date
        calendar.add(Calendar.DAY_OF_YEAR, 45) // Add 45 days

        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val newDate = dateFormat.format(calendar.time)
         return newDate
    }

    private fun showPartialPayment(selectedCartItem: CartItem) {
        paymentType = "partial"
        binding.tvOneTimePayment.text = "1st Installment"
        binding.clSecondbottomInstallement.visibility = View.VISIBLE
        binding.etInstallmentbelowDetails.text =  "2nd Installment On: "
        binding.secondText.text = secondInstallment()

        val partialPaymentItems = originalCartItems.filter { it.withInstallmentPrice > 0 }
        if (partialPaymentItems.isNotEmpty()) {
            cartAdapter.updateCartItems(partialPaymentItems)
            val discountPrice =
                (selectedCartItem.price.toDouble() * selectedCartItem.discount.toDouble()) / 100
            var discountPriceVal = (helperFunctions.calculateDiscountDetails(
                selectedCartItem.price.toDouble(),
                discountPrice
            ).second)
            var firstInstallment =
                ((selectedCartItem.withInstallmentPrice.toDouble())) * 0.6
            instAmountpaid = firstInstallment
            var secondInstallment = (selectedCartItem.withInstallmentPrice.toDouble()) - firstInstallment


            totalAmount = selectedCartItem.price
//        var  firstInstallment = originalCartItems.get(0).withInstallmentPrice
//        var secondInstallment = originalCartItems.get(0).price.minus(originalCartItems.get(0).withInstallmentPrice)
            binding.tvInstallmentPrice.visibility = View.VISIBLE
            binding.tvInstallmentLabel.visibility = View.VISIBLE
            binding.tvInstallmentPrice2.visibility = View.VISIBLE
            binding.tvInstallmentLabel2.visibility = View.VISIBLE
            binding.tvInstallmentCharge.visibility = View.VISIBLE
            binding.tvInstallmentChargePrice.visibility = View.VISIBLE
            binding.tvInstDiscountLabel.visibility = View.GONE
            binding.tvInstDiscount.visibility = View.GONE
            binding.tvPrice.text = "₹${firstInstallment}"
            var installmentChart =
                selectedCartItem.withInstallmentPrice.minus(selectedCartItem.price)
            binding.tvInstTotalAmount.text = "₹${selectedCartItem.withInstallmentPrice}"
            binding.tvInstCoursePrice.text = "₹${selectedCartItem.price}"
            binding.tvInstallmentPrice.text = "₹${firstInstallment}"
            binding.tvInstallmentPrice2.text = "₹${secondInstallment}"
            binding.tvInstallmentChargePrice.text =
                "₹${selectedCartItem.withInstallmentPrice}"
        }
    }

    private fun processPayment(order: CreateOrderMutation.CreateOrder) {
        val rzpOrderId = order.rzpOrderId
        Log.e("razorpaydi",rzpOrderId.toString())
        var amount = order.amountPaid
        Log.e("chcekcnou",amount.toString())
        val currency = "INR"
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_DcVrk6NysFj71r")
        Log.e("user/id=",userId.toString())
        Log.e("user/tokem=",sharedPreferencesManager.accessToken.toString())
        val obj = JSONObject()
        try {
            obj.put("name", "Competishun")
            obj.put("currency", currency)
            obj.put("amount", amount)
            obj.put("order_id", rzpOrderId)
            val prefill = JSONObject()
            prefill.put("userId", userId)
            prefill.put("contact", sharedPreferencesManager.mobileNo)
            obj.put("prefill", prefill)
            Log.e("orderData",obj.toString())
            checkout.open(requireActivity(), obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun navigatePaymentFail() {
        findNavController().navigate(R.id.action_mycartFragment_to_paymentFailedFragment)
    }

    private fun myAllCart() {
        binding.clPaymentSummary.visibility = View.GONE
        binding.rvAllCart.visibility = View.GONE
        binding.clEmptyCart.visibility = View.VISIBLE
        binding.MyCartNavigateToCourses.setOnClickListener {

            findNavController().navigate(R.id.action_mycartFragment_to_homeFragment)
        }
        cartViewModel.findAllCartItems(userId)
        cartViewModel.findAllCartItemsResult.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { data ->
                Log.e("CartItems", data.findAllCartItems.toString())
                var complementryId = ""
                 cartItems = data.findAllCartItems.map { cartItemData ->


                    binding.clEmptyCart.visibility = View.GONE
                    binding.parentData.visibility = View.VISIBLE
                    binding.clPaymentSummary.visibility = View.VISIBLE
                    binding.rvAllCart.visibility = View.VISIBLE
                    binding.clProccedToPay.visibility = View.VISIBLE
                    val course = cartItemData.course
                    courseName = course.name
                    if (!course.complementary_course.isNullOrEmpty())
                        complementryId = course.complementary_course
                    Log.e("complementryIDd", complementryId)
                     Log.e("kajal ${course.name}", course.with_installment_price.toString())
                    Log.e("coureseIDd", course.id)
                     if (course.with_installment_price == 0){
                         binding.tabLayoutContainer.visibility = View.GONE
                     }else{
                         binding.tabLayoutContainer.visibility = View.VISIBLE
                     }
                //    instAmountpaid = ((course.price ?: 0) + (course.with_installment_price ?: 0) * 0.6)
                    CartItem(
                        profileImageResId = course.banner_image ?: "", // Replace with actual logic for image
                        name = course.name,
                        viewDetails = "View Details",
                        forwardDetails = R.drawable.cart_arrow_right,
                        discount = course.discount ?: 0,
                        price = course.price ?: 0,
                        cartItemId = cartItemData.cartItem.id,
                        entityId = cartItemData.cartItem.entity_id,
                        cartId = cartItemData.cartItem.cart_id,
                        courseId = course.id,
                        withInstallmentPrice = course.with_installment_price ?: 0,
                        categoryId = course.category_id.toString(),
                        recommendCourseTags = course.course_tags
                    )

                }
                if (cartItems.isNotEmpty()){
                    if (cartItems[0].withInstallmentPrice==0){
                        Log.e("cartITemsprice",cartItems[0].withInstallmentPrice.toString())
                        binding.tabLayoutContainer.visibility = View.GONE
                    }else{
                        binding.tabLayoutContainer.visibility = View.VISIBLE
                    }
                }else{
                    // Handle empty cart case
                    Log.e("cartItems", "Cart is empty")
                    binding.tabLayoutContainer.visibility = View.GONE
                }



                binding.tvCartCount.text = "(${cartItems.size})"
                binding.cartBadge.text = cartItems.size.toString()
                if (cartItems.isNotEmpty()) {
                    cartItems[0].isSelected = true
                }

                // Ensure this observer is only added once
                if (complementryId != null) {
                getCourseByIDViewModel.fetchCourseById(complementryId)
                if (getCourseByIDViewModel.courseByID.hasActiveObservers().not()) {
                    getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { course ->
                        Log.e("listcourses", course.toString())

                        if (course != null) {
                            Log.e("listcourses", course.toString())
                            val freeCourseItem = CartItem(
                                profileImageResId = course.banner_image ?: "",
                                name = course.name,
                                viewDetails = "View Details",
                                forwardDetails = R.drawable.cart_arrow_right,
                                discount = course.discount ?: 0,
                                price = course.price ?: 0,
                                cartItemId = "",
                                entityId = course.id,
                                cartId = "", // Assuming this will be a new cart item
                                courseId = course.id,
                                withInstallmentPrice = course.with_installment_price ?: 0,
                                categoryId = course.category_id.toString(),
                                isFree = true,
                                recommendCourseTags = course.course_tags
                            )

                            if (freeCourseItem !in cartItems) {
                                val updatedCartItems = cartItems.toMutableList()
                                Log.e("FLKJ", freeCourseItem.toString())
                                updatedCartItems.add(freeCourseItem)
                                cartAdapter.updateCartItems(updatedCartItems)
                                cartItems = updatedCartItems
                            }
                        }
                            cartAdapter.updateCartItems(cartItems)
                            originalCartItems = cartItems
                             updateCartVisibility(cartItems)
                            Log.e("orfafaf",originalCartItems.toString())
                        //    cartAdapter.updateCartItems(originalCartItems)


                    })
                }
                }else{
                    cartAdapter.updateCartItems(cartItems)
                    originalCartItems = cartItems
                    updateCartVisibility(cartItems)
                }

            }.onFailure { exception ->
                Log.e("exception in cart", exception.toString())

                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateCartVisibility(cartItems: List<CartItem>) {

        val selectedItems = cartItems.find { it.isSelected }
        Log.e("getSelectedITem",selectedItems.toString())
        if (selectedItems!= null) {
            Log.e("lkkajlfa", "Cart has items")
            binding.clrvContainer.visibility = View.VISIBLE
            binding.CartTabLayout.visibility = View.VISIBLE
            binding.rvAllCart.visibility = View.VISIBLE
            binding.btnProceedToPay.visibility = View.VISIBLE

            // Show payment options if cart has items
            showFullPayment(selectedItems)
        }
    }

    override fun onCartItemRemoved() {
        binding.tvCartCount.text = "(0)"
        binding.cartBadge.text = "0"
        binding.clPaymentSummary.visibility = View.GONE
        binding.clProccedToPay.visibility = View.GONE
        binding.clEmptyCart.visibility = View.VISIBLE
        binding.clSecondbottomInstallement.visibility = View.GONE
        binding.MyCartNavigateToCourses.setOnClickListener {
            findNavController().navigate(R.id.action_mycartFragment_to_homeFragment)
        }
        binding.clrvContainer.visibility = View.GONE
        //   Toast.makeText(requireContext(), "Cart item removed", Toast.LENGTH_SHORT).show()
    }



    fun calculateDiscountedPrice(price: Double, withInstallmentPrice: Double, discountPrice: Double): Double {
        val totalPrice = price + withInstallmentPrice

        val discountedAmount = totalPrice - discountPrice

        val discountPercentage = 0.60
        val finalPrice = discountedAmount * (discountPercentage)

        return finalPrice
    }

    fun withoutFree(){
        cartViewModel.findAllCartItems(userId)
        cartViewModel.findAllCartItemsResult.observe(viewLifecycleOwner, Observer { result ->

            result.onSuccess { data ->
                Log.e("CartItems", data.findAllCartItems.toString() )
                val cartItems = data.findAllCartItems.mapNotNull { cartItemData ->
                    val course = cartItemData.course
                    Log.e("coursevalue",course.toString())
                    // instAmountpaid = ((course.price ?: 0) + (course.with_installment_price?:0) - (course.discount?:0))* 0.6
                    CartItem(
                        profileImageResId = course.banner_image ?: "", // Replace with actual logic for image
                        name = course.name,
                        viewDetails = "View Details",
                        forwardDetails = R.drawable.cart_arrow_right,
                        discount = course.discount ?: 0,
                        price = course.price ?: 0,
                        cartItemId = cartItemData.cartItem.id,
                        entityId = cartItemData.cartItem.entity_id,
                        cartId = cartItemData.cartItem.cart_id,
                        courseId = course.id,
                        withInstallmentPrice = course.with_installment_price ?: 0,
                        categoryId = course.category_id.toString(),
                        recommendCourseTags = course.course_tags
                    )

                }
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
                  //  showFullPayment()
                } else {
                    // Optionally show a message indicating no items are available
                    Toast.makeText(requireContext(), "No items available in the cart", Toast.LENGTH_SHORT).show()
                }
            }.onFailure { exception ->
                Log.e("exception in cart",exception.toString())
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCartItemClicked(cartItem: CartItem) {
        val bundle = Bundle().apply {
            putStringArrayList("recommendCourseTags", ArrayList(cartItem.recommendCourseTags))
            putString("course_id", cartItem.courseId)
        }
        findNavController().navigate(R.id.exploreFragment,bundle)
    }


}



