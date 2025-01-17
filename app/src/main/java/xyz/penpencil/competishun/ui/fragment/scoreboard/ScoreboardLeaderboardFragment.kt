package xyz.penpencil.competishun.ui.fragment.scoreboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.scoreboard.LeaderBoardUser
import xyz.penpencil.competishun.databinding.FragmentScoreboardLeaderboardBinding
import xyz.penpencil.competishun.ui.adapter.scoreboard.LeaderBoardAdapter

class ScoreboardLeaderboardFragment : Fragment() {

    private lateinit var binding: FragmentScoreboardLeaderboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreboardLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userList = listOf(
            LeaderBoardUser("4", "Abhishek Jain", "360"),
            LeaderBoardUser("5", "Surbhi Srivastava", "240"),
            LeaderBoardUser("6", "Amar Soni", "150"),
            LeaderBoardUser("7", "Riya Srivastava", "180")
        )

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvNameScore.layoutManager = layoutManager
        val adapter = LeaderBoardAdapter(userList)
        binding.rvNameScore.adapter = adapter

        binding.studentTabLayout.apply {
            addTab(newTab().setText("Result Analysis"))
            addTab(newTab().setText("Leaderboard"))
        }

        binding.clRankPosition.setOnClickListener {
            findNavController().navigate(R.id.ScoreboardSolutionAnswer)
        }
    }
}
