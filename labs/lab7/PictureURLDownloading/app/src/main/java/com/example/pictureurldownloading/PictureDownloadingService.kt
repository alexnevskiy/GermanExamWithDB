package com.example.pictureurldownloading

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.View
import kotlinx.coroutines.*
import java.io.File
import java.net.URL

private const val MSG_TO_MESSENGER = 1
private const val MSG_TO_CLIENT = 2

class PictureDownloadingService : IntentService("PictureDownloading") {
    private val intentAction = "com.example.pictureurldownloading.PICTURE_DOWNLOAD"

    private lateinit var pictureMessenger: Messenger
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    internal class MessageHandler(private val service: PictureDownloadingService) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_TO_MESSENGER -> {
                    Log.i("Thread Message", Thread.currentThread().name)
                    service.startCoroutine(msg.replyTo, msg.obj as String)
                }
            }
        }
    }

    override fun onCreate() {
        Log.i("Service", "Service is started")
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i("Thread Service", Thread.currentThread().name)
        val url = intent?.getStringExtra("url").toString()
        val imageBitMap = downloadImage(url)
        val imagePath = createFile(imageBitMap)
        Log.i("Service", "Started service create file")

        Intent().also { intent ->
            intent.action = intentAction
            intent.putExtra("url", imagePath)
            sendBroadcast(intent)
            Log.i("Service", "Sending broadcast message")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        pictureMessenger = Messenger(MessageHandler(this))
        return pictureMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (job != null && job?.isActive!!) {
            job?.cancel()
            Log.i("Coroutine", "Job is canceled")
        }
        Log.i("Service", "Service is unbind by client")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.i("Service", "Service is destroyed")
        super.onDestroy()
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

    private fun createFile(imageBitMap: Bitmap?): String {
        val filename = "image" + imageBitMap.hashCode()

        val fileStream = this.openFileOutput(filename, Context.MODE_PRIVATE)
        imageBitMap?.compress(Bitmap.CompressFormat.PNG, 100, fileStream)
        fileStream.close()

        return File(this.filesDir, filename).absolutePath
    }

    private fun startCoroutine(messenger: Messenger, url: String) {
        job = scope.launch {
            Log.i("Thread Coroutine", Thread.currentThread().name)
            val imageBitMap = downloadImage(url)
            if (!isActive) return@launch
            val imagePath = createFile(imageBitMap)
            val message = Message.obtain(null, MSG_TO_CLIENT, imagePath)
            messenger.send(message)
        }
    }
}