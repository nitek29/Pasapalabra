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
const val KEY_SRC = "KEY_SRC"
const val KEY_TARGET = "KEY_TARGET"

val LANGUAGES = mapOf<String,String>(
                "af" to	"Afrikaans",
                "am"	to "Amharic",
                "ar" to	"Arabic",
                "ar-Latn" to	"Arabic",
                "az" to "Azerbaijani",
                "be" to "Belarusian",
                "bg" to "Bulgarian",
                "bg-Latn" to "Bulgarian",
                "bn" to "Bengali",
                "bs" to "Bosnian",
                "ca" to "Catalan",
                "ceb" to "Cebuano",
                "co" to "Corsican",
                "cs" to "Czech",
                "cy" to "Welsh",
                "da" to "Danish",
                "de" to "German",
                "el" to "Greek",
                "el-Latn" to "Greek",
                "en" to "English",
                "eo" to "Esperanto",
                "es" to "Spanish",
                "et" to "Estonian",
                "eu" to "Basque",
                "fa" to "Persian",
                "fi" to "Finnish",
                "fil" to "Filipino",
                "fr"  to "French",
                "fy"  to "Western Frisian",
                "ga"  to "Irish",
                "gd"  to "Scots Gaelic",
                "gl"  to "Galician",
                "gu"  to "Gujarati",
                "ha"  to "Hausa",
                "haw"  to "Hawaiian",
                "he"  to "Hebrew",
                "hi"  to "Hindi",
                "hi-Latn"  to "Hindi",
                "hmn"  to "Hmong",
                "hr"  to "Croatian",
                "ht"  to "Haitian",
                "hu"  to "Hungarian",
                "hy"  to "Armenian",
                "id"  to "Indonesian",
                "ig"  to "Igbo",
                "is"  to "Icelandic",
                "it"  to "Italian",
                "ja"  to "Japanese",
                "ja-Latn"  to "Japanese",
                "jv"  to "Javanese",
                "ka"  to "Georgian",
                "kk"  to "Kazakh",
                "km"  to "Khmer",
                "kn"  to "Kannada",
                "ko"  to "Korean",
                "ku"  to "Kurdish",
                "ky"  to "Kyrgyz",
                "la"  to "Latin",
                "lb"  to "Luxembourgish",
                "lo"  to "Lao",
                "lt"  to "Lithuanian",
                "lv"  to "Latvian",
                "mg"  to "Malagasy",
                "mi"  to "Maori",
                "mk"  to "Macedonian",
                "ml"  to "Malayalam",
                "mn"  to "Mongolian",
                "mr"  to "Marathi",
                "ms"  to "Malay",
                "mt"  to "Maltese",
                "my"  to "Burmese",
                "ne"  to "Nepali",
                "nl"  to "Dutch",
                "no"  to "Norwegian",
                "ny"  to "Nyanja",
                "pa"  to "Punjabi",
                "pl"  to "Polish",
                "ps"  to "Pashto",
                "pt"  to "Portuguese",
                "ro"  to "Romanian",
                "ru"  to "Russian",
                "ru-Latn"  to "Russian",
                "sd"  to "Sindhi",
                "si"  to "Sinhala",
                "sk"  to "Slovak",
                "sl"  to "Slovenian",
                "sm"  to "Samoan",
                "sn"  to "Shona",
                "so"  to "Somali",
                "sq"  to "Albanian",
                "sr"  to "Serbian",
                "st"  to "Sesotho",
                "su"  to "Sundanese",
                "sv"  to "Swedish",
                "sw"  to "Swahili",
                "ta"  to "Tamil",
                "te"  to "Telugu",
                "tg"  to "Tajik",
                "th"  to "Thai",
                "tr"  to "Turkish",
                "uk"  to "Ukrainian",
                "ur"  to "Urdu",
                "uz"  to "Uzbek",
                "vi"  to "Vietnamese",
                "xh"  to "Xhosa",
                "yi"  to "Yiddish",
                "yo"  to "Yoruba",
                "zh"  to "Chinese",
                "zh-Latn"  to "Chinese",
                "zu"  to "Zulu",
)