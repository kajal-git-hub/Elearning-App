package xyz.penpencil.competishun.ui.fragment.scoreboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentBarChartBinding

class BarChartFragment : Fragment() {

    private lateinit var binding : FragmentBarChartBinding

    private lateinit var barChart:BarChart




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
        binding = FragmentBarChartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        barChart=  view.findViewById(R.id.bar_chart)

        val list : ArrayList<BarEntry> = ArrayList()

        list.add(BarEntry(100f,100f))
        list.add(BarEntry(101f,102f))
        list.add(BarEntry(103f,107f))
        list.add(BarEntry(104f,109f))

        val barDataSet = BarDataSet(list,"List")

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)
        barDataSet.valueTextColor = Color.BLACK
        val barData = BarData(barDataSet)
        barChart.setFitBars(true)
        barChart.data = barData

        barChart.description.text = "Bar Chart"

        barChart.animateY(2000)



    }

}