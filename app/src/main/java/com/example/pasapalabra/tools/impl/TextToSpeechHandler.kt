package com.example.pasapalabra.tools.impl
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.pasapalabra.tools.TextToSpeechTool
import com.google.android.gms.tasks.Task
import java.util.*

class TextToSpeechHandler(context: Context, val locale: Locale): TextToSpeechTool {

    private val speaker = TextToSpeech(context, object: TextToSpeech.OnInitListener {

        override fun onInit(status: Int) {
            Log.d("Speak", "OnInit")
            if (status == TextToSpeech.SUCCESS) {
                Log.d("Speak", "status: $status")

            }else{
                Log.e("TTS", "Initilization Failed!")
            }
        }
    })


    override fun speak(text: String) {
        Log.d("TTS handler","speak function $text")
        speaker.language = locale
        speaker.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun stop() {
        speaker.stop()
    }

    override fun close() {
        speaker.shutdown()
    }
}