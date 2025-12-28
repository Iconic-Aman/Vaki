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
    private var isListening = false
    private var timeoutRunnable: Runnable? = null
    
    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("VakiDebug", "SpeechRecognizer: Ready for speech")
            startTimeoutTimer()
        }
        override fun onBeginningOfSpeech() {
            Log.d("VakiDebug", "SpeechRecognizer: Beginning of speech")
            cancelTimeoutTimer()
        }
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            Log.d("VakiDebug", "SpeechRecognizer: End of speech")
        }
        override fun onError(error: Int) {
            handleError(error)
        }

        override fun onResults(results: Bundle?) {
            cancelTimeoutTimer()
            if (isListening) {
                isListening = false
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!data.isNullOrEmpty()) {
                    val result = data[0]
                    Log.d("VakiDebug", "SpeechRecognizer Result: $result")
                    mainHandler.post { onResult(result) }
                } else {
                    handleError(SpeechRecognizer.ERROR_NO_MATCH)
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun handleError(error: Int) {
        cancelTimeoutTimer()
        if (isListening) {
            isListening = false
            Log.e("VakiDebug", "SpeechRecognizer Error: $error")
            mainHandler.post { onError(error) }
            mainHandler.post { speechRecognizer?.cancel() }
        }
    }

    private fun startTimeoutTimer() {
        cancelTimeoutTimer()
        timeoutRunnable = Runnable {
            Log.d("VakiDebug", "SpeechRecognizer: Watchdog timeout triggered")
            handleError(SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
        }
        mainHandler.postDelayed(timeoutRunnable!!, 5000) // 5 seconds watchdog
    }

    private fun cancelTimeoutTimer() {
        timeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        timeoutRunnable = null
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
            if (isListening) return@post
            try {
                ensureRecognizer()
                Log.d("VakiDebug", "SpeechRecognizer: Starting session...")
                speechRecognizer?.startListening(recognizerIntent)
                isListening = true
            } catch (e: Exception) {
                Log.e("VakiDebug", "SpeechRecognizer Exception: ${e.message}")
                isListening = false
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            cancelTimeoutTimer()
            if (isListening) {
                Log.d("VakiDebug", "SpeechRecognizer: Cancelling session...")
                speechRecognizer?.cancel() 
                isListening = false
            }
        }
    }

    fun destroy() {
        mainHandler.post {
            cancelTimeoutTimer()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
            isListening = false
        }
    }
}
