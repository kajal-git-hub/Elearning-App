package xyz.penpencil.competishun.ui.adapter.score

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TestItem
import android.os.Bundle
import androidx.navigation.findNavController
import xyz.penpencil.competishun.R


class ScoreboardAdapter(private var testTypeList: List<TestItem>):
    RecyclerView.Adapter<ScoreboardAdapter.ScoreboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_academic, parent, false)
        return ScoreboardViewHolder(view)
    }

    override fun getItemCount(): Int = testTypeList.size

    override fun onBindViewHolder(holder: ScoreboardViewHolder, position: Int) {
        val item = testTypeList[position]
//        holder.bind(item)
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("testId", 0)
                putString("testName", "")
            }
//            it.findNavController().navigate(R.id.action_academicTestFragment_to_testDetailFragment, bundle)
        }
    }

    inner class ScoreboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /* private val titleText: TextView = itemView.findViewById(R.id.title)
         private val type: ImageView = itemView.findViewById(R.id.type)

         fun bind(item: TestItem) {
             titleText.text = item.title
             type.visibility = if (item.isFilter) View.VISIBLE else View.GONE
         }*/
    }
}