package ru.spbstu.icc.kspt.lab2.continuewatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class ExecutorActivity : AppCompatActivity()  {
    var secondsElapsed: Int = 0

    var counts: Boolean = false

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    private fun createThread() {
        val executor = (application as ExecutorApplication).executor
        executor.submit {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    break
                }
                secondsDisplay()
            }
        }
        //executor.shutdownNow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        counts = true
        createThread()
    }

    override fun onResume() {
        if (!counts) {
            createThread()
            counts = true
        }
        super.onResume()
    }

    override fun onPause() {
        counts = false
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