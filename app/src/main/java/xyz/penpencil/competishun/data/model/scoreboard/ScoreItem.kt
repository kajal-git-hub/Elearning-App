package xyz.penpencil.competishun.data.model.scoreboard

import android.widget.ImageView

data class ScoreItem(
    val userAvtar:Int,
    val userStatus : String,
    val testType : String,
    val score: String,
    val correctAnswers: String,
    val incorrectAnswers: String,
    val skippedAnswers: String,
    val timeTaken: String
)

