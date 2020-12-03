package com.example.pasapalabra.workers

import android.content.Context
import android.text.TextUtils
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.pasapalabra.KEY_STT
import com.example.pasapalabra.KEY_TT
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.TranslationTool
import kotlinx.android.synthetic.main.activity_translator.*
import timber.log.Timber
import java.util.*

class TranslatorWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        lateinit var translator: TranslationTool
        val stt = inputData.getString(KEY_STT)

        val service = BlockService(this)
        translator = service.translator(Locale.FRENCH, Locale.ENGLISH)

        return try {
            if (TextUtils.isEmpty(stt)) {
                Timber.e("Invalid input stt")
                throw IllegalArgumentException("Invalid input stt")
            }
            var output: String
            output = ""
            translator.translate(stt.toString()) { enText ->
               output = enText
            }
            val outputData = workDataOf(KEY_TT to output)
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }
}