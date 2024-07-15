package com.student.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.databinding.FragmentVerifyBinding
import com.student.competishun.ui.adapter.ExampleAdapter

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!


    private var etEnterHereText: EditText? = null
    private var etEnterCityText: EditText? = null
    private var btnGetSubmit2: Button? = null
    private var btnGetSubmit1:Button?=null
    private var recyclerview: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEnterHereText = view.findViewById(R.id.etEnterHereText)
        etEnterCityText = view.findViewById(R.id.etEnterCityText)
        btnGetSubmit2 = view.findViewById(R.id.btnGetSubmit2)
        btnGetSubmit1 = view.findViewById(R.id.btnGetSubmit1)

        recyclerview = view.findViewById(R.id.examRecyclerview)

        val constraintLayout: ConstraintLayout = view.findViewById(R.id.clAnimConstraint)
        val slideInAnimation = AnimationUtils.loadAnimation(this@OnBoardingFragment.context, R.anim.slide_in_bottom)
        constraintLayout.startAnimation(slideInAnimation)


        setupRecyclerView()
        setUpTextWatchers()
        updateButtonBackground()

        btnGetSubmit2?.setOnClickListener {
            Log.d("OnBoardingFragment", "Button clicked")
        }
        btnGetSubmit1?.setOnClickListener {
            findNavController().navigate(R.id.action_OnBoardingFragment_to_loginFragment)
        }

    }

    private fun setupRecyclerView() {
        val recyclerview = recyclerview
        val layoutManager = GridLayoutManager(this@OnBoardingFragment.context, 1)
        recyclerview?.layoutManager = layoutManager

        val dataList = listOf("Friends/Family", "Social Media", "Advertisment", "Other")
        val adapter = ExampleAdapter(dataList)
        recyclerview?.adapter = adapter
    }

    private fun setUpTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateButtonBackground()
            }
        }

        etEnterHereText?.addTextChangedListener(textWatcher)
        etEnterCityText?.addTextChangedListener(textWatcher)
    }

    private fun updateButtonBackground() {
        val text1 = etEnterHereText?.text.toString().trim()
        val text2 = etEnterCityText?.text.toString().trim()

        Log.d("OnBoardingFragment", "etEnterHereText: $text1, etEnterCityText: $text2")

        if (text1.isNotEmpty() && text2.isNotEmpty()) {
            btnGetSubmit2?.setBackgroundResource(R.drawable.second_getstarteddone)
        } else {
            btnGetSubmit2?.setBackgroundResource(R.drawable.second_getstarted)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        etEnterHereText = null
        etEnterCityText = null
        btnGetSubmit2 = null
    }
}
