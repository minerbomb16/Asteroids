package com.asteroids.objects

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.cos
import kotlin.math.sin
import com.asteroids.GameState.*
import com.asteroids.MainActivity
import com.asteroids.R

class Bullet(
    private val context: Context,
    private val ship: Ship
) {
    lateinit var bulletImage: ImageView
    private lateinit var handler: Handler

    private var screenWidth = 0
    private var screenHeight = 0

    private var directionX = 0.0f
    private var directionY = 0.0f
    var distance = 60

    companion object {
        private const val BULLET_SPEED = 20.0f
        const val DELAY: Long = 16
    }

    fun initialize(rotationAngle: Float) {
        screenWidth = context.resources.displayMetrics.widthPixels
        screenHeight = context.resources.displayMetrics.heightPixels

        bulletImage = ImageView(context)

        val scale = context.resources.displayMetrics.density
        bulletImage.setImageResource(R.drawable.bullet3x5)
        bulletImage.layoutParams = ConstraintLayout.LayoutParams((3f * scale).toInt(), (5f * scale).toInt())
        bulletImage.translationZ = -1f

        bulletImage.x = ship.playerShip.x - 4 + (ship.playerShip.width / 2) * (1 + sin(Math.toRadians(rotationAngle.toDouble())).toFloat())
        bulletImage.y = ship.playerShip.y - 6 + (ship.playerShip.height / 2) * (1 - cos(Math.toRadians(rotationAngle.toDouble())).toFloat())
        bulletImage.rotation = ship.playerShip.rotation
        directionX = sin(Math.toRadians(rotationAngle.toDouble())).toFloat()
        directionY = -cos(Math.toRadians(rotationAngle.toDouble())).toFloat()
        (context as? MainActivity)?.addBullet(this)
    }

    fun startMovement(handler: Handler) {
        this.handler = handler
        handler.post(object : Runnable {
            override fun run() {
                if ((context as? MainActivity)?.getGameState() == PLAYING) {
                    move()
                }
                if (distance <= 0) {
                    stopMovement()
                } else {
                    if (bulletImage.x > screenWidth + 120) {
                        bulletImage.x = 0.0f
                    } else if (bulletImage.x < 0.0f) {
                        bulletImage.x = screenWidth.toFloat() + 120
                    }
                    if (bulletImage.y > screenHeight) {
                        bulletImage.y = 0.0f
                    } else if (bulletImage.y < 0.0f) {
                        bulletImage.y = screenHeight.toFloat()
                    }
                    handler.postDelayed(this, DELAY)
                }
            }
        })
        (context as Activity).findViewById<ConstraintLayout>(R.id.constraintLayout)?.addView(bulletImage)
    }

    private fun move() {
        if (directionX != 0.0f || directionY != 0.0f) {
            bulletImage.x += directionX * BULLET_SPEED
            bulletImage.y += directionY * BULLET_SPEED
            distance--
        }
    }

    fun stopMovement() {
        (context as? MainActivity)?.removeBullet(this)
        handler.removeCallbacksAndMessages(this)
    }
}