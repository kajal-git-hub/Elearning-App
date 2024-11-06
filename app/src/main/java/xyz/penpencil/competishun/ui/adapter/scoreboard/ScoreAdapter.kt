package xyz.penpencil.competishun.ui.adapter.scoreboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.scoreboard.ScoreItem

class ScoreAdapter(private val scoreList: List<ScoreItem>) :
    RecyclerView.Adapter<ScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_name_score, parent, false)
        return ScoreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val currentItem = scoreList[position]

        holder.imageUser.setImageResource(currentItem.userAvtar)
        holder.tvYouLabel.text = currentItem.userStatus
        holder.tvYouSubLabel.text  = currentItem.testType
        holder.tvScore.text = currentItem.score
        holder.tvCorrectAns.text = currentItem.correctAnswers
        holder.tvIncorrectAns.text = currentItem.incorrectAnswers
        holder.tvSkippedAns.text = currentItem.skippedAnswers
        holder.tvTimeTakenAns.text = currentItem.timeTaken
    }

    override fun getItemCount(): Int {
        return scoreList.size
    }
}

class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageUser: ImageView = itemView.findViewById(R.id.imageUser)
    val tvYouLabel: TextView = itemView.findViewById(R.id.tvYouLabel)
    val tvYouSubLabel: TextView = itemView.findViewById(R.id.tvYouSubLabel)
    val tvScore: TextView = itemView.findViewById(R.id.tvScore)
    val tvCorrectAns: TextView = itemView.findViewById(R.id.tvCorrectAns)
    val tvIncorrectAns: TextView = itemView.findViewById(R.id.tvIncorrectAns)
    val tvSkippedAns: TextView = itemView.findViewById(R.id.tvSkippedAns)
    val tvTimeTakenAns: TextView = itemView.findViewById(R.id.tvTimeTakenAns)
}

