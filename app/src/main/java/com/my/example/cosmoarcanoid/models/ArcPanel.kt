package com.my.example.cosmoarcanoid.models

import android.graphics.RectF

class ArcPanel(private val screenX: Int, private val screenY: Int) {

    private var rectangle: RectF
    private val length: Float = screenX / 6f
    private val height: Float = 30f
    private var x: Float = screenX / 2f
    private var y: Float = screenY - 30f
    private val panelSpeed: Float
    private var panelMoving: Int = STOPPED

    init {
        rectangle = RectF(x, y, x + length, y + height)
        panelSpeed = 600f
    }

    companion object {
        const val STOPPED = 0
        const val LEFT = 1
        const val RIGHT = 2
    }

    fun getRectangle(): RectF {
        return rectangle
    }

    fun setMovementState(state: Int) {
        panelMoving = state
    }

    fun update(fps: Long) {
        if (panelMoving == LEFT) {
            x -= panelSpeed / fps
            if (x <= 0f) {
                x = 0f
            }
        } else if (panelMoving == RIGHT) {
            x += panelSpeed / fps
            if (x >= screenX - length) {
                x = screenX.toFloat() - length
            }
        }
        rectangle.left = x
        rectangle.right = x + length
    }
}