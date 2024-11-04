package xyz.penpencil.competishun.ui.fragment.scoreboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentScoreDashboardBinding
import xyz.penpencil.competishun.databinding.FragmentTestDashboardBinding


class ScoreDashboardFragment : Fragment() {

    private var _binding: FragmentScoreDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}