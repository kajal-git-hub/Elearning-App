package com.student.competishun.ui.fragment.test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestBinding
import com.student.competishun.databinding.FragmentViewSolutionBinding

class ViewSolutionFragment : Fragment() {

    private var _binding: FragmentViewSolutionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewSolutionBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getString("QUESTION_ID", "")
        }
        testSubjectSpinner()
        clickListener()
    }

    private fun testSubjectSpinner(){
        val planetsArray = arrayOf("Maths", "Chemistry", "Physics", "Biology")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, planetsArray)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subject.adapter = adapter
        binding.subject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun clickListener(){
        binding.tabRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                binding.textSolutionButton.id->{
                    binding.textSolutionView.visibility = View.VISIBLE
                    binding.textVideoView.visibility = View.GONE
                }
                binding.videoSolutionButton.id->{
                    binding.textSolutionView.visibility = View.GONE
                    binding.textVideoView.visibility = View.VISIBLE
                }
            }
        }

        binding.textVideoView.setOnClickListener { goToPlayerPage(it.findNavController(), "", "") }
        binding.back.setOnClickListener { it.findNavController().popBackStack() }

    }

    private fun goToPlayerPage(navController: NavController, videourl: String, name:String) {
        val bundle = Bundle().apply {
            putString("url", videourl)
            putString("url_name", name)
        }
        Log.d("videourl",videourl)
        navController.navigate(R.id.action_homeFragment_to_mediaFragment,bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}