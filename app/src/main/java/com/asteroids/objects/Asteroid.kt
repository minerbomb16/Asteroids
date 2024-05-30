package com.asteroids.objects

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import com.asteroids.GameState.*
import com.asteroids.MainActivity
import com.asteroids.R

class Asteroid (
    private var startX: Float,
    private var startY: Float,
    private var size: Int,
    private var ship: Ship,
    private var context: Context,
){
    lateinit var asteroidImage: ImageView

    var slow: Float = 1f

    val handler = Handler(Looper.getMainLooper())
    var delete = false

    fun initialize() {
        (context as? MainActivity)?.runOnUiThread {
            createAsteroid()
        }
    }

    fun getSize(): Int {
        return size
    }

    private fun createAsteroid() {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val screenHeight = context.resources.displayMetrics.heightPixels

        asteroidImage = ImageView(context)

        val (drawable, imgSize) = when (size) {
            3 -> getRandomAsteroid(
                listOf(
                    Pair(R.drawable.asteroid50x50w1, 50f) to 500,
                    Pair(R.drawable.asteroid50x50w2, 50f) to 500,
                    Pair(R.drawable.asteroid50x50wd, 50f) to 1
                )
            )
            2 -> getRandomAsteroid(
                listOf(
                    Pair(R.drawable.asteroid35x35w1, 35f) to 500,
                    Pair(R.drawable.asteroid35x35w2, 35f) to 500,
                    Pair(R.drawable.asteroid35x35we, 35f) to 1
                )
            )
            else -> getRandomAsteroid(
                listOf(
                    Pair(R.drawable.asteroid20x20w1, 20f) to 250,
                    Pair(R.drawable.asteroid20x20w2, 20f) to 250,
                    Pair(R.drawable.asteroid20x20w3, 20f) to 250,
                    Pair(R.drawable.asteroid20x20w4, 20f) to 250,
                    Pair(R.drawable.asteroid20x20we, 20f) to 1
                )
            )
        }

        asteroidImage.setImageResource(drawable)

        val scale = context.resources.displayMetrics.density
        val imgDp = (imgSize * scale).toInt()

        asteroidImage.layoutParams = ConstraintLayout.LayoutParams(imgDp, imgDp)

        if (startX < 0 && startY < 0) {
            asteroidImage.x = ship.playerShip.x
            asteroidImage.y = ship.playerShip.y

            while (sqrt((ship.playerShip.x - asteroidImage.x).pow(2) + (ship.playerShip.y - asteroidImage.y).pow(2)) < 400) {
                asteroidImage.x = (100..screenWidth-100).random().toFloat()
                asteroidImage.y = (100..screenHeight-100).random().toFloat()
            }
        } else {
            asteroidImage.x = startX
            asteroidImage.y = startY
        }

        asteroidImage.translationZ = -1f

        (context as? MainActivity)?.addAsteroid(this)

        val directionX = sin(Math.toRadians((0..360).random().toDouble())).toFloat()
        val directionY = -cos(Math.toRadians((0..360).random().toDouble())).toFloat()
        val speed = (3..5).random()

        val random = java.util.Random()
        val rotationSpeed = when (random.nextInt(2)) {
            0 -> (1..3).random().toFloat()
            1 -> (-3..-1).random().toFloat()
            else -> 0.0f
        }

        handler.post(object : Runnable {
            override fun run() {
                slow = (context as? MainActivity)?.getAsteroidSlow() ?: 1f
                if ((context as? MainActivity)?.getGameState() == PLAYING) {
                    asteroidImage.rotation = asteroidImage.rotation + rotationSpeed * slow
                    asteroidImage.x += directionX * speed * slow
                    asteroidImage.y += directionY * speed * slow
                }
                if (delete) {
                    if (size > 1) {
                        val newAsteroid1 = Asteroid(asteroidImage.x, asteroidImage.y, size - 1, ship, context)
                        val newAsteroid2 = Asteroid(asteroidImage.x, asteroidImage.y, size - 1, ship, context)
                        newAsteroid1.initialize()
                        newAsteroid2.initialize()
                    }
                    stopMovement()
                } else {
                    if (asteroidImage.x < -asteroidImage.width) {
                        asteroidImage.x = screenWidth.toFloat() + 120
                    } else if (asteroidImage.x > screenWidth + 120) {
                        asteroidImage.x = -asteroidImage.width.toFloat()
                    }

                    if (asteroidImage.y < -asteroidImage.height) {
                        asteroidImage.y = screenHeight.toFloat()
                    } else if (asteroidImage.y > screenHeight) {
                        asteroidImage.y = -asteroidImage.height.toFloat()
                    }
                    handler.postDelayed(this, Ship.DELAY)
                }
            }
        })
        (context as Activity).findViewById<ConstraintLayout>(R.id.constraintLayout)?.addView(asteroidImage)
    }

    private fun getRandomAsteroid(drawablesWithWeights: List<Pair<Pair<Int, Float>, Int>>): Pair<Int, Float> {
        val totalWeight = drawablesWithWeights.sumOf { it.second }
        val randomValue = (1..totalWeight).random()
        var cumulativeWeight = 0

        for ((drawableWithSize, weight) in drawablesWithWeights) {
            cumulativeWeight += weight
            if (randomValue <= cumulativeWeight) {
                return drawableWithSize
            }
        }
        return drawablesWithWeights.first().first
    }

    private fun stopMovement() {
        (context as? MainActivity)?.removeAsteroid(this)
        handler.removeCallbacksAndMessages(null)
    }

    fun removeHandler() {
        handler.removeCallbacksAndMessages(null)
    }
}