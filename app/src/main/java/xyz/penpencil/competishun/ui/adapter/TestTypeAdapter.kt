package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TestItem
import xyz.penpencil.competishun.R

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
        private val itemFilter: RelativeLayout = itemView.findViewById(R.id.itemFilter)

        fun bind(item: TestItem) {
            titleText.text = item.title
            if (item.isFilter){
                itemFilter.background = ContextCompat.getDrawable(itemView.context, R.drawable.rounded_filter_blue_bg)
                titleText.setTextColor(ContextCompat.getColor(itemView.context, R.color.PrimaryColor))
            }else{
                itemFilter.background = ContextCompat.getDrawable(itemView.context, R.drawable.card_bg_r20)
                titleText.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }
}

interface ItemClickListener{
    fun onItemClick(isFirst:Boolean, item: TestItem)
}