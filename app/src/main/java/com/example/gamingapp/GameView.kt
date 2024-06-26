package com.example.gamingapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class GameView(context: Context, private val gameTask: GameTask) : View(context) {

    private val myPaint: Paint = Paint()
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myCarposition = 1
    private val otherCars = ArrayList<HashMap<String, Any>>()

    private lateinit var redCarDrawable: Drawable
    private lateinit var whiteCarDrawable: Drawable

    private var viewWidth = 0
    private var viewHeight = 0

    init {
        // Initialize paint for drawing texts
        myPaint.color = Color.WHITE
        myPaint.textSize = 40f

        // Load drawable resources for cars
        redCarDrawable = context.resources.getDrawable(R.drawable.red_car, null)
        whiteCarDrawable = context.resources.getDrawable(R.drawable.white_car, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get the width and height of the view
        viewWidth = measuredWidth
        viewHeight = measuredHeight

        // Generate other cars randomly
        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCars.add(map)
        }

        // Update time and calculate car dimensions
        time += 10 + speed
        val carWidth = viewWidth / 5
        val carHeight = carWidth + 10

        // Draw the player's car
        redCarDrawable.setBounds(
            myCarposition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - carHeight,
            myCarposition * viewWidth / 3 + viewWidth / 15 + carWidth - 25,
            viewHeight - 2
        )
        redCarDrawable.draw(canvas!!)

        // Draw other cars and handle collisions and scoring
        val iterator = otherCars.iterator()
        while (iterator.hasNext()) {
            val car = iterator.next()
            val lane = car["lane"] as Int
            val carX = lane * viewWidth / 3 + viewWidth / 15
            var carY = time - car["startTime"] as Int

            whiteCarDrawable.setBounds(
                carX + 25, carY - carHeight, carX + carWidth - 25, carY
            )
            whiteCarDrawable.draw(canvas)

            if (lane == myCarposition) {
                if (carY > viewHeight - 2 - carHeight && carY < viewHeight - 2) {
                    // Close the game if collision occurs
                    gameTask.closeGame(score)
                }
            }

            if (carY > viewHeight + carHeight) {
                // Remove cars that have gone off the screen and update score and speed
                iterator.remove()
                score++
                speed = 1 + score / 8
            }
        }

        // Draw score and speed on the canvas
        canvas.drawText("Score: $score", 250f, 80f, myPaint)
        canvas.drawText("Speed: $speed", 640f, 80f, myPaint)

        // Redraw the view
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Handle touch events for moving the player's car
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myCarposition > 0) {
                        myCarposition--
                    }
                } else {
                    if (myCarposition < 2) {
                        myCarposition++
                    }
                }
                // Redraw the view
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Handle touch up if needed
            }
        }
        return true
    }
}
