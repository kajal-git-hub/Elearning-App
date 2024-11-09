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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentDownloadBinding
import xyz.penpencil.competishun.ui.adapter.DownloadedItemAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.offline.TopicContentViewModel
import java.io.File
import java.util.Locale


@AndroidEntryPoint
class DownloadFragment : DrawerVisibility(), DownloadedItemAdapter.OnVideoClickListener {

    private lateinit var binding: FragmentDownloadBinding
    private var adapter: DownloadedItemAdapter?= null
    private val topicContentViewModel: TopicContentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


    private fun selectTab(pos: Int = 0){
     /*   val tabToSelect = binding.studentTabLayout.getTabAt(pos)
        tabToSelect?.let {
            binding.studentTabLayout.selectTab(it)
        }*/
    }

    private fun setupTabLayout() {
        binding.studentTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 ->{
                            topicContentViewModel.updateSelectedCount(0)
                            loadData("PDF")
                        }
                        1 -> {
                            topicContentViewModel.updateSelectedCount(1)
                            loadData("VIDEO")
                        }
                    }
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
                        binding.studentTabLayout.getTabAt(0)?.select()
                    }

                    if (it.selectedCount == 1){
                        loadData("VIDEO")
                        binding.studentTabLayout.getTabAt(1)?.select()
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


    private fun updateRecyclerView(items: List<TopicContentModel>) {
        if (items.isEmpty()){
            binding.rvDownloads.visibility = View.GONE
            binding.clEmptyDownloads.visibility = View.VISIBLE
            return
        }else {
            binding.rvDownloads.visibility = View.VISIBLE
            binding.clEmptyDownloads.visibility = View.GONE
        }
        adapter = DownloadedItemAdapter(requireContext(), items.toMutableList(), this, parentFragmentManager, this)
        binding.rvDownloads.adapter = adapter
    }

    override fun onVideoClick(topicContentModel: TopicContentModel) {
        if (topicContentModel.localPath.isNotEmpty()) {
            findNavController().navigate(R.id.downloadMediaPlayerFragment, Bundle().apply {
                putSerializable("VIDEO_DATA", topicContentModel)
            })
        } else {
            Toast.makeText(requireContext(), "Video file not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeleteClick(topicContentModel: TopicContentModel) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                try {
                    if (File(topicContentModel.localPath).exists()){
                        File(topicContentModel.localPath).delete()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "onDeleteClick: ${e.message}" )
                }
                topicContentViewModel.deleteTopicContent(topicContentModel)
            }
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
                adapter?.filter?.filter(newText?.trim()?.lowercase(Locale.getDefault()))
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
