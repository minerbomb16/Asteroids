package com.asteroids.buttons

import android.content.Context
import com.asteroids.GameState
import com.asteroids.MainActivity
import com.asteroids.R
import com.asteroids.objects.Ship

class ButtonsController (
    private val context: Context,
    private val buttons: PlayButtons,
    private val ship: Ship
) {
    fun check() {
        if ((context as? MainActivity)?.getGameState() == GameState.PAUSED) {
            if (buttons.continueGame) {
                ship.resumeShip()
            }
        } else if (((context as? MainActivity)?.getGameState() != GameState.GAMEOVER)) {
            if (buttons.pause) {
                ship.pauseShip()
            }
            if (buttons.rotatingLeft) {
                ship.rotateShip(-Ship.ANGLE)
            }
            if (buttons.rotatingRight) {
                ship.rotateShip(Ship.ANGLE)
            }
            if (buttons.move) {
                ship.playerShip.setImageResource(R.drawable.shipfire30x30)
            } else {
                ship.playerShip.setImageResource(R.drawable.ship30x30)
            }
            if (buttons.shoot) {
                ship.shoot()
            }
            ship.updateSpeed()
            ship.movePlayerShip()
        }
    }
}