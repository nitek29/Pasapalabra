package com.example.pasapalabra

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.SpeechToTextTool
import kotlinx.android.synthetic.main.activity_speech_to_text.*

class PasapalabraActivity : AppCompatActivity() {
    private lateinit var viewModel : PasapalabraViewModel
    //private lateinit var binding:  ActivityTranslateBinding  -> activity_translate.xml

    private val RecordAudioRequestCode = 1

    lateinit var speechToText: SpeechToTextTool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityTranslateBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContentView(R.layout.activity_speech_to_text) // TODO a suppr

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        record_button.setOnTouchListener { v, event ->
            var stt : String
            stt =""
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d("Reco UI", "Button pressed")
                v.performClick()
                speechToText.start(object : SpeechToTextTool.Listener {
                    override fun onResult(text: String, isFinal: Boolean) {
                        if (isFinal) {  stt = text}
                    }
                })
            } else if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Reco UI", "Button releases")
                viewModel.applyTranslation(stt)


            }
            false
        }


        // Get the ViewModel
        viewModel = ViewModelProvider(this).get(PasapalabraViewModel::class.java)

        //binding.goButton.setOnClickListener( viewModel.applyTranslation())

        // Speech
    }

    private fun checkPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }


}