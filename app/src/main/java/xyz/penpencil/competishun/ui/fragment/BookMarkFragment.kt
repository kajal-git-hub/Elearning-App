package xyz.penpencil.competishun.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentBookMarkBinding
import xyz.penpencil.competishun.ui.adapter.BookMarkAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File

class BookMarkFragment : DrawerVisibility(), BookMarkAdapter.OnVideoClickListener {

    private lateinit var binding: FragmentBookMarkBinding
    private var allDownloadedItems: List<TopicContentModel> = emptyList()
    private lateinit var bookmarkAdapter: BookMarkAdapter
    private var pdfItemsSize = ""
    private var videoItemsSize = ""
    private lateinit var emptyStateLayout: View

    private var selectedTabPosition: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookMarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let { selectedTabPosition = it.getInt("SELECTED_TAB_POSITION", 0) }

        view.setFocusableInTouchMode(true)
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    v?.findNavController()?.popBackStack()
                    return true
                }
                return false
            }

        })


        binding.BookmarkTabLayout.getTabAt(selectedTabPosition)?.select()


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
        updateRecyclerView(pdfItems)
        bookmarkAdapter.updateItems(pdfItems)
        checkEmptyState() // Check for empty state

    }

    private fun showVideoItems() {
        val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }
        updateRecyclerView(videoItems)
        bookmarkAdapter.updateItems(videoItems)
        checkEmptyState() // Check for empty state


    }

    fun updateDownloadedItems(fileType: String) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        allDownloadedItems = sharedPreferencesManager.getDownloadedItemsBm()
        if (fileType == "PDF") {
            val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }
            binding.BookmarkTabLayout.getTabAt(0)?.text = "PDFs (${pdfItems.size})"
            updateRecyclerView(pdfItems)
        } else {
            val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }
            binding.BookmarkTabLayout.getTabAt(1)?.text = "Videos (${videoItems.size})"
            updateRecyclerView(videoItems)
        }
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

        if (binding.BookmarkTabLayout.getTabAt(selectedTabPosition)?.text == "PDFs ($pdfItemsSize)") {
            updateRecyclerView(pdfItems)
        }
        if (binding.BookmarkTabLayout.getTabAt(selectedTabPosition)?.text == "Videos ($videoItemsSize)") {
            updateRecyclerView(videoItems)
        }
        checkEmptyState()
    }

    private fun updateRecyclerView(items: List<TopicContentModel>) {
        bookmarkAdapter = BookMarkAdapter(
            requireContext(),
            items.toMutableList(), parentFragmentManager, this, this
        )
        binding.rvBookmark.adapter = bookmarkAdapter
        checkEmptyState()

    }

    private fun setupTabLayout() {
        binding.BookmarkTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> showPdfItems()
                        1 -> showVideoItems()
                    }
                    selectedTabPosition = it.position
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
            findNavController().navigate(R.id.downloadMediaPlayerFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Video file not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        val searchView =
            binding.TopViewBookMark.menu.findItem(R.id.action_search_download)?.actionView as? SearchView
        searchView?.queryHint = "Search Pdf/Video"


        searchView?.setOnSearchClickListener {
            binding.screenBmTitleBookmark.visibility = View.GONE
        }

        searchView?.setOnCloseListener {
            binding.screenBmTitleBookmark.visibility = View.VISIBLE
            false
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                bookmarkAdapter.filter(newText)
                return true
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SELECTED_TAB_POSITION", selectedTabPosition)
    }

    override fun onResume() {
        super.onResume()
        setStatusBarGradiant(requireActivity())
        (activity as? HomeActivity)?.hideCallingSupport()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.setBackgroundDrawable(null)
    }

    private fun setStatusBarGradiant(activity: Activity) {
        val window: Window = activity.window
        val background = ContextCompat.getDrawable(activity, R.drawable.gradiant_bg_bookmark)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity,android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }

}