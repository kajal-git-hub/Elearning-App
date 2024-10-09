package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TestItem

class TestTypeAdapter(private var testTypeList: List<TestItem>, private var listener: ItemClickListener):
    RecyclerView.Adapter<TestTypeAdapter.TestTypeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestTypeAdapter.TestTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_type, parent, false)
        return TestTypeViewHolder(view)
    }

    override fun getItemCount(): Int = testTypeList.size

    override fun onBindViewHolder(holder: TestTypeAdapter.TestTypeViewHolder, position: Int) {
        val item = testTypeList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            listener.onItemClick(isFirst = (position == 0), item = item)
        }
    }
    inner class TestTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.title)

        fun bind(item: TestItem) {
            titleText.text = item.title
        }
    }
}

interface ItemClickListener{
    fun onItemClick(isFirst:Boolean, item: TestItem)
}