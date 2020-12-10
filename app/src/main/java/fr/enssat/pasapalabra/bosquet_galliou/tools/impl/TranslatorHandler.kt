package fr.enssat.pasapalabra.bosquet_galliou.tools.impl

import android.content.Context
import android.util.Log
import fr.enssat.pasapalabra.bosquet_galliou.tools.TranslationTool
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class TranslatorHandler(context: Context, from: Locale, to: Locale): TranslationTool {

    private val options = TranslatorOptions.Builder()
                                .setSourceLanguage(from.language)
                                .setTargetLanguage(to.language)
                                .build()

    private val translator = Translation.getClient(options)

    private val conditions = DownloadConditions.Builder()
                                .requireWifi()
                                .build()

    init { var res =translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener { Log.d("Translation", "download completed") }
                .addOnFailureListener { e -> Log.e("Translation", "Download failed ", e) }
        Tasks.await(res)
    }

    override fun translate(text: String, callback: (String) -> Unit) {
        //Log.d("Translator handler", "Translate handler "+text)

        var res : Task<String>
        res =translator.translate(text)
            .addOnSuccessListener(callback)
            .addOnFailureListener { e -> Log.e("Translation", "Translation failed", e) }
        Tasks.await(res)
    }

    override fun close() {
        translator.close()
    }
}