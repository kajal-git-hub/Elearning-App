package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.CoursePaymentDetails
import xyz.penpencil.competishun.databinding.FragmentMyPurchaseBinding
import xyz.penpencil.competishun.ui.adapter.CoursePaymentAdapter
import xyz.penpencil.competishun.ui.adapter.PurchaseStatus
import xyz.penpencil.competishun.ui.adapter.PurchaseStatusAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity

class MyPurchaseFragment : Fragment() {
    private var _binding: FragmentMyPurchaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var coursePaymentAdapter: CoursePaymentAdapter
    private lateinit var coursePaymentList: List<CoursePaymentDetails>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        binding.MyPurchaseTopView.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }


        val statusList = listOf(
            PurchaseStatus("All"),
            PurchaseStatus("Successful"),
            PurchaseStatus("Failed"),
            PurchaseStatus("Processing")
        )

        binding.rvToggleButtons.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = PurchaseStatusAdapter(context, statusList){ selectedStatus ->
                filterPurchaseList(selectedStatus)

            }
        }

        coursePaymentList = listOf(
            CoursePaymentDetails(
                "COMPLETE",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.group_1707479053,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                false // Refund note visible
            ),
            CoursePaymentDetails(
                "FAILED",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.failed_status,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                false // Refund note visible
            ),
            CoursePaymentDetails(
                "REFUND COMPLETE",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.refund_status,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                true // Refund note visible
            ),
            CoursePaymentDetails(
                "PAYMENT PROCESSING",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.process_status,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                true // Refund note visible
            ),
        )

        coursePaymentAdapter = CoursePaymentAdapter(coursePaymentList)
        binding.recyclerViewPurchases.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coursePaymentAdapter
        }
    }
    private fun filterPurchaseList(selectedStatus: String) {
        val filteredList = when (selectedStatus) {
            "All" -> coursePaymentList
            "Successful" -> coursePaymentList.filter { it.purchaseStatus == "COMPLETE" }
            "Failed" -> coursePaymentList.filter { it.purchaseStatus == "FAILED" }
            "Processing" -> coursePaymentList.filter { it.purchaseStatus == "PAYMENT PROCESSING" }
            else -> coursePaymentList
        }

        // Update the adapter with the filtered list
        coursePaymentAdapter.updateData(filteredList)
    }

}