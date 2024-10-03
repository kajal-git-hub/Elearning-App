package com.student.competishun.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.FragmentDownloadBinding
import com.student.competishun.ui.adapter.DownloadedItemAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DownloadFragment : Fragment(), DownloadedItemAdapter.OnVideoClickListener {

    private lateinit var viewModel: VideourlViewModel
    private lateinit var binding: FragmentDownloadBinding
    private lateinit var adapter: DownloadedItemAdapter
    private var allDownloadedItems: List<TopicContentModel> = emptyList()

    private var pdfItemsSize = ""
    private var videoItemsSize = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(VideourlViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.TopViewDownloads.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        binding.rvDownloads.layoutManager = LinearLayoutManager(requireContext())

        setupTabLayout()
        setupToolbar()
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

    fun loadDownloadedItems() {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        allDownloadedItems = sharedPreferencesManager.getDownloadedItems()
        Log.d("allDownloadedItems", allDownloadedItems.toString())

        val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }
        val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }

        pdfItemsSize = pdfItems.size.toString()
        videoItemsSize = videoItems.size.toString()

        binding.studentTabLayout.getTabAt(0)?.text = "PDFs ($pdfItemsSize)"
        binding.studentTabLayout.getTabAt(1)?.text = "Videos ($videoItemsSize)"

        updateRecyclerView(pdfItems) // Default to show PDF items
    }

    private fun updateRecyclerView(items: List<TopicContentModel>) {
        adapter = DownloadedItemAdapter(requireContext(),
            items.toMutableList(), this, parentFragmentManager, this)
        binding.rvDownloads.adapter = adapter
    }

    private fun showPdfItems() {
        val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }
        adapter.updateItems(pdfItems)
    }

    private fun showVideoItems() {
        val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }
        adapter.updateItems(videoItems)
    }

    override fun onVideoClick(folderContentId: String, name: String) {
        playVideo(folderContentId, name)
    }

    private fun playVideo(folderContentId: String, name: String) {
        val videoFileURL = File(requireContext().filesDir, "$name.mp4").absolutePath

        if (videoFileURL.isNotEmpty()) {
            val bundle = Bundle().apply {
                putString("url", videoFileURL)
                putString("url_name", name)
                putString("ContentId", folderContentId)
            }
            findNavController().navigate(R.id.mediaFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Video file not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        val searchView = binding.TopViewDownloads.menu.findItem(R.id.action_search_download)?.actionView as? SearchView
        searchView?.queryHint = "Search Pdf/Video"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return true
            }
        })
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
