package com.example.to_do_list

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class VakiVoiceManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Setting the language to English (US)
            val result = tts.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                // To make Vaki sound "Unique" and not like a standard robot:
                tts.setPitch(1.2f) // Higher makes it sound more like a friendly assistant
                tts.setSpeechRate(1.0f)
                isReady = true
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            // QUEUE_FLUSH means it stops any current talking to say the new text immediately
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutDown() {
        tts.stop()
        tts.shutdown()
    }
}
