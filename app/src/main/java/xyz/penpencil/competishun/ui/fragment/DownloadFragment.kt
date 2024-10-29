package xyz.penpencil.competishun.ui.fragment

import android.R.attr.fragment
import android.app.Activity
import android.os.Build
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentDownloadBinding
import xyz.penpencil.competishun.ui.adapter.DownloadedItemAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.ui.viewmodel.offline.CountFileType
import xyz.penpencil.competishun.ui.viewmodel.offline.TopicContentViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File


@AndroidEntryPoint
class DownloadFragment : DrawerVisibility(), DownloadedItemAdapter.OnVideoClickListener {

    private lateinit var viewModel: VideourlViewModel
    private lateinit var binding: FragmentDownloadBinding
    private lateinit var adapter: DownloadedItemAdapter

    private val topicContentViewModel: TopicContentViewModel by viewModels()

    private var allDownloadedItems: List<TopicContentModel> = emptyList()

    private var pdfItemsSize = ""
    private var videoItemsSize = ""
    private var selectedTabPosition: Int = 0
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

        savedInstanceState?.let {    selectedTabPosition = it.getInt("SELECTED_TAB_POSITION", 0)}


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

        binding.studentTabLayout.getTabAt(selectedTabPosition)?.select()

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
                        0 -> loadData("PDF")
                        1 -> loadData("VIDEO")
                    }
                    selectedTabPosition = it.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadDownloadedItems() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                topicContentViewModel.topicContentCountFileType.collect {
                    binding.studentTabLayout.getTabAt(0)?.text = "PDFs (${it.pdfCount})"
                    binding.studentTabLayout.getTabAt(1)?.text = "Videos (${it.videoCount})"
                    if (it.selectedCount == 0){
                        loadData("PDF")
                    }

                    if (it.selectedCount == 1){
                        loadData("VIDEO")
                    }
                }
            }
        }
    }

    private fun loadData(fileType: String){

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                topicContentViewModel.getTopicContentByFileType(fileType).collect { content ->
                    when (fileType) {
                        "PDF" -> {
                            updateRecyclerView(content)
                        }

                        "VIDEO" -> {
                            updateRecyclerView(content)
                        }

                        else -> {

                        }
                    }

                }
            }
        }
    }

    fun updateDownloadedItems(fileType:String) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        allDownloadedItems = sharedPreferencesManager.getDownloadedItems()
        if (fileType == "PDF") {
            val pdfItems = allDownloadedItems.filter { it.fileType == "PDF" }
            binding.studentTabLayout.getTabAt(0)?.text = "PDFs (${pdfItems.size})"
            updateRecyclerView(pdfItems)
        }else
        {
            val videoItems = allDownloadedItems.filter { it.fileType == "VIDEO" }
            binding.studentTabLayout.getTabAt(1)?.text = "Videos (${videoItems.size})"
            updateRecyclerView(videoItems)
        }
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

    override fun onDeleteClick(topicContentModel: TopicContentModel) {


    }

    private fun playVideo(folderContentId: String, name: String) {
        var file=File(context?.filesDir,"$name.mp4")
        if (!file.isFile){
            file = File("/storage/emulated/0/Download/$name.mp4")
        }
        val videoFileURL = file.absolutePath
        Log.e("FilePath", "File exists: ${file.exists()}, Path: $videoFileURL")

        if (videoFileURL.isNotEmpty()) {
            val bundle = Bundle().apply {
                putString("url", videoFileURL)
                putString("url_name", name)
            }
            findNavController().navigate(R.id.downloadMediaPlayerFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Video file not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        val searchView = binding.TopViewDownloads.menu.findItem(R.id.action_search_download)?.actionView as? SearchView
        searchView?.queryHint = "Search Pdf/Video"

        searchView?.setOnSearchClickListener {
            binding.tittleTb.visibility = View.GONE
            binding.clNote.visibility = View.GONE
        }

        searchView?.setOnCloseListener {
            binding.tittleTb.visibility = View.VISIBLE
            binding.clNote.visibility = View.GONE
            false
        }

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
        setStatusBarGradiant(requireActivity())
        (activity as? HomeActivity)?.hideCallingSupport()


    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SELECTED_TAB_POSITION", selectedTabPosition)}

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.setBackgroundDrawable(null)
    }

    private fun setStatusBarGradiant(activity: Activity) {
        val window: Window = activity.window
        val background =ContextCompat.getDrawable(activity, R.drawable.gradiant_bg_downloads)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity,android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }
}
