package xyz.penpencil.competishun.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.CreateCartViewModel
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.OrdersViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentPaymentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var binding: FragmentPaymentBinding
    private val userViewModel: UserViewModel by viewModels()
    private val ordersViewModel: OrdersViewModel by viewModels()
    private lateinit var  helperFunctions: HelperFunctions
    private val cartViewModel: CreateCartViewModel by viewModels()
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }
    }

    private fun handleBackPressed() {
        findNavController().navigate(R.id.myCartFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)


        val binding = binding
        helperFunctions= HelperFunctions()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        var userId = arguments?.getString("userId").toString()
        Log.e("userid  $userId: ",sharedPreferencesManager.userId.toString())
        //  startActivity(Intent(requireContext(), HomeActivity::class.java))
        if (!sharedPreferencesManager.userId.isNullOrEmpty()) {
            orderdetails(ordersViewModel,sharedPreferencesManager.userId.toString())

        }else orderdetails(ordersViewModel,userId)
        binding.paymentTickGif.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.paymentSuccessText.visibility = View.VISIBLE
        }, 2000)

        // Delay for additional 1 second to shift the layout and change colors
        Handler(Looper.getMainLooper()).postDelayed({
            animateLayout()
        }, 3000)

        binding.clStartBottomBar.setOnClickListener{ findNavController().navigate(R.id.homeFragment) }
        getUserDetails()
        binding.btReceipt.setOnClickListener {
            binding.btReceipt.isEnabled = false
         var transactionId = sharedPreferencesManager.rzpOrderId
            if (transactionId != null) {
                downloadReceipt(transactionId)
            }
        }
    }

    private fun removeCourse( cartItemId:String){
        cartViewModel.removeCartItem(cartItemId)
        cartViewModel.removeCartItemResult.observe(viewLifecycleOwner)
        { result ->

            result.onSuccess {
                Log.e("SuccessfullyRemovedCart",it.toString())
            }.onFailure {
                // Handle failure, e.g., show an error message
                Log.e("Failed to remove cart: ",it.message.toString())
            }
        }

    }

    // TODO : CALLBACK FOR THE DOWNLOAD RECEIPT
    private fun downloadReceipt( transactionId:String){
        ordersViewModel.generateReceipt(transactionId)
        ordersViewModel.receiptResult.observe(viewLifecycleOwner)
        { result ->

            result.onSuccess {
                val receiptLink = it.generateReceipt
                Log.e("ReceiptLink",receiptLink)
               helperFunctions.downloadPdf(requireContext(),receiptLink,"Payment Invoice")

            }.onFailure {
                binding.btReceipt.isEnabled = true
                // Handle failure, e.g., show an error message
                Log.e("Failed to download ReceiptLink: ",it.message.toString())
            }
        }

    }

    fun getUserDetails()
    {
        userViewModel.fetchUserDetails()
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                binding.tvRollNumber.text = userDetails.userInformation.rollNumber
                val courseId = sharedPreferencesManager.COURSEID
                if (courseId != null) {
                    coursePayment(ordersViewModel, userDetails.id,courseId )
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun orderdetails(ordersViewModel: OrdersViewModel, userId:String
    ){
        binding.progressBar.visibility = View.VISIBLE
        val userIds = listOf(userId)
        ordersViewModel.fetchOrdersByUserIds(userIds)
        ordersViewModel.ordersByUserIds.observe(viewLifecycleOwner) { orders ->
            Log.e("getorderDetails",orders.toString())
            binding.progressBar.visibility = View.GONE
            binding.clMypurchase.visibility = View.VISIBLE
            orders?.let {

                for (order in orders) {
                    Log.e("orderDataEntity",order.entityId)
                    binding.tvAmount.text = "₹ ${order.amountPaid.toInt()}"
                    binding.paymentSuccessText.text = "Payment Sucessfully"
                    if (sharedPreferencesManager.paymentType == "partial"){
                        binding.tvPaymentType.text = "Installment"
                    }else if (sharedPreferencesManager.paymentType == "full"){
                        binding.tvPaymentType.text = "One-Time Payment"
                    }
                    sharedPreferencesManager.COURSEID?.let { it1 -> observeCourseById(it1) }
                    binding.tvPaidDate.text =  getCurrentDateString()
                  val cartItemId =   sharedPreferencesManager.getString("cartItemId","")
                    if (cartItemId!="" && cartItemId != null){
                            removeCourse(cartItemId)
                    }
                    if (order.paymentStatus.equals("paid", ignoreCase = true)) {
                        // Save the payment success status to SharedPreferences
                        sharedPreferencesManager.putBoolean("savePaymentSuccess", true)
                        sharedPreferencesManager.isBottomSheetShown = false
                        Log.d("Order", "Payment Successful. Amount Paid: ${order.amountPaid}")
                    } else {
                        // Save the payment failure status to SharedPreferences
                        sharedPreferencesManager.putBoolean("savePaymentSuccess", false)
                        Log.d("Order", "Payment Failed. Status: ${order.paymentStatus}")
                    }
                    Log.d("Order", "Amount Paid: ${order.amountPaid}, Entity ID: ${order.entityId}, Payment Status: ${order.paymentStatus}")
                }
            } ?: run {

                // Handle the case where orders is null
                Log.e("OrdersFragment", "No orders found")
            }
        }

        // Fetch orders by user IDs

    }

    private fun coursePayment(ordersViewModel: OrdersViewModel, userId:String, courseId: String){
        binding.progressBar.visibility = View.VISIBLE
        ordersViewModel.getPaymentBreakdown(courseId,userId)
        ordersViewModel.paymentResult.observe(viewLifecycleOwner) { order ->
            binding.progressBar.visibility = View.GONE
            binding.clMypurchase.visibility = View.VISIBLE
            order?.forEach {
                binding.tvCourseName.text = it.courseName
                binding.tvAmount.text = it.amount.toInt().toString()
                if (it.paymentType == "partial"){
                    binding.tvPaymentType.text = "Installment"
                }else if (it.paymentType == "full"){
                    binding.tvPaymentType.text = "One-Time Payment"
                }
            }
        }
    }
    private fun animateLayout() {
        val clTickSuccess = binding.clTickSuccess
        val paymentSuccessText = binding.paymentSuccessText
        val rootLayout = binding.root as ConstraintLayout // Get the root ConstraintLayout

        // Animator to shift the layout to the top
        val moveUp = ObjectAnimator.ofFloat(clTickSuccess, "translationY", -760f)

        // Animator to change the background color of the root layout
        val backgroundColorAnimator = ObjectAnimator.ofArgb(
            rootLayout,
            "backgroundColor",
            ContextCompat.getColor(requireContext(), R.color.white),
            ContextCompat.getColor(requireContext(), R.color.blue)
        )

        // Animator to change the text color
        val textColorAnimator = ObjectAnimator.ofArgb(
            paymentSuccessText,
            "textColor",
            ContextCompat.getColor(requireContext(), R.color._357f2f),
            ContextCompat.getColor(requireContext(), R.color.white)
        )

        // Combine all animations into an AnimatorSet
        val animatorSet = AnimatorSet().apply {
            playTogether(moveUp, backgroundColorAnimator, textColorAnimator)
            duration = 1000
        }

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // Show the start bottom bar after the animation completes
                binding.clStartBottomBar.visibility = View.VISIBLE
                binding.clPaymenthistory.visibility = View.VISIBLE
            }
        })

        animatorSet.start()
    }

    private fun observeCourseById(entityId:String) {

        getCourseByIDViewModel.fetchCourseById(entityId)
        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { courses ->
            Log.e("listcourses", courses.toString())

            if (courses != null) {
                Log.e("listcourses", courses.toString())
                val courseName = "${courses.name}"
              //  binding.tvCourseName.text = courseName
             //   binding.className.text = helperFunctions.toDisplayString(courses.course_class?.name) +" Class"
             //   binding.targettv.text = "Target ${courses.target_year}"
                binding.starts.text = helperFunctions.formatCourseDate(courses.course_start_date.toString())
                val categoryName = courses.category_name?.split(" ") ?: emptyList()
                val wordsWithoutLast = categoryName.dropLast(1)
            //  binding.category.text = wordsWithoutLast.joinToString(" ")


            }
        })
    }
    fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("dd-MM,yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferencesManager.paymentType = ""
        sharedPreferencesManager.COURSEID = ""
    }

}
