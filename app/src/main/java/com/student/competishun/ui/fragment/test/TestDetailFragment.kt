package com.student.competishun.ui.fragment.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentTestDetailBinding

class TestDetailFragment : Fragment() {

    private var _binding: FragmentTestDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.PrimaryColor)
        clickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        _binding = null
    }

    private fun clickListener(){
        val bundle = Bundle().apply {
            putString("MESSAGE", "Best Of Luck!")
            putBoolean("IS_TEST_START", true)
        }
        binding.submit.setOnClickListener {
            if (binding.instructionAccept.isChecked){
                it.findNavController().navigate(R.id.action_testDetailFragment_to_testSplashFragment, bundle)
            }else{
                Toast.makeText(it.context, "Please accept test instruction", Toast.LENGTH_SHORT).show()
            }
        }
        binding.back.setOnClickListener { it.findNavController().popBackStack() }
    }
}