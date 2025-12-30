package com.example.to_do_list

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class VakiWakeWordService : Service(), RecognitionListener {

    private var speechService: SpeechService? = null
    private var model: Model? = null
    private var isInitializing = false
    private var onWakeWordDetected: (() -> Unit)? = null

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): VakiWakeWordService = this@VakiWakeWordService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun setWakeWordListener(listener: () -> Unit) {
        onWakeWordDetected = listener
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
        initModel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun initModel() {
        if (isInitializing) return
        isInitializing = true
        Log.d("VakiDebug", "VakiWakeWordService: Unpacking model...")
        
        StorageService.unpack(this, "model-en-Ind", "vosk-model",
            { m: Model ->
                this.model = m
                isInitializing = false
                Log.d("VakiDebug", "VakiWakeWordService: Model Ready")
                startListening()
            },
            { exception: Exception -> 
                isInitializing = false
                Log.e("VakiDebug", "VakiWakeWordService: Model Load Error: ${exception.message}") 
            }
        )
    }

    fun startListening() {
        if (speechService != null) return
        
        model?.let { m ->
            try {
                Log.d("VakiDebug", "VakiWakeWordService: Starting SpeechService...")
                val vocabulary = "[\"vaki\", \"vakee\", \"vakey\", \"vicky\", \"wakey\", \"bucky\", \"hi\", \"hello\", \"hey\", \"[unk]\"]"
                val recognizer = Recognizer(m, 16000.0f, vocabulary)
                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening(this)
                Log.d("VakiDebug", "VakiWakeWordService: Listening active")
            } catch (e: Exception) {
                Log.e("VakiDebug", "VakiWakeWordService: Failed to start: ${e.message}")
            }
        } ?: Log.e("VakiDebug", "VakiWakeWordService: Cannot start, model not loaded")
    }

    fun stopListening() {
        Log.d("VakiDebug", "VakiWakeWordService: Stopping service...")
        speechService?.let {
            it.stop()
            it.shutdown()
            speechService = null
        }
    }

    override fun onResult(hypothesis: String) {
        Log.d("VakiDebug", "VakiWakeWordService Result: $hypothesis")
        val textValue = hypothesis.substringAfter("\"text\" : \"").substringBefore("\"").lowercase().trim()
        val wakeWords = listOf("vaki", "vakee", "vakey", "vicky", "wakey")
        
        val isMatch = wakeWords.any { variant ->
            textValue == "hi $variant" || textValue == "hello $variant" || textValue == "hey $variant"
        }
        
        if (isMatch) {
            Log.d("VakiDebug", "VakiWakeWordService: WAKE-WORD MATCH FOUND!")
            onWakeWordDetected?.invoke()
        }
    }

    override fun onPartialResult(hypothesis: String) {}
    override fun onFinalResult(hypothesis: String) {}
    override fun onError(exception: Exception) {
        Log.e("VakiDebug", "VakiWakeWordService Error: ${exception.message}")
    }
    override fun onTimeout() {}

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "VakiWakeWordChannel",
                "Vaki Wake Word Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "VakiWakeWordChannel")
            .setContentTitle("Vaki is Listening")
            .setContentText("Hands-free wake word is active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onDestroy() {
        stopListening()
        super.onDestroy()
    }
}
