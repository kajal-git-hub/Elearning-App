package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.NotificationItem
import xyz.penpencil.competishun.data.model.NotificationSection
import xyz.penpencil.competishun.databinding.FragmentNotificationBinding
import xyz.penpencil.competishun.ui.adapter.NotificationSectionAdapter

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationSectionAdapter: NotificationSectionAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvNotificationSections)

        val todayNotifications = listOf(
            NotificationItem(
                R.drawable.avatar_type,
                "Aman check out these courses",
                "IIT-JEE Revision Course 2024",
                null
            ),
            NotificationItem(
                R.drawable.avatar_type,
                "Tips & Tricks to Score 200+ in Mains",
                "Aman must read for you.",
                "https://s3-alpha-sig.figma.com/img/533e/b91c/2fea47da35c57a6d6772dc40113f1352?Expires=1724630400&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=ceHI7z0QAjXh7zruVbb4N9HOmKGUW4MpFs1ivqwDCOm00FR1LuUbZYrP8J-x5RvIn3PCyGIHvEdPFelKtPCYbBjO25lh3Iyi5~S6uEjDLxOR6yzV66Jy7MhW53HVMq5YNJnyZWgydFZKtB2fo8hM4jzhvk7b89KFLnysUBuorsNBOKh~ihKg3n-~oEgaG53GaTJ--7h8-5HhNFzdo4EFh4nrmgssXbPMsXn7FvenNDmiClYfofQKGSAGPmKOJXIOwbk9N9xIae5ojzJqou0AzZP9a0pAdw0qKHHdV2dJRnhKR3UWO6YKSNnlru4R3FuGtY6aqoUGZzawdfQu0EBjmw__"
            ),
            NotificationItem(
                R.drawable.avatar_type,
                "Aman check out these courses",
                "IIT-JEE Revision Course 2024",
                null
            ),
        )
        val yesterdayNotifications = listOf(
            NotificationItem(
                R.drawable.avatar_type,
                "Aman check out these courses",
                "IIT-JEE Revision Course 2024",
                null
            ),
            NotificationItem(
                R.drawable.avatar_type,
                "Tips & Tricks to Score 200+ in Mains",
                "Aman must read for you.",
                null
            )
        )

        val sections = listOf(
            NotificationSection("Today", todayNotifications),
            NotificationSection("Yesterday", yesterdayNotifications)
        )

        notificationSectionAdapter = NotificationSectionAdapter(sections)
        recyclerView.adapter = notificationSectionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


    }

}