package fr.enssat.pasapalabra.bosquet_galliou.tools

import android.content.Context
import androidx.work.Worker
import fr.enssat.pasapalabra.bosquet_galliou.tools.impl.SpeechRecognizerHandler
import fr.enssat.pasapalabra.bosquet_galliou.tools.impl.TextToSpeechHandler
import fr.enssat.pasapalabra.bosquet_galliou.tools.impl.TranslatorHandler

import java.util.Locale

interface TextToSpeechTool {
    fun speak(text: String)
    fun stop()
    fun close()
    fun isSpeaking(): Boolean
}

interface TranslationTool {
    fun translate(text: String, callback: (String) -> Unit)
    fun close()
}

interface SpeechToTextTool {
    interface Listener {
        fun onResult(text: String, isFinal: Boolean)
    }
    fun start(listener: Listener)
    fun stop()
    fun close()
}

class BlockService(val worker : Worker){//val context: Context) {

    fun textToSpeech(): TextToSpeechTool {
        val locale = Locale.getDefault()
        return TextToSpeechHandler(worker.applicationContext, locale)
    }

    fun translator(from: Locale, to: Locale): TranslationTool =
        TranslatorHandler(worker.applicationContext, from, to)

    fun speechToText(from: Locale = Locale.getDefault()): SpeechToTextTool =
        SpeechRecognizerHandler(worker.applicationContext, from)
}

class BlockServiceContext(val context: Context) {

    fun textToSpeech(lang : String): TextToSpeechTool {
        //val locale = Locale.getDefault()
        val locale = Locale(lang)
        return TextToSpeechHandler(context.applicationContext, locale)
    }

    fun translator(from: Locale, to: Locale): TranslationTool =
            TranslatorHandler(context.applicationContext, from, to)

    fun speechToText(from: Locale = Locale.getDefault()): SpeechToTextTool =
            SpeechRecognizerHandler(context.applicationContext, from)
}
