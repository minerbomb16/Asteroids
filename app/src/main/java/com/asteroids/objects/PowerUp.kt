package com.asteroids.objects

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.asteroids.GameState
import com.asteroids.MainActivity
import com.asteroids.R
import kotlin.math.cos
import kotlin.math.sin

class PowerUp (
    private var context: Context
) {
    lateinit var powerUpImage: ImageView
    lateinit var powerUpType: String

    val handler = Handler(Looper.getMainLooper())
    var distance = 300

    fun initialize() {
        (context as? MainActivity)?.runOnUiThread {
            createPowerUp()
        }
    }

    private fun createPowerUp() {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val screenHeight = context.resources.displayMetrics.heightPixels

        powerUpImage = ImageView(context)

        val random = java.util.Random()
        powerUpType = when (random.nextInt(2)) {
            0 -> "tripleShot"
            else -> "asteroidSlow"
        }

        if (powerUpType == "tripleShot") {
            powerUpImage.setImageResource(R.drawable.poweruptripleshot30x30)
        } else {
            powerUpImage.setImageResource(R.drawable.powerupasteroidslow30x30)
        }

        val scale = context.resources.displayMetrics.density
        val imgDp = (30f * scale).toInt()

        powerUpImage.layoutParams = ConstraintLayout.LayoutParams(imgDp, imgDp)

        val (startX, startY) = when (random.nextInt(2)) {
            0 -> Pair(screenWidth + 150, screenHeight / 2)
            else -> Pair(screenWidth / 2, screenHeight + 120)
        }

        powerUpImage.x = startX.toFloat()
        powerUpImage.y = startY.toFloat()

        powerUpImage.translationZ = -1f

        (context as? MainActivity)?.addPowerUp(this)

        val directionX = sin(Math.toRadians((0..360).random().toDouble())).toFloat()
        val directionY = -cos(Math.toRadians((0..360).random().toDouble())).toFloat()
        val speed = 5

        handler.post(object : Runnable {
            override fun run() {
                if ((context as? MainActivity)?.getGameState() == GameState.PLAYING) {
                    powerUpImage.x += directionX * speed
                    powerUpImage.y += directionY * speed
                    distance--
                }
                if (distance <= 0) {
                    stopMovement()
                } else {
                    if (powerUpImage.x < -powerUpImage.width) {
                        powerUpImage.x = screenWidth.toFloat() + 120
                    } else if (powerUpImage.x > screenWidth + 120) {
                        powerUpImage.x = -powerUpImage.width.toFloat()
                    }

                    if (powerUpImage.y < -powerUpImage.height) {
                        powerUpImage.y = screenHeight.toFloat()
                    } else if (powerUpImage.y > screenHeight) {
                        powerUpImage.y = -powerUpImage.height.toFloat()
                    }
                    handler.postDelayed(this, Ship.DELAY)
                }
            }
        })
        (context as Activity).findViewById<ConstraintLayout>(R.id.constraintLayout)?.addView(powerUpImage)
    }

    private fun stopMovement() {
        (context as? MainActivity)?.removePowerUp(this)
        handler.removeCallbacksAndMessages(this)
    }
}