package com.asteroids.controllers

import android.app.Activity
import android.content.Context
import com.asteroids.objects.PowerUp

class PowerUpSpawn {
    private lateinit var context: Context
    private lateinit var activity: Activity

    var lastSpawnTimeMillis: Long = 0
    private val spawnDelayMillis: Long = 20000

    fun initialize(activity: Activity, context: Context) {
        this.activity = activity
        this.context = context
        lastSpawnTimeMillis = System.currentTimeMillis()
    }

    fun check() {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastSpawnTimeMillis >= spawnDelayMillis) {
            PowerUp(context).initialize()
            lastSpawnTimeMillis = currentTimeMillis
        }
    }

}