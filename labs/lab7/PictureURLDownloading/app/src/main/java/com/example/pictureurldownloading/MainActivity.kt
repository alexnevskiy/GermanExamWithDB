package com.example.pictureurldownloading

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import kotlin.random.Random

private const val MSG_TO_MESSENGER = 1
private const val MSG_TO_CLIENT = 2

class MainActivity : AppCompatActivity() {

    var url = "https://picsum.photos/"
    private var isConnected = false
    private var isWaiting = false
    private var pictureMessenger: Messenger? = null

    lateinit var textView: TextView
    lateinit var progressBar: ProgressBar
    lateinit var buttonStartedService: Button
    lateinit var buttonBoundService: Button
    lateinit var myMessenger: Messenger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        buttonStartedService = findViewById(R.id.button_started_service)
        buttonBoundService = findViewById(R.id.button_bound_service)

        myMessenger = Messenger(ClientHandler(this))

        buttonStartedService.setOnClickListener {
            val random = Random.nextInt(100, 2000)
            val intent = Intent(this, PictureDownloadingService::class.java)
                .putExtra("url", url + random.toString())
            startService(intent)
        }

        buttonBoundService.setOnClickListener {
            if (!isWaiting) {
                progressBar.visibility = VISIBLE
                val random = Random.nextInt(100, 2000)
                val message = Message.obtain(null, MSG_TO_MESSENGER,
                    url + random.toString()).apply {
                    replyTo = myMessenger
                }
                pictureMessenger?.send(message)
                isWaiting = true
            } else {
                Toast.makeText(baseContext, "Прошлая картинка ещё грузится, пожалуйста подождите", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        val intent = Intent(this, PictureDownloadingService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        super.onStart()
    }

    override fun onStop() {
        if (isConnected) {
            unbindService(serviceConnection)
            isConnected = false
            isWaiting = false
            progressBar.visibility = INVISIBLE
        }
        super.onStop()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            pictureMessenger = Messenger(service)
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            pictureMessenger = null
            isConnected = false
            isWaiting = false
            progressBar.visibility = INVISIBLE
        }
    }

    internal class ClientHandler(private val activity: MainActivity) : Handler(activity.mainLooper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_TO_CLIENT -> {
                    activity.textView.text = msg.obj.toString()
                    activity.progressBar.visibility = INVISIBLE
                    activity.isWaiting = false
                }
            }
        }
    }
}