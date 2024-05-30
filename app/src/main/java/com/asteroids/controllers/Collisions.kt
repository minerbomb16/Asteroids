package com.asteroids.controllers

import android.content.Context
import com.asteroids.GameState
import com.asteroids.MainActivity
import com.asteroids.Score
import com.asteroids.objects.Asteroid
import com.asteroids.objects.Bullet
import com.asteroids.objects.PowerUp
import com.asteroids.objects.Ship
import kotlin.math.pow
import kotlin.math.sqrt

class Collisions (
    private var context: Context,
    private var ship: Ship,
    private var bullets: List<Bullet>,
    private var asteroids: List<Asteroid>,
    private var powerUps: List<PowerUp>,
    private var wave: Wave,
    private var score: Score
) {

    fun check() {
        for (asteroid in asteroids) {
            val astX = asteroid.asteroidImage.x + asteroid.asteroidImage.width / 2
            val astY = asteroid.asteroidImage.y + asteroid.asteroidImage.height / 2

            val shipX = ship.playerShip.x + ship.playerShip.width / 2
            val shipY = ship.playerShip.y + ship.playerShip.height / 2

            for (bullet in bullets) {
                val bulX = bullet.bulletImage.x + bullet.bulletImage.width / 2
                val bulY = bullet.bulletImage.y + bullet.bulletImage.height / 2

                if (sqrt((astX - bulX).pow(2) + (astY - bulY).pow(2)) < asteroid.asteroidImage.width / 2) {
                    bullet.distance = 0
                    score.destroyAsteroid(asteroid.getSize())
                    asteroid.delete = true
                    break
                }
            }

            for (powerUp in powerUps) {
                val powerX = powerUp.powerUpImage.x + powerUp.powerUpImage.height / 2
                val powerY = powerUp.powerUpImage.y + powerUp.powerUpImage.width / 2

                if (sqrt((powerX - shipX).pow(2) + (powerY - shipY).pow(2)) < (powerUp.powerUpImage.width + ship.playerShip.width) / 2 - 10) {
                    if (powerUp.powerUpType == "tripleShot") {
                        ship.powerUpBegin()
                    } else if (powerUp.powerUpType == "asteroidSlow") {
                        wave.powerUpBegin()
                    }
                    powerUp.distance = 0
                }
            }

            if (sqrt((astX - shipX).pow(2) + (astY - shipY).pow(2)) < (asteroid.asteroidImage.width + ship.playerShip.width) / 2 - 10 && (context as? MainActivity)?.getGameState() != GameState.GAMEOVER) {
                ship.gameOver()
            }
        }
    }
}