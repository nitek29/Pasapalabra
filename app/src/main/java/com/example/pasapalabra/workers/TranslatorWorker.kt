package com.example.pasapalabra.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.*
import com.example.pasapalabra.KEY_RECO
import com.example.pasapalabra.KEY_STT
import com.example.pasapalabra.KEY_TT
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.BlockServiceContext
import com.example.pasapalabra.tools.TranslationTool
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.activity_translator.*
import timber.log.Timber
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.system.exitProcess

class TranslatorWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private var translated : String?=null

    /*private fun translator(text: String) : String{
        var res : String
        res = "aaa"
        // Create an English-German translator:
        val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.GERMAN)
                .build()
        val englishGermanTranslator = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                    Log.d("Download Model", "DL ok")
                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    // ...
                     Log.e("Download Model", "DL falied", exception)
                }
        var resu : Task<String>
        resu = englishGermanTranslator.translate(text)
                .addOnSuccessListener { translatedText ->
                    // Translation successful.
                    Log.d("Translator Worker", "Translation result2 $translatedText")
                    res = translatedText
                }
                .addOnFailureListener { exception ->
                    // Error.
                    // ...
                    Log.e("Translation", "Translation falied", exception)
                }
        Tasks.await(resu)
        return res
    }*/

    override fun doWork(): ListenableWorker.Result {

        lateinit var tt : String
        Log.d("Translator Worker", "Start Translator Worker")
        val appContext = applicationContext
        lateinit var translator: TranslationTool
        val stt = inputData.getString(KEY_STT)
        Log.d("Translator Worker", "stt ="+stt)

        val service = BlockService(this)
        translator = service.translator(Locale.FRENCH, Locale.ENGLISH)

        val outputData : Data

       return try {

            if (TextUtils.isEmpty(stt)) {
                Timber.e("Invalid input stt")
                throw IllegalArgumentException("Invalid input stt")
            }

            translator.translate(stt.toString()) { enText ->
                this.translated=enText
            }

            //this.translated = this.translator(stt.toString())

            Log.d("Translator Worker", "Translation result "+this.translated)

            outputData = workDataOf(KEY_TT to this.translated)
            translator.close()
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            return Result.failure()
        }
    }
}