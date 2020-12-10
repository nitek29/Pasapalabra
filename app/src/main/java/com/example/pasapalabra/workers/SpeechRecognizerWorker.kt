package com.example.pasapalabra.workers

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.pasapalabra.KEY_RECO
import com.example.pasapalabra.KEY_STT
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.BlockServiceContext
import com.example.pasapalabra.tools.SpeechToTextTool
import org.w3c.dom.Text
import timber.log.Timber

class SpeechRecognizerWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        Log.d("STT Worker", "Start STT Worker")

        return try {

            var output : String = inputData.getString(KEY_RECO).toString();
            Log.d("STT Worker", "stt text ="+output)

            //speechToText.stop()
            val outputData = workDataOf(KEY_STT to output)
            Result.success(outputData)


        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying stt")
            Result.failure()
    }


    }
}