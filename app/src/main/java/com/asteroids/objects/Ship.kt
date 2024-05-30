package com.asteroids.objects

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import com.asteroids.GameState.*
import com.asteroids.MainActivity
import com.asteroids.R
import com.asteroids.buttons.PlayButtons

class Ship (
    private val buttons: PlayButtons,
    private var pauseOverlay: View,
    private var pauseMenu: LinearLayout,
    private var gameOverMenu: LinearLayout,
    private var context: Context
) {
    lateinit var playerShip: ImageView
    private val scale = context.resources.displayMetrics.density

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    private lateinit var activity: Activity

    private var activePowerUp = false
    private var powerUpTime: Long = 10000
    var powerUpStartTime: Long = 0
    var lastShootTimeMillis: Long = 0
    private val shootDelayMillis: Long = 300

    private var currentSpeedX = 0.0f
    private var currentSpeedY = 0.0f
    private var rotationAngle = 0.0f
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val MAX_SPEED = 18.0f
        private const val ACCELERATION = 0.3f
        private const val SPEED_LOSS = 0.1f
        const val ANGLE = 3.5f
        const val DELAY: Long = 16
    }

    fun initialize(activity: Activity) {
        pauseOverlay.visibility = View.GONE
        pauseMenu.visibility = View.GONE
        gameOverMenu.visibility = View.GONE
        this.activity = activity

        playerShip = ImageView(context)
        playerShip.setImageResource(R.drawable.ship30x30)
        playerShip.x = (screenWidth / 2).toFloat()
        playerShip.y = (screenHeight / 2).toFloat()
        playerShip.layoutParams = ConstraintLayout.LayoutParams((30f * scale).toInt(), (30f * scale).toInt())
        playerShip.translationZ = -1f
        (context as Activity).findViewById<ConstraintLayout>(R.id.constraintLayout)?.addView(playerShip)
    }

    fun updateSpeed() {
        if (buttons.move) {
            currentSpeedX += sin(Math.toRadians(rotationAngle.toDouble())).toFloat() * ACCELERATION
            currentSpeedY -= cos(Math.toRadians(rotationAngle.toDouble())).toFloat() * ACCELERATION

            currentSpeedX = currentSpeedX.coerceIn(-MAX_SPEED, MAX_SPEED)
            currentSpeedY = currentSpeedY.coerceIn(-MAX_SPEED, MAX_SPEED)
        } else {
            val z = sqrt(currentSpeedX.pow(2)+currentSpeedY.pow(2))
            if(currentSpeedX != 0.0f) {
                currentSpeedX -= SPEED_LOSS * currentSpeedX / z
            }
            if(currentSpeedY != 0.0f) {
                currentSpeedY -= SPEED_LOSS * currentSpeedY / z
            }
        }
    }

    fun movePlayerShip() {
        var newX = playerShip.x + currentSpeedX
        var newY = playerShip.y + currentSpeedY

        if (newX < -playerShip.width) {
            newX = screenWidth.toFloat() + 120
        } else if (newX > screenWidth + 120) {
            newX = -playerShip.width.toFloat()
        }

        if (newY < -playerShip.height) {
            newY = screenHeight.toFloat()
        } else if (newY > screenHeight) {
            newY = -playerShip.height.toFloat()
        }

        playerShip.x = newX
        playerShip.y = newY
    }

    fun rotateShip(angle: Float) {
        rotationAngle += angle
        if (abs(rotationAngle) >= 360.0f) {
            rotationAngle = 0.0f
        }
        playerShip.rotation = rotationAngle
    }

    fun shoot() {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastShootTimeMillis >= shootDelayMillis) {
            lastShootTimeMillis = currentTimeMillis
            Handler(Looper.getMainLooper()).post {
                val bullet = Bullet(context, this)
                bullet.initialize(rotationAngle)
                bullet.startMovement(handler)

                if (activePowerUp) {
                    if(currentTimeMillis - powerUpStartTime >= powerUpTime) {
                        activePowerUp = false
                    } else {
                        val bullet1 = Bullet(context, this)
                        bullet1.initialize(rotationAngle + 10f)
                        bullet1.startMovement(handler)
                        val bullet2 = Bullet(context, this)
                        bullet2.initialize(rotationAngle - 10f)
                        bullet2.startMovement(handler)
                    }
                }
            }
        }
    }

    fun powerUpBegin() {
        activePowerUp = true
        powerUpStartTime = System.currentTimeMillis()
    }

    fun resumeShip() {
        (context as? MainActivity)?.setGameState(PLAYING)
        pauseOverlay.visibility = View.GONE
        pauseMenu.visibility = View.GONE
        gameOverMenu.visibility = View.GONE
    }

    fun pauseShip() {
        (context as? MainActivity)?.setGameState(PAUSED)
        pauseOverlay.visibility = View.VISIBLE
        pauseMenu.visibility = View.VISIBLE
    }

    fun gameOver() {
        (context as? MainActivity)?.setGameState(GAMEOVER)

        val explosionFrames = listOf(
            R.drawable.ship30x30a1,
            R.drawable.ship30x30a2,
            R.drawable.ship30x30a3,
            R.drawable.ship30x30a4,
            R.drawable.ship30x30a5,
            R.drawable.ship30x30a6,
            R.drawable.ship30x30a7,
            R.drawable.ship30x30a8,
            R.drawable.ship30x30a9,
            R.drawable.ship30x30a10,
            R.drawable.ship30x30a11,
            R.drawable.ship30x30a12,
            R.drawable.ship30x30a13,
            R.drawable.ship30x30a14,
            R.drawable.ship30x30a15,
            R.drawable.ship30x30a16,
            R.drawable.ship30x30a17
        )

        var frame = 0

        fun animateFrame() {
            if (frame < explosionFrames.size) {
                playerShip.setImageResource(explosionFrames[frame])
                frame++
                handler.postDelayed(::animateFrame, 50)
            } else {
                playerShip.visibility = View.GONE
                finishGameOver()
            }
        }

        animateFrame()
    }

    private fun finishGameOver() {
        pauseOverlay.visibility = View.VISIBLE
        gameOverMenu.visibility = View.VISIBLE
        playerShip.visibility = View.GONE
        handler.removeCallbacksAndMessages(null)
    }
}