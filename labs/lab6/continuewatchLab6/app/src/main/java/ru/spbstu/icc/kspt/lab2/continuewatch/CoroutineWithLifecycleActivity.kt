package ru.spbstu.icc.kspt.lab2.continuewatch

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class CoroutineWithLifecycleActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    private var scope: LifecycleCoroutineScope = lifecycleScope

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    private fun startCoroutine() {
        scope.launch(Dispatchers.Default) {
            whenResumed {
                while (true) {
                    delay(1000)
                    launch(Dispatchers.Main) { secondsDisplay() }
                    Log.d("CoroutineScope", "CoroutineScope running with hashcode: " + scope.hashCode())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startCoroutine()
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