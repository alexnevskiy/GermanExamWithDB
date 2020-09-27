package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0
    var run = false

    var backgroundThread = Thread {
        while (true) {
            Thread.sleep(1000)
            if (run) {
                secondsDisplay()
            }
        }
    }

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        backgroundThread.start()
    }

    override fun onResume() {
        run = true
        super.onResume()
    }

    override fun onPause() {
        run = false
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
