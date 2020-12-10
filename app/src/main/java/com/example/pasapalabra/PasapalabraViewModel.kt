package com.example.pasapalabra

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.pasapalabra.tools.SpeechToTextTool
import com.example.pasapalabra.tools.ui.ToolChain
import com.example.pasapalabra.workers.SpeechRecognizerWorker
import com.example.pasapalabra.workers.TextToSpeechWorker
import com.example.pasapalabra.workers.TranslatorWorker
import java.util.*
import kotlin.collections.ArrayList

class PasapalabraViewModel(application: Application) : AndroidViewModel(application) {
    internal var stt: String? = null
    internal var tt: String? = null
    internal var recognizer: String? = null
    internal  var lang_number : Int = 0
    private val workManager = WorkManager.getInstance(application)
    internal var workerList : ArrayList<LiveData<List<WorkInfo>>> = ArrayList()
    internal val outputWorkInfosSTT: LiveData<List<WorkInfo>>
    internal val outputWorkInfosTT: LiveData<List<WorkInfo>>
    internal val outputWorkInfosTTS: LiveData<List<WorkInfo>>

    // Add an init block to the PasapalabraViewModel class
    init {
        // This transformation makes sur that whenever the current work ID changes the WorkInfo
        // the UI is listening to changes
        outputWorkInfosSTT = workManager.getWorkInfosByTagLiveData(TAG_STT)
        outputWorkInfosTT = workManager.getWorkInfosByTagLiveData(TAG_TRANSLATOR)
        outputWorkInfosTTS = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    internal fun cancelWork() {
        //workManager.cancelUniqueWork(PASAPALABRA_WORK_NAME)
        workManager.cancelAllWork()
    }

    /**
     * Creates the input data bundle which includes the STRING to operate on
     * @return Data which contains the Speech to Text as a String
     */
    private fun createSTTInputData(language_src : String,language_dest : String, pos :Int): Data {
        val builder = Data.Builder()
        stt?.let {
            builder.putString(KEY_STT, stt)
        }

        builder.putString("LANG_SRC", language_src)
        if (pos == this.lang_number-2){
            builder.putString("LANG_OUT", language_dest)
            builder.putString("LANG_DST", "")
        }else {
            builder.putString("LANG_DST", language_dest)
        }
        return builder.build()
    }

    /**
     * Creates the input data bundle which includes the STRING to operate on
     * @return Data which contains the Text translation to as a String
     */
    private fun createTTInputData(langout : String): Data {
        val builder = Data.Builder()
        tt?.let {
            builder.putString(KEY_TT, tt)
        }
        builder.putString("LANG_OUT", langout)
        return builder.build()
    }

    /**
     * Creates the input data bundle which includes the STRING to operate on
     * @return Data which contains the Speech to text to as a String
     */
    private fun createRecognizerInputData(text : String): Data {
        val builder = Data.Builder()
        builder.putString(KEY_RECO, text)

        return builder.build()
    }

    private fun  speechToTextWork(text: String) : OneTimeWorkRequest {
        return OneTimeWorkRequest.Builder(SpeechRecognizerWorker::class.java).addTag(TAG_STT).setInputData(createRecognizerInputData(text)).build();
    }

    /**
     * Create the WorkRequest to apply the translation
     */
    internal fun applyTranslation(text: String, languages: ToolChain, speechToText: SpeechToTextTool) {
        speechToText.close()
        Log.d("ApplyTranslation", "Create workmanager")
        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager
                .beginUniqueWork(
                        PASAPALABRA_WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        speechToTextWork(text)
                        //OneTimeWorkRequest.from(SpeechRecognizerWorker::class.java)

                )

        this.lang_number = languages.size
        for ( i in 0 until this.lang_number -1){
Log.d("ViewModel","TAG_TRANSLATION : ${TAG_TRANSLATOR+i.toString()}\t langues ${languages.size}")
            val translatorBuilder = OneTimeWorkRequestBuilder<TranslatorWorker>()
                    .addTag(TAG_TRANSLATOR+i.toString())
            translatorBuilder.setInputData(createSTTInputData(languages.get(i).code,languages.get(i+1).code,i))
            var ttWorker = translatorBuilder.build()
            this.workerList.add(workManager.getWorkInfosByTagLiveData(TAG_TRANSLATOR+i.toString()))
            continuation = continuation.then(ttWorker)
        }


        // Add WorkRequest to launch TextToSpeech

        val textToSpeechBuilder = OneTimeWorkRequestBuilder<TextToSpeechWorker>()
                .addTag(TAG_OUTPUT)
                .setInputData(createTTInputData(languages.get(this.lang_number-1).code))
                .build()
        continuation = continuation.then(textToSpeechBuilder)

        // Actually start the work
        continuation.enqueue()
        Log.d("ApplyTranslation", "Start WorkManager")

    }


}