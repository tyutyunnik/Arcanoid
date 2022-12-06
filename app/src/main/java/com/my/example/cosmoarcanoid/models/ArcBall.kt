package com.my.example.cosmoarcanoid.models

import android.graphics.RectF
import kotlin.random.Random

class ArcBall {
    private val rectangle: RectF = RectF()
    private var xVelocity: Float = 200f
    private var yVelocity: Float = -400f
    private val arcBallWidth = 20
    private val arcBallHeight = 20

    fun getRectangle() : RectF{
        return rectangle
    }

    fun update(fps : Long){
        rectangle.left += (xVelocity/fps)
        rectangle.top += (yVelocity/fps)
        rectangle.right = rectangle.left + arcBallWidth
        rectangle.bottom = rectangle.top - arcBallHeight
    }

    fun reverseYVelocity(){
        yVelocity = -yVelocity
    }

    fun reverseXVelocity(){
        xVelocity = -xVelocity
    }

    fun setRandomXVelocity(){
        val randomGen = Random(System.nanoTime())
        val answer : Int = randomGen.nextInt(2)
        if (answer == 0){
            reverseXVelocity()
        }
    }

    fun clearObstacleY(y : Float){
        rectangle.bottom = y
        rectangle.top = y-arcBallHeight
    }

    fun clearObstacleX(x : Float){
        rectangle.left = x
        rectangle.right = x+arcBallWidth
    }

    fun reset(x: Int, y: Int) {
        rectangle.left = x / 2f
        rectangle.top = y - 20f
        rectangle.right = x / 2f + arcBallWidth
        rectangle.bottom = y - 20f - arcBallHeight
        xVelocity = 200f
        yVelocity = -400f
    }
}