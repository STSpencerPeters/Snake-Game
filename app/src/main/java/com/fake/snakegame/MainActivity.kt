package com.fake.snakegame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = FrameLayout(this)
        gameView = GameView(this)
        layout.addView(gameView)

        // Direction buttons
        val btnUp = Button(this).apply { text = "↑" }
        val btnDown = Button(this).apply { text = "↓" }
        val btnLeft = Button(this).apply { text = "←" }
        val btnRight = Button(this).apply { text = "→" }

        // Restart & Leaderboard buttons
        val btnRestart = Button(this).apply {
            text = "Restart"
            setOnClickListener {
                gameView.resetGame()
            }
        }

        val btnLeaderboard = Button(this).apply {
            text = "Leaderboard"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, LeaderboardActivity::class.java))
            }
        }

        // Position buttons overlay
        val overlay = FrameLayout(this)
        overlay.addView(btnUp, FrameLayout.LayoutParams(200, 200).apply {
            topMargin = 1500
            leftMargin = 500
        })
        overlay.addView(btnDown, FrameLayout.LayoutParams(200, 200).apply {
            topMargin = 1900
            leftMargin = 500
        })
        overlay.addView(btnLeft, FrameLayout.LayoutParams(200, 200).apply {
            topMargin = 1700
            leftMargin = 300
        })
        overlay.addView(btnRight, FrameLayout.LayoutParams(200, 200).apply {
            topMargin = 1700
            leftMargin = 700
        })

        overlay.addView(btnRestart, FrameLayout.LayoutParams(400, 150).apply {
            topMargin = 100
            leftMargin = 50
        })

        overlay.addView(btnLeaderboard, FrameLayout.LayoutParams(400, 150).apply {
            topMargin = 100
            leftMargin = 500
        })

        layout.addView(overlay)
        setContentView(layout)

        // Button listeners
        btnUp.setOnClickListener { gameView.changeDirection("UP") }
        btnDown.setOnClickListener { gameView.changeDirection("DOWN") }
        btnLeft.setOnClickListener { gameView.changeDirection("LEFT") }
        btnRight.setOnClickListener { gameView.changeDirection("RIGHT") }
    }
}