package com.asteroids

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.asteroids.GameState.*
import com.asteroids.buttons.PlayButtons
import com.asteroids.buttons.ButtonsController
import com.asteroids.buttons.MenuButtons
import com.asteroids.controllers.Collisions
import com.asteroids.controllers.PowerUpSpawn
import com.asteroids.controllers.Wave
import com.asteroids.objects.Asteroid
import com.asteroids.objects.Bullet
import com.asteroids.objects.PowerUp
import com.asteroids.objects.Ship


class MainActivity : AppCompatActivity(), SensorEventListener {

    private val handler = Handler(Looper.getMainLooper())
    private var gameState: GameState = MENU

    private lateinit var ship: Ship
    private val bullets = mutableListOf<Bullet>()
    private val asteroids = mutableListOf<Asteroid>()
    private val powerUps = mutableListOf<PowerUp>()

    private lateinit var waveController: Wave
    private lateinit var collisions: Collisions
    private lateinit var powerUpSpawn: PowerUpSpawn
    private lateinit var score: Score

    private lateinit var buttons: PlayButtons
    private lateinit var menubuttons: MenuButtons
    private lateinit var buttonsControler: ButtonsController

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private lateinit var pauseOverlay: View
    private lateinit var pauseMenu: LinearLayout
    private lateinit var gameOverMenu: LinearLayout

    companion object {
        private val accelerometerReading = FloatArray(3)
        private val magnetometerReading = FloatArray(3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)

        onMenuClick()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        handler.post(gameLoop)
    }

    fun onPlayButtonClicked() {
        gameState = PLAYING
        setContentView(R.layout.activity_main)

        pauseOverlay = findViewById(R.id.pauseOverlay)
        pauseMenu = findViewById(R.id.pauseMenu)
        gameOverMenu = findViewById(R.id.gameOverMenu)

        buttons = PlayButtons()
        buttons.initializeButtons(this)

        ship = Ship(buttons, pauseOverlay, pauseMenu, gameOverMenu, this)
        ship.initialize(this)

        score = Score(this)
        score.initialize(this)

        buttonsControler = ButtonsController(this, buttons, ship)

        waveController = Wave(ship, asteroids)
        waveController.initialize(this, this)

        collisions = Collisions(this, ship, bullets, asteroids, powerUps, waveController, score)

        powerUpSpawn = PowerUpSpawn()
        powerUpSpawn.initialize(this, this)
    }

    fun onMenuClick() {
        gameState = MENU
        setContentView(R.layout.menu_layout)

        menubuttons = MenuButtons()
        menubuttons.initializeButtons(this)
    }

    private val gameLoop = object : Runnable {
        override fun run() {
            if (gameState != MENU) {
                    buttonsControler.check()
                    waveController.check()
                    powerUpSpawn.check()
                    collisions.check()
                if (buttons.retry || buttons.playAgain) {
                    clearLists()
                    onPlayButtonClicked()
                }
                if (buttons.goMenu || buttons.goMenu2) {
                    clearLists()
                    gameState = MENU
                    onMenuClick()
                }
                if (gameState == PAUSED || gameState == GAMEOVER) {
                    val currentTime = System.currentTimeMillis()
                    ship.lastShootTimeMillis += currentTime  - ship.lastShootTimeMillis
                    powerUpSpawn.lastSpawnTimeMillis += currentTime - powerUpSpawn.lastSpawnTimeMillis
                    ship.powerUpStartTime += currentTime - ship.powerUpStartTime
                    waveController.powerUpStartTime += currentTime - waveController.powerUpStartTime
                }
            } else {
                if (menubuttons.play) {
                    gameState = PLAYING
                    onPlayButtonClicked()
                }
            }
            handler.postDelayed(this, Ship.DELAY)
        }
    }

    fun getAsteroidSlow(): Float {
        return waveController.slow
    }

    fun addBullet(bullet: Bullet) {
        bullets.add(bullet)
    }

    fun removeBullet(bullet: Bullet) {
        bullet.bulletImage.visibility = View.GONE
        bullets.remove(bullet)
        findViewById<ConstraintLayout>(R.id.constraintLayout)?.removeView(bullet.bulletImage)
    }

    fun addAsteroid(asteroid: Asteroid) {
        asteroids.add(asteroid)
    }

    fun removeAsteroid(asteroid: Asteroid) {
        asteroid.asteroidImage.visibility = View.GONE
        asteroids.remove(asteroid)
        findViewById<ConstraintLayout>(R.id.constraintLayout)?.removeView(asteroid.asteroidImage)
    }

    fun addPowerUp(powerUp: PowerUp) {
        powerUps.add(powerUp)
    }

    fun removePowerUp(powerUp: PowerUp) {
        powerUp.powerUpImage.visibility = View.GONE
        powerUps.remove(powerUp)
        findViewById<ConstraintLayout>(R.id.constraintLayout)?.removeView(powerUp.powerUpImage)
    }

    private fun clearLists() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)

        val asteroidIterator = asteroids.iterator()
        while (asteroidIterator.hasNext()) {
            val asteroid = asteroidIterator.next()
            asteroid.asteroidImage.visibility = View.GONE
            asteroid.removeHandler()
            asteroidIterator.remove()
            constraintLayout?.removeView(asteroid.asteroidImage)
        }

        val bulletIterator = bullets.iterator()
        while (bulletIterator.hasNext()) {
            val bullet = bulletIterator.next()
            bullet.distance = 0
        }

        val powerUpIterator = powerUps.iterator()
        while (powerUpIterator.hasNext()) {
            val powerUp = powerUpIterator.next()
            powerUp.distance = 0
        }
    }

    fun setGameState(gameState: GameState) {
        this.gameState = gameState
    }

    fun getGameState(): GameState {
        return gameState
    }

    /*
    private fun makeMenuAsteroids () {
        Asteroid(-1f, -1f, 3, ship, bullets, this).initialize()
        Asteroid(-1f, -1f, 3, ship, bullets, this).initialize()
        Asteroid(-1f, -1f, 2, ship, bullets, this).initialize()
        Asteroid(-1f, -1f, 2, ship, bullets, this).initialize()
        Asteroid(-1f, -1f, 1, ship, bullets, this).initialize()
        Asteroid(-1f, -1f, 1, ship, bullets, this).initialize()
    }
    */

    @Suppress("DEPRECATION")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.apply {
                systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                        )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager?.registerListener(
            this,
            magnetometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
        Log.d("Lifecycle", "Aplikacja została zminimalizowana")
        //ship.ispaused = true
        if(gameState != MENU && gameState != GAMEOVER) {
            gameState = PAUSED
            pauseOverlay.visibility = View.VISIBLE
            pauseMenu.visibility = View.VISIBLE
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        updateOrientationAngles()
        updateScreenOrientation()
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

    private fun updateScreenOrientation() {
        val orientationAngle = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
        if (orientationAngle > 45) {
            requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        } else if (orientationAngle < -45) {
            requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
}
