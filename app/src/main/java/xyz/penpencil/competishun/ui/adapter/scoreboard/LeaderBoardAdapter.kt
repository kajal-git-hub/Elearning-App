package xyz.penpencil.competishun.ui.adapter.scoreboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import android.widget.ImageView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.scoreboard.LeaderBoardUser

class LeaderBoardAdapter(private val userList: List<LeaderBoardUser>) :
    RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder>() {

    inner class LeaderBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val etCircleNumber: MaterialTextView = itemView.findViewById(R.id.et_circleNumber)
        val tvUserName: MaterialTextView = itemView.findViewById(R.id.et_studentName)
        val tvScore: MaterialTextView = itemView.findViewById(R.id.et_finalScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_name_score, parent, false)
        return LeaderBoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderBoardViewHolder, position: Int) {
        val user = userList[position]

        holder.etCircleNumber.text = user.number

        holder.tvUserName.text = user.name

        holder.tvScore.text = user.score

    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
