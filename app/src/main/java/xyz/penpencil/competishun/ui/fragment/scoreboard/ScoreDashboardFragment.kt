package xyz.penpencil.competishun.ui.fragment.scoreboard

import android.app.Dialog
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.student.competishun.data.model.TestItem
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentScoreDashboardBinding
import xyz.penpencil.competishun.ui.adapter.ItemClickListener
import xyz.penpencil.competishun.ui.adapter.TestTypeAdapter
import xyz.penpencil.competishun.ui.adapter.score.ScoreboardAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.utils.DialogTestUtils

class ScoreDashboardFragment : Fragment() {

    private var _binding: FragmentScoreDashboardBinding? = null
    private val binding get() = _binding!!

    private var scoreboardAdapter: ScoreboardAdapter?=null
    private var testTypeAdapter : TestTypeAdapter?=null
    private var downloadDialog: Dialog?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initUI()

        initAdapter()

        binding.btViewPort.setOnClickListener {
            findNavController().navigate(R.id.SBoardLeaderboardFragment)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_bar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setMaxWidth(Integer.MAX_VALUE)

        // Optionally set up search listeners
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes for live search, if needed
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun initUI(){
        (activity as? HomeActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    Toast.makeText(requireContext(), "xcnvnxcbv", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.toolbar.setNavigationIcon(R.drawable.arrow_left_white)
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }

        downloadDialog = DialogTestUtils.showDownloadDialog(requireContext())

    }


    private fun initAdapter(){
        testTypeAdapter = TestTypeAdapter(listOf(TestItem("JEE-Mains", true),
            TestItem("JEE-Advanced", true),
            TestItem("NEET-UG", false),
            TestItem("AIIMS", true)), object : ItemClickListener {
            override fun onItemClick(isFirst: Boolean, item: TestItem) {

            }
        })
        binding.rvTestType.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        binding.rvTestType.adapter = testTypeAdapter

        scoreboardAdapter = ScoreboardAdapter(listOf(
            TestItem("Filter", true),
            TestItem("JEE-Mains", false),
            TestItem("JEE-Advanced", false),
            TestItem("NEET-UG", false),
            TestItem("NEET-UG", false),
            TestItem("NEET-UG", false),
            TestItem("NEET-UG", false),
            TestItem("NEET-UG", false),
            TestItem("NEET-UG", false),
            TestItem("NEET-UG", false),
            TestItem("NEET-UG", false),
            TestItem("AIIMS", false)
        ))
        binding.rvScore.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)
        binding.rvScore.adapter = scoreboardAdapter


        binding.filter.setOnClickListener { requireActivity().supportFragmentManager?.let {
            val filter = BottomSheetScoreboardFilterFragment {

            }
            filter.show(it, "")
        } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.PrimaryColor)
    }

}