package com.student.competishun.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.CourseFItem
import com.student.competishun.databinding.FragmentPaymentBinding
import com.student.competishun.ui.adapter.CourseFeaturesAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.GetCourseByIDViewModel
import com.student.competishun.ui.viewmodel.OrdersViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private val ordersViewModel: OrdersViewModel by viewModels()
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
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        var userId = arguments?.getString("userId").toString()
        Log.e("userid  $userId: ",sharedPreferencesManager.userId.toString())
        //  startActivity(Intent(requireContext(), HomeActivity::class.java))
        if (!sharedPreferencesManager.userId.isNullOrEmpty()) {
            orderdetails(ordersViewModel,sharedPreferencesManager.userId.toString())
        }else orderdetails(ordersViewModel,userId)
        binding.paymentTickGif.visibility = View.VISIBLE

        // Delay for 2 seconds to show the text
        Handler(Looper.getMainLooper()).postDelayed({
            binding.paymentSuccessText.visibility = View.VISIBLE
        }, 2000)

        // Delay for additional 1 second to shift the layout and change colors
        Handler(Looper.getMainLooper()).postDelayed({
            animateLayout()
        }, 3000)

        binding.clStartBottomBar.setOnClickListener{

         findNavController().navigate(R.id.PersonalDetailsFragment)
        }
    }

    fun orderdetails(ordersViewModel: OrdersViewModel,userId:String
    ){
        val userIds = listOf(userId)
        ordersViewModel.fetchOrdersByUserIds(userIds)
        ordersViewModel.ordersByUserIds.observe(viewLifecycleOwner, Observer { orders ->
            orders?.let {
                // Handle the list of orders
                for (order in orders) {
                    binding.tvAmount.text = "₹ ${order.amountPaid/100}"
                    binding.paymentSuccessText.text = order.paymentStatus + "Successfully"
                    Log.d("Order", "Amount Paid: ${order.amountPaid}, Entity ID: ${order.entityId}, Payment Status: ${order.paymentStatus}")
                }
            } ?: run {

                // Handle the case where orders is null
                Log.e("OrdersFragment", "No orders found")
            }
        })

        // Fetch orders by user IDs

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
                val coursefeature = courses.course_features
                // Do something with course features
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
