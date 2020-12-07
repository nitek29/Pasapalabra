package com.example.pasapalabra.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.pasapalabra.KEY_RECO
import com.example.pasapalabra.KEY_STT
import com.example.pasapalabra.KEY_TT
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.BlockServiceContext
import com.example.pasapalabra.tools.TranslationTool
import kotlinx.android.synthetic.main.activity_translator.*
import timber.log.Timber
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.system.exitProcess

class TranslatorWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private var translated : String?=null
    val outputData = ""

    fun getData(txt :String){
        this.translated =txt
    }

    /**
     * Creates the input data bundle which includes the STRING to operate on
     * @return Data which contains the Speech to text to as a String
     */
    private fun createInputData(text : String): Data {
        val builder = Data.Builder()
        builder.putString(KEY_TT, text)

        return builder.build()
    }

    override fun doWork(): Result {

        lateinit var tt : String
        Log.d("Translator Worker", "Start Translator Worker")
        val appContext = applicationContext
        lateinit var translator: TranslationTool
        val stt = inputData.getString(KEY_STT)
        Log.d("Translator Worker", "stt ="+stt)

        val service = BlockService(this)
        translator = service.translator(Locale.FRENCH, Locale.ENGLISH)

        return try {

            if (TextUtils.isEmpty(stt)) {
                Timber.e("Invalid input stt")
                throw IllegalArgumentException("Invalid input stt")
            }

            translator.translate(stt.toString()) { enText ->

                this.translated=enText
                Log.d("Translator Worker", "Translation result2 "+this.translated)
                createInputData(enText)
            }

            Log.d("Translator Worker", "Translation result "+inputData.getString(KEY_TT))

            this.translated = "Hello it is me"
            val outputData = workDataOf(KEY_TT to this.translated)

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }
}