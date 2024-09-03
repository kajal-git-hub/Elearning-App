package com.student.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.FAQItem
import com.student.competishun.databinding.FragmentAllFaqBinding
import com.student.competishun.ui.adapter.FAQAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllFaqFragment : Fragment() {

    private var _binding: FragmentAllFaqBinding? = null
    private val binding get() = _binding!!

    private lateinit var faqAdapter: FAQAdapter
    private lateinit var faqItems: List<FAQItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register the back press callback here
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }

        binding.igFaqBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        faqItems = arguments?.getParcelableArrayList("faq_items") ?: emptyList()

        faqAdapter = FAQAdapter(faqItems)

        binding.rvAllFaq.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = faqAdapter
        }
    }

    private fun handleBackPressed() {
        findNavController().navigate(R.id.exploreFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
