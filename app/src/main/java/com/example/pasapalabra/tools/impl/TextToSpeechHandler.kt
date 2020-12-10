package com.example.pasapalabra.tools.impl
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.pasapalabra.tools.TextToSpeechTool
import com.google.android.gms.tasks.Task
import java.lang.Thread.sleep
import java.util.*

class TextToSpeechHandler(context: Context, val locale: Locale): TextToSpeechTool {
    private val flag : Boolean = false
    private val tss : Tss = Tss()
    private val speaker : TextToSpeech = TextToSpeech(context,tss)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun speak(text: String) {
        while ( !tss.flag) {
            sleep(1500)
        }
        Log.d("TTS handler","speak function $text in $locale")
        speaker.language = locale
        Log.d("TTS handler","speak function $text in ${speaker.language.toLanguageTag()}")
        speaker.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun stop() {
        speaker.stop()
    }

    override fun close() {
        speaker.shutdown()
    }

    override fun isSpeaking(): Boolean {
        return speaker.isSpeaking
    }
}

class Tss() : TextToSpeech.OnInitListener{
    var flag : Boolean =false
    override fun onInit(status: Int) {
        Log.d("Speak", "OnInit")
        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speak", "status: $status")
            this.flag =true

        }else{
            Log.e("TTS", "Initilization Failed!")
        }
    }
}
