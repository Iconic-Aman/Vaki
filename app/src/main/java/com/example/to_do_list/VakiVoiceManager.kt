package com.example.to_do_list

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class VakiVoiceManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false
    private var onSpeechFinished: (() -> Unit)? = null

    init {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                // When Vaki finishes talking, we trigger the callback
                onSpeechFinished?.invoke()
            }
            override fun onError(utteranceId: String?) {}
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setPitch(1.2f)
                tts.setSpeechRate(1.0f)
                isReady = true
            }
        }
    }

    fun speak(text: String, onFinished: (() -> Unit)? = null) {
        if (isReady) {
            onSpeechFinished = onFinished
            // Adding a unique utterance ID is required for the listener to work
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "VakiSpeechID")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "VakiSpeechID")
        }
    }

    fun shutDown() {
        tts.stop()
        tts.shutdown()
    }
}
