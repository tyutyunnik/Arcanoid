package com.mstete.wnn.csmt.models

import android.graphics.RectF

class ArcBrick(
    row: Int,
    column: Int,
    width: Int,
    height: Int
) {

    private var rectangle: RectF
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