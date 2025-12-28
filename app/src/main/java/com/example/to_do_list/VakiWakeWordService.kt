package com.example.to_do_list

import android.content.Context
import android.util.Log
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class VakiWakeWordService(
    private val context: Context,
    private val onWakeWordDetected: () -> Unit
) : RecognitionListener {

    private var speechService: SpeechService? = null
    private var model: Model? = null
    private var isInitializing = false

    init {
        initModel()
    }

    private fun initModel() {
        if (isInitializing) return
        isInitializing = true
        Log.d("VakiDebug", "VakiWakeWordService: Unpacking model...")
        
        StorageService.unpack(context, "model-en-Ind", "vosk-model",
            { m: Model ->
                this.model = m
                isInitializing = false
                Log.d("VakiDebug", "VakiWakeWordService: Model Ready")
                start()
            },
            { exception: Exception -> 
                isInitializing = false
                Log.e("VakiDebug", "VakiWakeWordService: Model Load Error: ${exception.message}") 
            }
        )
    }

    fun start() {
        if (speechService != null) return // Already running
        
        model?.let { m ->
            try {
                Log.d("VakiDebug", "VakiWakeWordService: Starting SpeechService...")
                // Vocabulary optimization for "Vaki"
                val recognizer = Recognizer(m, 16000.0f, "[\"vaki\", \"hi vaki\", \"vakee\", \"vakey\", \"[unread]\"]")
                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening(this)
                Log.d("VakiDebug", "VakiWakeWordService: Listening active")
            } catch (e: Exception) {
                Log.e("VakiDebug", "VakiWakeWordService: Failed to start: ${e.message}")
            }
        } ?: Log.e("VakiDebug", "VakiWakeWordService: Cannot start, model not loaded")
    }

    override fun onResult(hypothesis: String) {
        Log.d("VakiDebug", "VakiWakeWordService Result: $hypothesis")
        // Hypothesis comes as JSON like {"text" : "vaki"}
        if (hypothesis.lowercase().contains("vaki") || 
            hypothesis.lowercase().contains("vakee") || 
            hypothesis.lowercase().contains("vakey")) {
            Log.d("VakiDebug", "VakiWakeWordService: MATCH FOUND!")
            onWakeWordDetected()
        }
    }

    override fun onPartialResult(hypothesis: String) {
        // Log partials to see if it's picking up sound at all
        if (hypothesis.length > 15) { // Only log if it actually contains text
            Log.d("VakiDebug", "VakiWakeWordService Partial: $hypothesis")
        }
    }

    override fun onFinalResult(hypothesis: String) {
        Log.d("VakiDebug", "VakiWakeWordService Final: $hypothesis")
    }

    override fun onError(exception: Exception) {
        Log.e("VakiDebug", "VakiWakeWordService Error: ${exception.message}")
    }

    override fun onTimeout() {
        Log.d("VakiDebug", "VakiWakeWordService Timeout")
    }

    fun stop() {
        Log.d("VakiDebug", "VakiWakeWordService: Stopping service...")
        speechService?.let {
            it.stop()
            it.shutdown()
            speechService = null
        }
    }
}
