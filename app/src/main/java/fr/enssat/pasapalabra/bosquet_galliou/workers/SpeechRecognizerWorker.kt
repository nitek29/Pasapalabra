package fr.enssat.pasapalabra.bosquet_galliou.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import fr.enssat.pasapalabra.bosquet_galliou.KEY_RECO
import fr.enssat.pasapalabra.bosquet_galliou.KEY_STT
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