@file:JvmName("Constants")
package com.example.pasapalabra

// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
        "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
@JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

// The name of the image manipulation work
const val PASAPALABRA_WORK_NAME = "pasapalabra_work"

// Others keys
const val TAG_TRANSLATOR = "TRANSLATOR"
const val TAG_OUTPUT = "OUTPUT"
const val KEY_STT = "KEY_STT"
const val  KEY_TT = "KEY_TT"
const val  KEY_RECO = "KEY_RECO"