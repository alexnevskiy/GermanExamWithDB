package com.example.picturedownloading

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.AsyncTask.Status
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import kotlin.random.Random

class AsyncTaskActivity : AppCompatActivity() {
    var url = "https://picsum.photos/"

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var button: Button

    private var task: DownloadImageTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            if (task?.status == Status.PENDING || task?.status == Status.FINISHED || task == null) {
                val random = Random.nextInt(100, 2000)
                task = DownloadImageTask(imageView, progressBar)
                task?.execute(url + random.toString())
            } else {
                Toast.makeText(baseContext, "Прошлая картинка ещё грузится, пожалуйста подождите", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        try {
            Log.d("AsyncTask", "AsyncTask status is " + task?.status)
            task?.cancel(true)
            Log.d("AsyncTask", "AsyncTask is canceled. Status is " + task?.status)
        } catch (e: UninitializedPropertyAccessException) {
            println("Task is not initialized")
        }
        super.onPause()
    }

    private class DownloadImageTask(private val imageView: ImageView, private val progressBar: ProgressBar) : AsyncTask<String?, Void?, Bitmap?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = VISIBLE
        }

        override fun doInBackground(vararg params: String?): Bitmap? {
            val urlDisplay = params[0]
            var pictureBitMap: Bitmap? = null
            try {
                val input = URL(urlDisplay).openStream()
                if (isCancelled) return null
                pictureBitMap = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                Log.e("Error", e.message!!)
                e.printStackTrace()
            }
            return pictureBitMap
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            progressBar.visibility = INVISIBLE
        }

        override fun onCancelled() {
            super.onCancelled()
            progressBar.visibility = INVISIBLE
            Log.d("AsyncTask", "AsyncTask was canceled.")
        }
    }
}