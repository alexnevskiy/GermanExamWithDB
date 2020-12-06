package com.example.picturebroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    val intentAction = "com.example.pictureurldownloading.PICTURE_DOWNLOAD"
    lateinit var button: Button
    lateinit var textView: TextView
    private lateinit var broadcastReceiver: PictureBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        button = findViewById(R.id.button)
        if (intent.getStringExtra("path") != null) {
            Log.i("Activity", "Text was switched")
            textView.text = intent?.getStringExtra("path").toString()
        }

        broadcastReceiver = PictureBroadcastReceiver()
        registerReceiver(broadcastReceiver, IntentFilter(intentAction))

        button.setOnClickListener {
            textView.text = "Путь сброшен"
        }
    }

    class PictureBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Broadcast Receiver", "Message was receive")
            val intentMainActivity = Intent(context, MainActivity::class.java)
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK)
            val path = intent?.getStringExtra("url").toString()
            intentMainActivity.putExtra("path", path)
            context?.startActivity(intentMainActivity)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}