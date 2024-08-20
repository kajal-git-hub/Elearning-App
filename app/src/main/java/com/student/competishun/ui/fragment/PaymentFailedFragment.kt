package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentPaymentBinding
import com.student.competishun.databinding.FragmentPaymetFailedBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.callbackFlow

@AndroidEntryPoint
class PaymentFailedFragment : Fragment() {

    private var _binding: FragmentPaymetFailedBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymetFailedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
          binding.btTryAgain.setOnClickListener{
              requireActivity().onBackPressedDispatcher.onBackPressed()
          }

    }

}