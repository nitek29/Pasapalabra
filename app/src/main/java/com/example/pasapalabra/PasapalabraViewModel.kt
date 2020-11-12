package com.example.pasapalabra

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager

class PasapalabraViewModel(application: Application) : AndroidViewModel(application) {
    internal var stt: String? = null
    internal var translate: String? = null
    private val workManager =  WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    // Add an init block to the PasapalabraViewModel class
    init {
        // This transformation makes sur that whenever the current work ID changes the WorkInfo
        // the UI is listening to changes
        outputWorkInfos =  workManager.getWorkInfosByTagLiveData("OUTPUT")
    }
}