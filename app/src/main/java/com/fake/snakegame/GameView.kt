package com.fake.snakegame

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val thread = GameThread(holder, this)
    private var snake = mutableListOf(Point(5, 5))
    private var direction = "RIGHT"
    private var food = Point(10, 10)
    private var score = 0
    private var cellSize = 60
    private var running = true

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Nothing needed here for now
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread.running = true
        thread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.running = false
    }

    fun update() {
        val head = snake.first().let { Point(it.x, it.y) }
        when (direction) {
            "UP" -> head.y--
            "DOWN" -> head.y++
            "LEFT" -> head.x--
            "RIGHT" -> head.x++
        }

        if (head == food) {
            snake.add(0, head)
            score += 10
            spawnFood()
        } else {
            snake.add(0, head)
            snake.removeAt(snake.size - 1)
        }

        if (snake.drop(1).contains(head) || head.x < 0 || head.y < 0 || head.x > width / cellSize || head.y > height / cellSize) {
            running = false
            saveScore()
        }
    }

    private fun spawnFood() {
        food = Point(Random.nextInt(width / cellSize), Random.nextInt(height / cellSize))
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (canvas == null) return

        val paint = Paint()
        paint.color = Color.GREEN
        for (p in snake) {
            canvas.drawRect(
                (p.x * cellSize).toFloat(),
                (p.y * cellSize).toFloat(),
                ((p.x + 1) * cellSize).toFloat(),
                ((p.y + 1) * cellSize).toFloat(),
                paint
            )
        }

        paint.color = Color.RED
        canvas.drawRect(
            (food.x * cellSize).toFloat(),
            (food.y * cellSize).toFloat(),
            ((food.x + 1) * cellSize).toFloat(),
            ((food.y + 1) * cellSize).toFloat(),
            paint
        )

        paint.color = Color.WHITE
        paint.textSize = 60f
        canvas.drawText("Score: $score", 50f, 100f, paint)

        if (!running) {
            paint.textSize = 100f
            canvas.drawText("Game Over!", width / 4f, height / 2f, paint)
        }
    }

    private fun saveScore() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user.uid).update("score", score)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                direction = when {
                    x < width / 3 -> "LEFT"
                    x > width * 2 / 3 -> "RIGHT"
                    y < height / 3 -> "UP"
                    else -> "DOWN"
                }
            }
        }
        return true
    }
}