package com.asteroids.buttons

import android.app.Activity
import android.widget.ImageButton
import com.asteroids.R

class MenuButtons : PlayButtons() {
    private lateinit var playButton: ImageButton

    var play = false

    override fun initializeButtons(activity: Activity) {
        playButton = activity.findViewById(R.id.playButton)

        setTouchListener(playButton) { play = it }
    }
}