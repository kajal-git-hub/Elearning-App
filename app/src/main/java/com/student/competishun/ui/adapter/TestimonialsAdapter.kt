package com.student.competishun.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.Testimonial

class TestimonialsAdapter(private val testimonials: List<Testimonial>) : RecyclerView.Adapter<TestimonialsAdapter.TestimonialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestimonialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_testimonials_card, parent, false)
        return TestimonialViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestimonialViewHolder, position: Int) {
        val testimonial = testimonials[position]
        holder.tvComment.text = testimonial.comment
        holder.tvname.text = testimonial.name
        holder.tvclass.text = testimonial.currentClass
        holder.tvtarget.text = testimonial.target
//
//        holder.tvComment.setOnClickListener {
//
//            val url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
//            goToPlayerPage(holder.itemView.findNavController(), url)
//        }

    }

        private fun goToPlayerPage(navController: NavController, url: String) {
            val bundle = Bundle().apply {
                putString("url", url)
            }
            navController.navigate(R.id.action_homeFragment_to_mediaFragment,bundle)
        }

    override fun getItemCount(): Int {
        return testimonials.size
    }

    class TestimonialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvComment: TextView = itemView.findViewById(R.id.tvComment)
        val tvname:TextView=itemView.findViewById(R.id.userName)
        val tvclass:TextView=itemView.findViewById(R.id.userClass)
        val tvtarget:TextView=itemView.findViewById(R.id.userTarget)
    }
}
