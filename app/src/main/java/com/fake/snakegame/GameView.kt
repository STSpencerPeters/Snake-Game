package com.fake.snakegame

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var thread = GameThread(holder, this)
    private var snake = mutableListOf(Point(5, 5))
    private var direction = "RIGHT"
    private var food = Point(10, 10)
    private var score = 0
    private var cellSize = 60
    private var running = true

    private var startX = 0f
    private var startY = 0f

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = GameThread(holder, this)
        thread.running = true
        thread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopThread()
    }

    fun update() {
        if (!running) return

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

        if (snake.drop(1).contains(head) || head.x < 0 || head.y < 0 ||
            head.x > width / cellSize || head.y > height / cellSize) {
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
        canvas.drawColor(Color.BLACK)

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
            paint.textSize = 60f
            canvas.drawText("Tap to Restart", width / 3f, height / 2f + 100, paint)
        }
    }

    private fun saveScore() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user.uid).update("score", score)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!running && event.action == MotionEvent.ACTION_DOWN) {
            resetGame()
            return true
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val deltaX = event.x - startX
                val deltaY = event.y - startY
                if (abs(deltaX) > abs(deltaY)) {
                    if (deltaX > 0) changeDirection("RIGHT")
                    else changeDirection("LEFT")
                } else {
                    if (deltaY > 0) changeDirection("DOWN")
                    else changeDirection("UP")
                }
            }
        }
        return true
    }

    fun changeDirection(newDir: String) {
        if ((direction == "UP" && newDir == "DOWN") ||
            (direction == "DOWN" && newDir == "UP") ||
            (direction == "LEFT" && newDir == "RIGHT") ||
            (direction == "RIGHT" && newDir == "LEFT")
        ) return
        direction = newDir
    }

    fun resetGame() {
        stopThread() // stop previous thread if any
        snake = mutableListOf(Point(5, 5))
        direction = "RIGHT"
        food = Point(10, 10)
        score = 0
        running = true
        thread = GameThread(holder, this)
        thread.running = true
        thread.start()
    }

    fun stopThread() {
        if (thread.isAlive) {
            thread.running = false
            try { thread.join() } catch (e: InterruptedException) {}
        }
    }
}