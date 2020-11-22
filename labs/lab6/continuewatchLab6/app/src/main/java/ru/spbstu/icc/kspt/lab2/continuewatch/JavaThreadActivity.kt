package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class JavaThreadActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    @Volatile
    var backgroundThread: Thread? = null

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    private fun createThread() {
        backgroundThread = Thread {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    break
                }
                secondsDisplay()
            }
        }
        backgroundThread?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createThread()
    }

    override fun onResume() {
        if (backgroundThread == null) {
            createThread()
        }
        super.onResume()
    }

    override fun onPause() {
        backgroundThread?.interrupt()
        backgroundThread = null
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("seconds", secondsElapsed)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt("seconds")
        secondsDisplay()
        super.onRestoreInstanceState(savedInstanceState)
    }
}
