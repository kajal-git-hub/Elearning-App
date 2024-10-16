package xyz.penpencil.competishun.ui.adapter.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TestItem
import xyz.penpencil.competishun.R

class TestListAdapter(private var testTypeList: List<TestItem>):
    RecyclerView.Adapter<TestListAdapter.TestListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_list, parent, false)
        return TestListViewHolder(view)
    }

    override fun getItemCount(): Int = testTypeList.size

    override fun onBindViewHolder(holder: TestListViewHolder, position: Int) {
        val item = testTypeList[position]
//        holder.bind(item)
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("testId", 0)
                putString("testName", "")
            }
            it.findNavController().navigate(R.id.action_testDashboardFragment_to_academicTestFragment, bundle)

        }
    }

    inner class TestListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       /* private val titleText: TextView = itemView.findViewById(R.id.title)
        private val type: ImageView = itemView.findViewById(R.id.type)

        fun bind(item: TestItem) {
            titleText.text = item.title
            type.visibility = if (item.isFilter) View.VISIBLE else View.GONE
        }*/
    }
}