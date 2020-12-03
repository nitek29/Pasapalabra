package com.example.pasapalabra.workers

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.pasapalabra.KEY_STT
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.SpeechToTextTool
import kotlinx.android.synthetic.main.activity_speech_to_text.*
import org.w3c.dom.Text
import timber.log.Timber

class SpeechRecognizerWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    lateinit var speechToText: SpeechToTextTool
    override fun doWork(): Result {
        val appContext = applicationContext

        val service = BlockService(this)
        speechToText = service.speechToText()

        return try {
            var output : String;
            output = "";

            speechToText.stop()
            val outputData = workDataOf(KEY_STT to output)
            Result.success(outputData)


        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
    }


    }
}