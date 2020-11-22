package ru.spbstu.icc.kspt.lab2.continuewatch

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class AsyncTaskActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    private lateinit var task: AsyncTaskCounter

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        task = AsyncTaskCounter(this)
        task.execute()
    }

    override fun onResume() {
        if (task.isCancelled) {
            task = AsyncTaskCounter(this)
            task.execute()
        }
        super.onResume()
    }

    override fun onPause() {
        task.cancel(true)
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

    class AsyncTaskCounter(private val activity: AsyncTaskActivity) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            while (!isCancelled) {
                TimeUnit.SECONDS.sleep(1)
                publishProgress()
                Log.d("AsyncTask", "AsyncTask is running with hashcode: " + hashCode())
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            activity.secondsDisplay()
        }

        override fun onCancelled() {
            super.onCancelled()
            Log.d("AsyncTask", "Canceled")
        }
    }
}