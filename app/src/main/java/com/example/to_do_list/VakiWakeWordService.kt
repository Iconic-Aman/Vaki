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
        if (speechService != null) return
        
        model?.let { m ->
            try {
                Log.d("VakiDebug", "VakiWakeWordService: Starting SpeechService...")
                // Balanced vocabulary to help the model distinguish words better
                val vocabulary = "[\"vaki\", \"vakee\", \"vakey\", \"vicky\", \"wakey\", \"bucky\", \"hi\", \"hello\", \"[unk]\"]"
                val recognizer = Recognizer(m, 16000.0f, vocabulary)
                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening(this)
                Log.d("VakiDebug", "VakiWakeWordService: Listening active (Strict Order Mode)")
            } catch (e: Exception) {
                Log.e("VakiDebug", "VakiWakeWordService: Failed to start: ${e.message}")
            }
        } ?: Log.e("VakiDebug", "VakiWakeWordService: Cannot start, model not loaded")
    }

    override fun onResult(hypothesis: String) {
        Log.d("VakiDebug", "VakiWakeWordService Result: $hypothesis")
        
        // Parsing the "text" field manually from JSON
        val textValue = hypothesis.substringAfter("\"text\" : \"").substringBefore("\"").lowercase().trim()
        
        // Possible variations of the name "Vaki"
        val wakeWords = listOf("vaki", "vakee", "vakey", "vicky", "wakey")
        
        // STRICT ORDER: Trigger ONLY if preceded by "hi" or "hello"
        val isMatch = wakeWords.any { variant ->
            textValue == "hi $variant" || textValue == "hello $variant"
        }
        
        if (isMatch) {
            Log.d("VakiDebug", "VakiWakeWordService: WAKE-WORD MATCH FOUND (Strict Order)!")
            onWakeWordDetected()
        } else {
            if (textValue.isNotEmpty()) {
                Log.d("VakiDebug", "VakiWakeWordService: Ignored non-prefixed command: $textValue")
            }
        }
    }

    override fun onPartialResult(hypothesis: String) {
        if (!hypothesis.contains("\"partial\" : \"\"")) {
            // Partial results logged for debugging
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
