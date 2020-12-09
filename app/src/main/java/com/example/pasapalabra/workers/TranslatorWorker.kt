package com.example.pasapalabra.workers

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
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
import kotlin.collections.HashMap
import kotlin.system.exitProcess

class TranslatorWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private var translated : String?=null
    private var tts_flag : Boolean = false

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

    fun getCountryCode(countryName :String) : String? {

        // Get all country codes in a string array.
        var isoCountryCodes : Array<String> = Locale.getISOCountries();
        var countryMap : MutableMap<String?, String?> = mutableMapOf()
        var locale: Locale;
        var  name : String

        // Iterate through all country codes:
        for ( code in isoCountryCodes) {
            // Create a locale using each country code
            locale = Locale("", code);
            // Get country name for each code.
            name = locale.getDisplayCountry();
            // Map all country names and codes in key - value pairs.
            countryMap.put(name, code);
        }

        // Return the country code for the given country name using the map.
        // Here you will need some validation or better yet
        // a list of countries to give to user to choose from.
        return countryMap.get(countryName); // "NL" for Netherlands.
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun doWork(): ListenableWorker.Result {


        Log.d("Translator Worker", "Start Translator Worker")
        val appContext = applicationContext
        lateinit var translator: TranslationTool
        val stt = inputData.getString(KEY_STT)
        Log.d("Translator Worker", "stt ="+stt)

        val src = inputData.getString("LANG_SRC")
        var dst = inputData.getString("LANG_DST")
        if (dst==""){
            this.tts_flag = true
            dst = inputData.getString("LANG_OUT")
            Log.d("Translator Worker", "Lang output : $dst")

        }

        val service = BlockService(this)
        //translator = service.translator(Locale.FRENCH, Locale.ENGLISH)

        Log.d("Translator Worker", "Lang src : ${Locale(src)} \t Lang dst : ${Locale(dst)}")
        translator = service.translator(Locale(src), Locale(dst))

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
           Log.d("Translator Worker", "Translation flag output is ${this.tts_flag}")
           outputData = if (this.tts_flag){
               workDataOf(KEY_TT to this.translated)
           }else {
               workDataOf(KEY_STT to this.translated)
           }
            translator.close()
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            return Result.failure()
        }
    }
}