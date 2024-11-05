package xyz.penpencil.competishun.ui.fragment.scoreboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.scoreboard.DateItem
import xyz.penpencil.competishun.data.model.scoreboard.LeaderBoardUser
import xyz.penpencil.competishun.databinding.FragmentScoreboardLeaderboardBinding
import xyz.penpencil.competishun.databinding.FragmentScoreboardViewSolutionAnswerBinding
import xyz.penpencil.competishun.ui.adapter.scoreboard.DateSelectAdapter
import xyz.penpencil.competishun.ui.adapter.scoreboard.LeaderBoardAdapter
import xyz.penpencil.competishun.utils.HelperFunctions

class ScoreboardViewSolutionAnswer : Fragment() {

    private lateinit var binding : FragmentScoreboardViewSolutionAnswerBinding

    private var helperFunctions  = HelperFunctions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScoreboardViewSolutionAnswerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateList = listOf(
            DateItem(1,R.drawable.green_bg_select),
            DateItem(2,R.drawable.red_bg_select),
            DateItem(3,R.drawable.red_bg_select),
            DateItem(4,R.drawable.green_bg_select),
            DateItem(5,R.drawable.green_bg_select),

        )
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateSelector.layoutManager = layoutManager

        val adapter = DateSelectAdapter(dateList)
        binding.rvDateSelector.adapter = adapter

        binding.questionHeading.setOnClickListener {
            helperFunctions.showCustomSnackBar(binding.root)
        }



    }

}