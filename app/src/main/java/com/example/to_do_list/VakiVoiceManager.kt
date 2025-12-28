package com.example.to_do_list

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class VakiVoiceManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false
    private var onSpeechFinished: (() -> Unit)? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d("VakiVoice", "TTS Started: $utteranceId")
            }
            override fun onDone(utteranceId: String?) {
                Log.d("VakiVoice", "TTS Done: $utteranceId")
                mainHandler.post {
                    onSpeechFinished?.invoke()
                    onSpeechFinished = null
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e("VakiVoice", "TTS Error: $utteranceId")
                mainHandler.post {
                    onSpeechFinished?.invoke() // Invoke anyway to unblock UI
                    onSpeechFinished = null
                }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e("VakiVoice", "TTS Error: $utteranceId, ErrorCode: $errorCode")
                mainHandler.post {
                    onSpeechFinished?.invoke()
                    onSpeechFinished = null
                }
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setPitch(1.2f)
                tts.setSpeechRate(1.0f)
                isReady = true
                Log.d("VakiVoice", "TTS Initialized successfully")
            } else {
                Log.e("VakiVoice", "TTS Language not supported")
            }
        } else {
            Log.e("VakiVoice", "TTS Initialization failed")
        }
    }

    fun speak(text: String, onFinished: (() -> Unit)? = null) {
        if (isReady) {
            onSpeechFinished = onFinished
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "VakiSpeechID")
            val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "VakiSpeechID")
            
            if (result == TextToSpeech.ERROR) {
                Log.e("VakiVoice", "TTS failed to speak")
                mainHandler.post { 
                    onFinished?.invoke() 
                    onSpeechFinished = null
                }
            }
        } else {
            Log.e("VakiVoice", "TTS not ready")
            onFinished?.invoke() 
        }
    }

    fun stop() {
        mainHandler.post {
            onSpeechFinished = null
            tts.stop()
        }
    }

    fun shutDown() {
        tts.stop()
        tts.shutdown()
    }
}
