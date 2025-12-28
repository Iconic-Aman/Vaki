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

    init {
        // Vosk StorageService.unpack expects the source path in assets and the target path in internal storage.
        // It looks for a folder in assets, and tries to copy it.
        // The error "model-en-Ind/uuid" suggests it's trying to find a 'uuid' file inside the unzipped folder to verify it.
        Log.d("VakiDebug", "Vosk: Unpacking model from assets...")
        StorageService.unpack(context, "model-en-Ind", "vosk-model",
            { model: Model ->
                this.model = model
                Log.d("VakiDebug", "Vosk: Model unpacked successfully")
                start()
            },
            { error -> 
                Log.e("VakiDebug", "Vosk: Model failed to load: ${error.message}") 
            }
        )
    }

    fun start() {
        model?.let {
            Log.d("VakiDebug", "Vosk Wake-Word Service: Starting...")
            try {
                // Ensure the sample rate matches what the model expects (usually 16000)
                val recognizer = Recognizer(it, 16000.0f, "[\"vaki\", \"hi vaki\", \"vakee\", \"vakey\", \"[unread]\"]")
                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening(this)
                Log.d("VakiDebug", "Vosk Wake-Word Service: Ready and listening for 'Vaki'")
            } catch (e: Exception) {
                Log.e("VakiDebug", "Vosk Recognizer failed to start: ${e.message}")
            }
        }
    }

    override fun onResult(hypothesis: String) {
        Log.d("VakiDebug", "Vosk Hypothesis: $hypothesis")
        if (hypothesis.contains("vaki") || hypothesis.contains("vakee") || hypothesis.contains("vakey")) {
            Log.d("VakiDebug", "Wake-word detected!")
            onWakeWordDetected()
        }
    }

    override fun onPartialResult(hypothesis: String?) { }
    override fun onFinalResult(hypothesis: String?) { }
    override fun onError(exception: Exception?) {
        Log.e("VakiDebug", "Vosk Error: ${exception?.message}")
    }
    override fun onTimeout() { }

    fun stop() {
        speechService?.stop()
        speechService?.shutdown()
        Log.d("VakiDebug", "Vosk Wake-Word Service: Stopped")
    }
}
