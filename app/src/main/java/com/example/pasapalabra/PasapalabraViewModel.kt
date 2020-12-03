package com.example.pasapalabra

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.pasapalabra.workers.SpeechRecognizerWorker
import com.example.pasapalabra.workers.TextToSpeechWorker
import com.example.pasapalabra.workers.TranslatorWorker

class PasapalabraViewModel(application: Application) : AndroidViewModel(application) {
    internal var stt: String? = null
    internal var tt: String? = null
    internal var recognizer: String? = null
    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    // Add an init block to the PasapalabraViewModel class
    init {
        // This transformation makes sur that whenever the current work ID changes the WorkInfo
        // the UI is listening to changes
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    internal fun cancelWork() {
        workManager.cancelUniqueWork(PASAPALABRA_WORK_NAME)
    }

    /**
     * Creates the input data bundle which includes the STRING to operate on
     * @return Data which contains the Speech to Text as a String
     */
    private fun createSTTInputData(): Data {
        val builder = Data.Builder()
        stt?.let {
            builder.putString(KEY_STT, stt)
        }
        return builder.build()
    }

    /**
     * Creates the input data bundle which includes the STRING to operate on
     * @return Data which contains the Text translation to as a String
     */
    private fun createTTInputData(): Data {
        val builder = Data.Builder()
        tt?.let {
            builder.putString(KEY_TT, tt)
        }
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
        return OneTimeWorkRequest.Builder(SpeechRecognizerWorker::class.java).setInputData(createRecognizerInputData(text)).build();
    }

    /**
     * Create the WorkRequest to apply the translation
     */
    internal fun applyTranslation(text :String) {
        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager
                .beginUniqueWork(
                        PASAPALABRA_WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        speechToTextWork(text)
                        //OneTimeWorkRequest.from(SpeechRecognizerWorker::class.java)

                )

        val translatorBuilder = OneTimeWorkRequestBuilder<TranslatorWorker>()
                .addTag(TAG_TRANSLATOR)
        translatorBuilder.setInputData(createSTTInputData())

        continuation = continuation.then(translatorBuilder.build())

        // Add WorkRequest to launch TextToSpeech

        val textToSpeechBuilder = OneTimeWorkRequestBuilder<TextToSpeechWorker>()
                .addTag(TAG_OUTPUT)
                .setInputData(createTTInputData())
                .build()
        continuation = continuation.then(textToSpeechBuilder)

        // Actually start the work
        continuation.enqueue()


    }
}