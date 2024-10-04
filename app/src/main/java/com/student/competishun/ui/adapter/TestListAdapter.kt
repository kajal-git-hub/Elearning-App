package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TestItem

class TestListAdapter(private var testTypeList: List<TestItem>):
    RecyclerView.Adapter<TestListAdapter.PolicyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestListAdapter.PolicyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_list, parent, false)
        return PolicyViewHolder(view)
    }

    override fun getItemCount(): Int = testTypeList.size

    override fun onBindViewHolder(holder: TestListAdapter.PolicyViewHolder, position: Int) {
        val policyItem = testTypeList[position]
//        holder.bind(policyItem)
    }

    inner class PolicyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       /* private val titleText: TextView = itemView.findViewById(R.id.title)
        private val type: ImageView = itemView.findViewById(R.id.type)

        fun bind(item: TestItem) {
            titleText.text = item.title
            type.visibility = if (item.isFilter) View.VISIBLE else View.GONE
        }*/
    }
}