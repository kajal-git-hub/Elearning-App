package xyz.penpencil.competishun.ui.fragment.scoreboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.student.competishun.data.model.TestItem
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentScoreDashboardBinding
import xyz.penpencil.competishun.databinding.FragmentTestDashboardBinding
import xyz.penpencil.competishun.ui.adapter.score.ScoreboardAdapter
import xyz.penpencil.competishun.ui.adapter.test.AcademicTestAdapter
import xyz.penpencil.competishun.ui.adapter.test.AcademicTestResumeAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity


class ScoreDashboardFragment : Fragment() {

    private var _binding: FragmentScoreDashboardBinding? = null
    private val binding get() = _binding!!

    private var scoreboardAdapter: ScoreboardAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

        initAdapter()
    }

    private fun initUI(){
        (activity as? HomeActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {

                    true
                }
                else -> false
            }
        }

        binding.toolbar.setNavigationIcon(R.drawable.arrow_left_white)
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }

    }


    private fun initAdapter(){
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}