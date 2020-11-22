package ru.spbstu.icc.kspt.lab2.continuewatch

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class CoroutineActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var job: Job

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    private fun startCoroutine() {
        job = scope.launch {
            while (true) {
                delay(1000)
                launch(Dispatchers.Main) { secondsDisplay() }
                Log.d("Job", "Job running with hashcode: " + job.hashCode())
                Log.d("CoroutineScope", "CoroutineScope running with hashcode: " + scope.hashCode())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startCoroutine()
    }

    override fun onResume() {
        if (!job.isActive) {
            startCoroutine()
        }
        super.onResume()
    }

    override fun onPause() {
        job.cancel()
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