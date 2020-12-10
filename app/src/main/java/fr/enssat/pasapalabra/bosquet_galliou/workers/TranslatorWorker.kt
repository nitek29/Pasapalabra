package fr.enssat.pasapalabra.bosquet_galliou.workers

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import fr.enssat.pasapalabra.bosquet_galliou.tools.BlockService
import fr.enssat.pasapalabra.bosquet_galliou.tools.TranslationTool
import fr.enssat.pasapalabra.bosquet_galliou.KEY_STT
import fr.enssat.pasapalabra.bosquet_galliou.KEY_TT
import timber.log.Timber
import java.lang.Thread.sleep
import java.util.*

class TranslatorWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private var translated: String? = null
    private var tts_flag: Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun doWork(): Result {


        Log.d("Translator Worker", "Start Translator Worker")
        val appContext = applicationContext
        lateinit var translator: TranslationTool

        val stt = inputData.getString(KEY_STT)
        Log.d("Translator Worker", "stt =$stt")

        val src = inputData.getString("LANG_SRC")
        var dst = inputData.getString("LANG_DST")
        if (dst == "") {
            this.tts_flag = true
            dst = inputData.getString("LANG_OUT")
            Log.d("Translator Worker", "Lang output : $dst")

        }

        val service = BlockService(this)
        Log.d("Translator Worker", "Lang src : ${Locale(src)} \t Lang dst : ${Locale(dst)}")
        translator = service.translator(Locale(src), Locale(dst))
        sleep(1500)

        val outputData: Data

        return try {

            if (TextUtils.isEmpty(stt)) {
                Timber.e("Invalid input stt")
                throw IllegalArgumentException("Invalid input stt")
            }
            var tt: String? = null
            translator.translate(stt.toString()) { enText ->
                this.translated = enText
                tt = enText
            }
            sleep(1000)

            if (this.translated.isNullOrEmpty()) {
                this.translated = tt
            }

            Log.d("Translator Worker", "Translation result " + this.translated)
            translator.close()
            outputData = if (this.tts_flag) {
                workDataOf(KEY_TT to this.translated)
            } else {
                workDataOf(KEY_STT to this.translated)
            }

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            return Result.failure()
        }
    }
}