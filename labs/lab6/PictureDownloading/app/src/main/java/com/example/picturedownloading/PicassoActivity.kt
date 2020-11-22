package com.example.picturedownloading

import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlin.random.Random

class PicassoActivity : AppCompatActivity() {
    var url = "https://picsum.photos/"

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            progressBar.visibility = VISIBLE
            val random = Random.nextInt(100, 2000)
            Picasso
                .get()
                .load(url + random)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .tag("Image")
                .into(imageView, object: Callback {
                    override fun onSuccess() {
                        progressBar.visibility = INVISIBLE
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Log.d("Picasso", "Image cannot download...")
                    }
                })
        }
    }

    override fun onPause() {
        Picasso.get().pauseTag("Image")
        super.onPause()
    }

    override fun onResume() {
        Picasso.get().resumeTag("Image")
        super.onResume()
    }
}