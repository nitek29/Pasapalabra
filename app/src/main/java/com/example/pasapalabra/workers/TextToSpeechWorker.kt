package com.example.pasapalabra.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.pasapalabra.KEY_TT
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.TextToSpeechTool
import kotlinx.android.synthetic.main.activity_text_to_speech.*
import timber.log.Timber

class TextToSpeechWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        Log.d("TTS Worker", "Start TTS Worker")
        val appContext = applicationContext
        val tt = inputData.getString(KEY_TT)
        Log.d("TTS Worker", "translation text  :$tt")

        lateinit var speaker: TextToSpeechTool
        val service = BlockService(this)
        speaker = service.textToSpeech()

        return try {
            if (TextUtils.isEmpty(tt)) {
                Timber.e("Invalid input tt")
                throw IllegalArgumentException("Invalid input tt ")
            }
            //cf build.gradle kotlin-android-extensions
            //cf import kotlinx.android.synthetic.main.activity_text_to_speech.*
            //say goodbye to findviewbyid, recovering and binding view from layout


            val text = tt.toString()
            speaker.speak(text)

            Result.success()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }
}