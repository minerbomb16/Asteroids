package com.asteroids.buttons

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.ImageButton
import com.asteroids.R

open class PlayButtons {
    private lateinit var moveButton: ImageButton
    private lateinit var rotateLeftButton: ImageButton
    private lateinit var rotateRightButton: ImageButton
    private lateinit var shootButton: ImageButton
    private lateinit var pauseButton: ImageButton

    private lateinit var continueButton: ImageButton
    private lateinit var retryButton: ImageButton
    private lateinit var menuButton: ImageButton

    private lateinit var playAgainButton: ImageButton
    private lateinit var menu2Button: ImageButton

    var rotatingLeft = false
    var rotatingRight = false
    var move = false
    var shoot = false
    var pause = false
    var continueGame = false
    var retry = false
    var goMenu = false
    var playAgain = false
    var goMenu2 = false

    open fun initializeButtons(activity: Activity) {
        rotateLeftButton = activity.findViewById(R.id.rotateLeftButton)
        rotateRightButton = activity.findViewById(R.id.rotateRightButton)
        moveButton = activity.findViewById(R.id.moveButton)
        shootButton = activity.findViewById(R.id.shotButton)
        pauseButton = activity.findViewById(R.id.pauseButton)

        continueButton = activity.findViewById(R.id.continueButton)
        retryButton = activity.findViewById(R.id.retryButton)
        menuButton = activity.findViewById(R.id.menuButton)

        playAgainButton = activity.findViewById(R.id.playAgainButton)
        menu2Button = activity.findViewById(R.id.menu2Button)

        setTouchListener(rotateLeftButton) { rotatingLeft = it }
        setTouchListener(rotateRightButton) { rotatingRight = it }
        setTouchListener(moveButton) { move = it }
        setTouchListener(shootButton) { shoot = it }
        setTouchListener(pauseButton) { pause = it }

        setTouchListener(continueButton) { continueGame = it }
        setTouchListener(retryButton) { retry = it }
        setTouchListener(menuButton) { goMenu = it }

        setTouchListener(playAgainButton) { playAgain = it }
        setTouchListener(menu2Button) { goMenu2 = it }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setTouchListener(button: ImageButton, action: (Boolean) -> Unit) {
        button.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    action(true)
                    button.alpha = 0.5f
                    button.isPressed = true
                }
                MotionEvent.ACTION_UP -> {
                    action(false)
                    button.alpha = 1f
                    button.isPressed = false
                    if (event.eventTime - event.downTime < ViewConfiguration.getTapTimeout()) {
                        button.performClick()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    action(false)
                    button.alpha = 1f
                    button.isPressed = false
                }
            }
            true
        }
    }
}