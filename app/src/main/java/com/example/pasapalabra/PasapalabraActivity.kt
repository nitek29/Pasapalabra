package com.example.pasapalabra

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.pasapalabra.tools.BlockService
import com.example.pasapalabra.tools.SpeechToTextTool
import kotlinx.android.synthetic.main.activity_speech_to_text.*
import kotlinx.android.synthetic.main.activity_speech_to_text.record_button
import kotlinx.android.synthetic.main.activity_tool_chain.*

class PasapalabraActivity : AppCompatActivity() {
    val handler = Handler(Looper.getMainLooper())

    // Liste des langues disponibles de l'application
    val languages = arrayOf<String>(
            "Afrikaans",
            "Allemand",
            "Anglais",
            "Arabe",
            "Assamese",
            "Bangla",
            "Bosniaque",
            "Bulgare",
            "Canara",
            "Cantonais",
            "Catalan",
            "Chinois",
            "Coréen",
            "Croate",
            "Danois",
            "Dari",
            "Espagnol",
            "Estonien",
            "Fidjien",
            "Filipino",
            "Finnois",
            "Français",
            "Gallois",
            "Grec",
            "Gujarâtî",
            "Haïtien",
            "Hébreu",
            "Hindi",
            "Hmonddaw",
            "Hongrois",
            "Indonésien",
            "Irlandais",
            "Islandais",
            "Italien",
            "Japonais",
            "Kazakh",
            "Klingon",
            "Kurdish",
            "Letton",
            "Lituanien",
            "Malaisien",
            "Malayalam",
            "Malgache",
            "Maltais",
            "Maori",
            "Marathi",
            "Néerlandais",
            "Norvégien",
            "Odia",
            "Pastho",
            "Perse",
            "Polonais",
            "Portugais",
            "Punjabi",
            "Querétaro Otomi",
            "Roumain",
            "Russe",
            "Samoan",
            "Serbe",
            "Slovaque",
            "Slovène",
            "Suédois",
            "Swahili",
            "Tahitien",
            "Tamil",
            "Tchèque",
            "Telugu",
            "Thaï",
            "Tongien",
            "Turc",
            "Ukrainien",
            "Urdu",
            "Vietnamien",
            "Yucatec Maya"
    )

    private lateinit var viewModel : PasapalabraViewModel
    private val RecordAudioRequestCode = 1

    private fun getTool(ind: Int, language: String) =
            object : ToolDisplay {
                override var title  = languages[ind]
                override var output = ""
                override var input  = "Langue $language"
                override val tool   = object : Tool {
                    //override run method of Tool interface
                    override fun run(input: String, output: (String) -> Unit) {
                        handler.postDelayed({ output("$input $ind") }, 1000)
                    }
                    override fun close() {
                        Log.d(title, "close")
                    }
                }
            }

    lateinit var speechToText: SpeechToTextTool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_chain)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        // gestion permission mirophone
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        //create toolchain and its adapter
        val toolChain = ToolChain()
        val adapter = ToolChainAdapter(toolChain)

        //dedicated drag and drop mover helper
        val moveHelper = ToolChainMoveHelper.create(adapter)
        moveHelper.attachToRecyclerView(tool_chain_list)

        //see tool_chain_list in activity_tool_chain.xml
        //chain of tools
        tool_chain_list.adapter = adapter

        // description
        description.visibility = View.VISIBLE

        //see tool_list in activity_tool_chain.xml
        //simple list of ids
        tool_list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        tool_list.setOnItemClickListener { _, _, position, _ ->
            if (toolChain.size == 0) {
                toolChain.add(getTool(position, "source"))
            } else if (toolChain.size == 1) {
                description.visibility = View.GONE
                record_button.visibility = View.VISIBLE
                toolChain.add(getTool(position, "cible"))
            } else {
                toolChain.add(getTool(position, "cible"))
            }
        }
        // gestion bouton enregistrement message
        record_button.visibility = View.GONE
        record_button.setOnTouchListener { v, event ->
            var stt : String
            stt =""
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d("Reco UI", "Button pressed")
                v.performClick()
                speechToText.start(object : SpeechToTextTool.Listener {
                    override fun onResult(text: String, isFinal: Boolean) {
                        if (isFinal) {  stt = text}
                    }
                })
            } else if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Reco UI", "Button releases")
                viewModel.applyTranslation(stt)


            }
            false
        }

        // Get the ViewModel
        viewModel = ViewModelProvider(this).get(PasapalabraViewModel::class.java)
    }
    private fun checkPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}