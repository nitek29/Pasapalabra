package com.example.pasapalabra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pasapalabra.R
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.TextToSpeechTool


import kotlinx.android.synthetic.main.activity_text_to_speech.*


class TextToSpeechActivity : AppCompatActivity() {

    lateinit var speaker: TextToSpeechTool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_to_speech)

        val service = BlockService(this)
        speaker = service.textToSpeech()

        //cf build.gradle kotlin-android-extensions
        //cf import kotlinx.android.synthetic.main.activity_text_to_speech.*
        //say goodbye to findviewbyid, recovering and binding view from layout

        play_button.setOnClickListener {
            val text = edit_query.text.toString()
            speaker.speak(text)
        }
    }

    override fun onDestroy() {
        speaker.close()
        super.onDestroy()
    }
}