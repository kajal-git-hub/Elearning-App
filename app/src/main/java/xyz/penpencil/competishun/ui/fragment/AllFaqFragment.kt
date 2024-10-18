package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.penpencil.competishun.data.model.FAQItem
import xyz.penpencil.competishun.ui.adapter.FAQAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentAllFaqBinding

@AndroidEntryPoint
class AllFaqFragment : DrawerVisibility() {

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



        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

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
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
