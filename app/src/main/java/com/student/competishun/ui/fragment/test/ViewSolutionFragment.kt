package com.student.competishun.ui.fragment.test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentViewSolutionBinding

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
        clickListener()
    }

    private fun clickListener(){
        binding.back.setOnClickListener { it.findNavController().popBackStack() }
        binding.backBtn.setOnClickListener { it.findNavController().popBackStack() }
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