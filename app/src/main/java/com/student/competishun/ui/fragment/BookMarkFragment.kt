package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.FragmentBookMarkBinding
import com.student.competishun.ui.adapter.BookMarkAdapter
import com.student.competishun.ui.adapter.DownloadedItemAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.utils.SharedPreferencesManager
import java.io.File

class BookMarkFragment : Fragment()  ,BookMarkAdapter.OnVideoClickListener{

    private lateinit var binding : FragmentBookMarkBinding
    private var allDownloadedItems: List<TopicContentModel> = emptyList()
    private lateinit var bookmarkAdapter: BookMarkAdapter
    private var pdfItemsSize = ""
    private var videoItemsSize = ""
    private lateinit var emptyStateLayout: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookMarkBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        emptyStateLayout = binding.clEmptySearchBookmark

        binding.TopViewBookMark.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rvBookmark.layoutManager = LinearLayoutManager(requireContext())

        setupToolbar()
        setupTabLayout()
        loadDownloadedItems()

    }
    private fun showPdfItems() {
        val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }
//        updateRecyclerView(pdfItems)
        bookmarkAdapter.updateItems(pdfItems)
//        checkEmptyState() // Check for empty state

    }

    private fun showVideoItems() {
        val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }
//        updateRecyclerView(videoItems)
        bookmarkAdapter.updateItems(videoItems)
//        checkEmptyState() // Check for empty state


    }
    fun loadDownloadedItems() {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        allDownloadedItems = sharedPreferencesManager.getDownloadedItemsBm()
        Log.d("allDownloadedItems", allDownloadedItems.toString())

        val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }
        val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }

        pdfItemsSize = pdfItems.size.toString()
        videoItemsSize = videoItems.size.toString()

        binding.BookmarkTabLayout.getTabAt(0)?.text = "PDFs ($pdfItemsSize)"
        binding.BookmarkTabLayout.getTabAt(1)?.text = "Videos ($videoItemsSize)"

        updateRecyclerView(pdfItems)
//        checkEmptyState()
    }
    private fun updateRecyclerView(items: List<TopicContentModel>) {
        bookmarkAdapter = BookMarkAdapter(
            requireContext(),
            items.toMutableList(),  parentFragmentManager,this,this)
        binding.rvBookmark.adapter = bookmarkAdapter
//        checkEmptyState() // Check for empty state after updating the adapter

    }
    private fun setupTabLayout() {
        binding.BookmarkTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    private fun checkEmptyState() {
        val isEmpty = bookmarkAdapter.itemCount == 0
        binding.rvBookmark.visibility = if (isEmpty) View.GONE else View.VISIBLE
        emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
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
        val searchView = binding.TopViewBookMark.menu.findItem(R.id.action_search_download)?.actionView as? SearchView
        searchView?.queryHint = "Search Pdf/Video"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                bookmarkAdapter.filter(newText)
                return true
            }
        })
    }

}