package com.student.competishun.ui.adapter.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TestItem
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton


class AcademicTestResumeAdapter(private var testTypeList: List<TestItem>):
    RecyclerView.Adapter<AcademicTestResumeAdapter.AcademicViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcademicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_resume, parent, false)
        return AcademicViewHolder(view)
    }


    override fun getItemCount(): Int = testTypeList.size

    override fun onBindViewHolder(holder: AcademicViewHolder, position: Int) {
        val item = testTypeList[position]
        holder.bind(item)
    }

    inner class AcademicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         private val continueTest: MaterialButton = itemView.findViewById(R.id.continueTest)
         fun bind(item: TestItem) {
             continueTest.setOnClickListener {
                 val bundle = Bundle().apply {
                     putInt("testId", 0)
                     putString("testName", "")
                 }
                 it.findNavController().navigate(R.id.action_academicTestFragment_to_testFragment, bundle)
             }
         }
    }
}