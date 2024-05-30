package com.asteroids

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat

class Score (
    private var context: Context
) {
    private lateinit var activity: Activity
    private lateinit var scoreTextView: TextView

    private var score = 0

    @SuppressLint("SetTextI18n")
    fun initialize(activity: Activity) {
        this.activity = activity

        scoreTextView = TextView(context).apply {
            text = "SCORE:0"
            textSize = 20f
            setTextColor(Color.WHITE)
            setPadding(20, 20, 0, 0)
            typeface = ResourcesCompat.getFont(context, R.font.joystix_monospace)
        }
        val scoreLayoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        }
        (context as Activity).findViewById<ConstraintLayout>(R.id.constraintLayout)
            ?.addView(scoreTextView, scoreLayoutParams)
    }

    @SuppressLint("SetTextI18n")
    private fun updateScore(points: Int) {
        score += points
        scoreTextView.text = "SCORE:$score"
    }

    fun destroyAsteroid(size: Int) {
        when (size) {
            3 -> updateScore(200)
            2 -> updateScore(100)
            1 -> updateScore(50)
        }
    }
}