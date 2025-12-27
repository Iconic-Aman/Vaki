package com.example.to_do_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class VakiSpeechRecognizer(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (Int) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    
    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("VakiDebug", "SpeechRecognizer: Ready for speech")
        }
        override fun onBeginningOfSpeech() {
            Log.d("VakiDebug", "SpeechRecognizer: Beginning of speech")
        }
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            Log.d("VakiDebug", "SpeechRecognizer: End of speech")
        }
        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            Log.e("VakiDebug", "SpeechRecognizer Error: $error ($errorMessage)")
            onError(error)
        }

        override fun onResults(results: Bundle?) {
            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!data.isNullOrEmpty()) {
                val result = data[0]
                Log.d("VakiDebug", "SpeechRecognizer Result: $result")
                onResult(result)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!data.isNullOrEmpty()) {
                Log.d("VakiDebug", "SpeechRecognizer Partial: ${data[0]}")
            }
        }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun ensureRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(recognitionListener)
            }
        }
    }

    fun startListening() {
        mainHandler.post {
            try {
                ensureRecognizer()
                Log.d("VakiDebug", "SpeechRecognizer: Starting to listen...")
                speechRecognizer?.startListening(recognizerIntent)
            } catch (e: Exception) {
                Log.e("VakiDebug", "SpeechRecognizer Exception in startListening: ${e.message}")
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            Log.d("VakiDebug", "SpeechRecognizer: Stopping...")
            speechRecognizer?.stopListening()
        }
    }

    fun destroy() {
        mainHandler.post {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
}
