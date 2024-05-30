package com.asteroids.controllers

import android.app.Activity
import android.content.Context
import com.asteroids.objects.Asteroid
import com.asteroids.objects.Ship

class Wave (
    private var ship: Ship,
    private var asteroids: List<Asteroid>
) {
    private var powerUpTime: Long = 10000
    var powerUpStartTime: Long = 0

    private lateinit var context: Context
    private lateinit var activity: Activity

    private var wave = 0
    var slow = 1f

    fun initialize(activity: Activity, context: Context) {
        this.activity = activity
        this.context = context
    }

    fun check() {
        if(System.currentTimeMillis() - powerUpStartTime >= powerUpTime) {
            slow = 1f
        }

        if (asteroids.isEmpty()) {
            wave++

            val waveCalc = wave + 8
            var numOfAst: Int = waveCalc / 3
            while (numOfAst > 0) {
                Asteroid(-1f, -1f, 3, ship, context).initialize()
                numOfAst--
            }
            if (waveCalc % 3 != 0) {
                Asteroid(-1f, -1f, waveCalc%3, ship, context).initialize()
            }
        }
    }

    fun powerUpBegin() {
        powerUpStartTime = System.currentTimeMillis()
        slow = 0.3f
    }
}