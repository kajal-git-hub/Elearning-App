package com.student.competishun.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

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

        // Show the GIF first
        binding.paymentTickGif.visibility = View.VISIBLE

        // Delay for 2 seconds to show the text
        Handler(Looper.getMainLooper()).postDelayed({
            binding.paymentSuccessText.visibility = View.VISIBLE
        }, 2000)

        // Delay for additional 1 second to shift the layout and change colors
        Handler(Looper.getMainLooper()).postDelayed({
            animateLayout()
        }, 3000)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
