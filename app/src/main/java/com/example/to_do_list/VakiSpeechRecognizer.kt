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
        // Removed explicit language string to resolve Error 13 (Language Unavailable) on some devices
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("VakiDebug", "SpeechRecognizer: Ready - Waiting for input")
            startWatchdog()
        }
        override fun onBeginningOfSpeech() {
            Log.d("VakiDebug", "SpeechRecognizer: User started speaking")
            cancelWatchdog()
        }
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            Log.d("VakiDebug", "SpeechRecognizer: Speech ended")
        }
        override fun onError(error: Int) {
            // Error 13 is often a non-fatal language check warning on certain devices.
            // We log it but only trigger full error recovery if it truly stops the session.
            if (error == 13) {
                Log.d("VakiDebug", "SpeechRecognizer: Info 13 received, continuing session...")
                return 
            }
            handleError(error)
        }

        override fun onResults(results: Bundle?) {
            if (isListening) {
                isListening = false
                cancelWatchdog()
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

        override fun onPartialResults(partialResults: Bundle?) {
            val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!data.isNullOrEmpty()) {
                Log.d("VakiDebug", "SpeechRecognizer Partial: ${data[0]}")
            }
        }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun startWatchdog() {
        cancelWatchdog()
        var secondsLeft = 5
        timeoutRunnable = object : Runnable {
            override fun run() {
                if (isListening) {
                    if (secondsLeft > 0) {
                        Log.d("VakiDebug", "Vaki is waiting for your command... ($secondsLeft s)")
                        secondsLeft--
                        mainHandler.postDelayed(this, 1000)
                    } else {
                        Log.d("VakiDebug", "Watchdog: 5s limit reached - triggering apology")
                        handleError(SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
                    }
                }
            }
        }
        mainHandler.post(timeoutRunnable as Runnable)
    }

    private fun cancelWatchdog() {
        timeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        timeoutRunnable = null
    }

    private fun handleError(error: Int) {
        if (isListening) {
            isListening = false
            cancelWatchdog()
            Log.d("VakiDebug", "SpeechRecognizer handling error $error - Resetting UI")
            mainHandler.post { onError(error) }
            mainHandler.post { 
                try {
                    speechRecognizer?.cancel() 
                } catch(e: Exception) { }
            }
        }
    }

    private fun ensureRecognizer() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {}
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(recognitionListener)
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
                Log.e("VakiDebug", "Speech module fail: ${e.message}")
                isListening = false
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            cancelWatchdog()
            if (isListening) {
                Log.d("VakiDebug", "SpeechRecognizer: Manually cancelling")
                speechRecognizer?.cancel() 
                isListening = false
            }
        }
    }

    fun destroy() {
        mainHandler.post {
            cancelWatchdog()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
            isListening = false
        }
    }
}
