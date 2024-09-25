package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.FragmentDownloadBinding
import com.student.competishun.ui.adapter.DownloadedItemAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadFragment : Fragment(),DownloadedItemAdapter.OnVideoClickListener {


    private lateinit var viewModel: VideourlViewModel

    private lateinit var binding: FragmentDownloadBinding
    private lateinit var adapter: DownloadedItemAdapter
    private var allDownloadedItems: List<TopicContentModel> = emptyList()

    private var pdfItemsSize = ""
    private var videoItemsSize = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(VideourlViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.backIconDownloads.setOnClickListener {
           requireActivity().onBackPressedDispatcher.onBackPressed()
       }


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        binding.rvDownloads.layoutManager = LinearLayoutManager(requireContext())

        setupTabLayout()

        loadDownloadedItems()
    }

    private fun setupTabLayout() {
        binding.studentTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> showPdfItems()
                        1 -> showVideoItems()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadDownloadedItems() {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        allDownloadedItems = sharedPreferencesManager.getDownloadedItems()
        Log.d("allDownloadedItems", allDownloadedItems.toString())

        val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }
        val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }

        pdfItemsSize = pdfItems.size.toString()
        videoItemsSize = videoItems.size.toString()

        binding.studentTabLayout.getTabAt(0)?.text = "PDFs (${pdfItemsSize})"
        binding.studentTabLayout.getTabAt(1)?.text = "Videos (${videoItemsSize})"

        showPdfItems()
    }

    private fun showPdfItems() {
        val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }

        updateRecyclerView(pdfItems)
    }

    private fun showVideoItems() {
        val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }

        updateRecyclerView(videoItems)
    }

    override fun onVideoClick(folderContentId: String, name: String) {
        videoUrlApi(viewModel, folderContentId, name)
    }

    private fun updateRecyclerView(items: List<TopicContentModel>) {
        adapter = DownloadedItemAdapter(requireContext(), items,this,parentFragmentManager)
        binding.rvDownloads.adapter = adapter
    }

    private fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String, name: String) {
        viewModel.fetchVideoStreamUrl(folderContentId, "480p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("VideoUrl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("url_name", name)
                    putString("ContentId", folderContentId)
                }
                findNavController().navigate(R.id.mediaFragment, bundle)
            } else {
                // Handle error or null URL
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)

    }
}
