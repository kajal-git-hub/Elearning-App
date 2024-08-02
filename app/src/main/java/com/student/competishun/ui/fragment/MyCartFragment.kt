package com.student.competishun.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.data.model.CartItem
import com.student.competishun.databinding.FragmentMyCartBinding
import com.student.competishun.ui.adapter.MyCartAdapter

class MyCartFragment : Fragment() {
    private var _binding: FragmentMyCartBinding? = null
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
        findNavController().navigate(R.id.exploreFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProceedToPay.setOnClickListener {
            navigateToLoaderScreen()
        }

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

        val cartItems = listOf(
            CartItem(R.drawable.frame_1707480855, "Prakhar Integrated (Fast Lane-2) 2024-25", R.drawable.frame_1707480886, "View Details", R.drawable.cart_arrow_right),
            CartItem(R.drawable.frame_1707480855, "Prakhar Integrated (Fast Lane-2) 2024-25", R.drawable.frame_1707480886, "View Details", R.drawable.cart_arrow_right)
        )

        val cartAdapter = MyCartAdapter(cartItems)
        binding.rvAllCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
        }
    }

    private fun navigateToLoaderScreen() {
        findNavController().navigate(R.id.action_mycartFragment_to_paymentLoaderFragment)

        Handler(Looper.getMainLooper()).postDelayed({
            if (findNavController().currentDestination?.id == R.id.paymentLoaderFragment) {
                findNavController().navigate(R.id.action_paymentLoaderFragment_to_paymentFragment)
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
