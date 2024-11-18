package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.RefundItem
import xyz.penpencil.competishun.databinding.FragmentRefundCancellationBinding
import xyz.penpencil.competishun.ui.adapter.RefundAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity

class RefundCancellation : Fragment() {

    private lateinit var binding : FragmentRefundCancellationBinding
    private lateinit var refundAdapter: RefundAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding =  FragmentRefundCancellationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val refundList = listOf(
            RefundItem(
                "How is the refund policy determined for purchases ?",
                "The refund policy depends on the type of course or subscription purchased. Specific refund eligibility and conditions are outlined in the course description for each batch, and only certain courses are eligible for refunds."
            ),
            RefundItem(
                "Are all courses eligible for a refund ?",
                "No, each course may have distinct refund terms based on its structure and delivery format. Refund eligibility is clearly indicated in the course description. If there is no refund information provided, the course is considered non-refundable."
            ),
            RefundItem(
                "What does a user confirm by proceeding with a purchase ?",
                "By proceeding with a purchase, the user confirms that they have reviewed and agreed to the specific refund terms for their course or subscription."
            ),

        )

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }

        binding.tvTitle.setOnClickListener {
            findNavController().popBackStack()
        }

//        faqItems = arguments?.getParcelableArrayList("faq_items") ?: emptyList()

        refundAdapter = RefundAdapter(refundList)

        binding.rvRefundPolicy.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = refundAdapter
        }

    }

    private fun handleBackPressed() {
        findNavController().navigate(R.id.homeFragment)
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
    }


}