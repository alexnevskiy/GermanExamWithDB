package com.example.picturedownloading

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.URL
import kotlin.random.Random

class CoroutineActivity : AppCompatActivity() {
    var url = "https://picsum.photos/"

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var button: Button

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            progressBar.visibility = VISIBLE
            if (job == null || !job?.isActive!!) {
                startCoroutine()
            } else {
                Toast.makeText(baseContext, "Прошлая картинка ещё грузится, пожалуйста подождите", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        if (job?.isActive!!) {
            job?.cancel()
            progressBar.visibility = INVISIBLE
        }
        super.onPause()
    }
    
    private fun downloadImage(url: String): Bitmap? {
        var pictureBitMap: Bitmap? = null
        try {
            val input = URL(url).openStream()
            pictureBitMap = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
            e.printStackTrace()
        }
        return pictureBitMap
    }

    private fun startCoroutine() {
        job = scope.launch {
            val random = Random.nextInt(100, 2000)
            val imageBitMap = downloadImage(url + random.toString())
            launch(Dispatchers.Main) {
                imageView.setImageBitmap(imageBitMap)
                progressBar.visibility = INVISIBLE }
        }
    }
}