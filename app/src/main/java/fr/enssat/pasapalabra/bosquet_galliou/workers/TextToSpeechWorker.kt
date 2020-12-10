package fr.enssat.pasapalabra.bosquet_galliou.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import fr.enssat.pasapalabra.bosquet_galliou.KEY_TT
import fr.enssat.pasapalabra.bosquet_galliou.KEY_TTS
import fr.enssat.pasapalabra.bosquet_galliou.tools.BlockServiceContext
import fr.enssat.pasapalabra.bosquet_galliou.tools.TextToSpeechTool
import timber.log.Timber
import java.lang.Thread.sleep

class TextToSpeechWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        Log.d("TTS Worker", "Start TTS Worker")
        val appContext = applicationContext
        val tt = inputData.getString(KEY_TT)
        Log.d("TTS Worker", "translation text  :$tt")
        val langOut : String = inputData.getString("LANG_OUT").toString()
        Log.d("TTS Worker", "translation text lang   :$langOut")

        lateinit var speaker: TextToSpeechTool
        val service = BlockServiceContext(appContext)

        speaker = service.textToSpeech(langOut)

        return try {
            if (TextUtils.isEmpty(tt)) {
                Timber.e("Invalid input tt")
                throw IllegalArgumentException("Invalid input tt ")
            }

            val text = tt.toString()
            speaker.speak(text)
            do{
                sleep(1000)
            }while(speaker.isSpeaking())
            speaker.stop()
            speaker.close()
            val outputData = workDataOf(KEY_TTS to tt)

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }
}