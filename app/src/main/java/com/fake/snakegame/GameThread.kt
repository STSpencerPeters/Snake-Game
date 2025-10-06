package com.fake.snakegame

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    var running = false

    override fun run() {
        while (running) {
            val canvas: Canvas = surfaceHolder.lockCanvas() ?: continue
            synchronized(surfaceHolder) {
                gameView.update()
                gameView.draw(canvas)
            }
            surfaceHolder.unlockCanvasAndPost(canvas)
            sleep(150)
        }
    }
}