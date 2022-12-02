package com.my.example.cosmoarcanoid.models

import android.graphics.RectF

class ArcBrick(
    private val row: Int,
    private val column: Int,
    private val width: Int,
    private val height: Int
) {

    private lateinit var rectangle: RectF
    private var isVisible: Boolean = false

    init {
        isVisible = true
        val padding = 1
        rectangle = RectF(
            (column * width + padding).toFloat(),
            (row * height + padding).toFloat(),
            (column * width + width - padding).toFloat(),
            (row * height + height - padding).toFloat()
        )
    }

    fun getRectangle(): RectF {
        return this.rectangle
    }

    fun setInvisible() {
        isVisible = false
    }

    fun getVisibility(): Boolean {
        return isVisible
    }
}