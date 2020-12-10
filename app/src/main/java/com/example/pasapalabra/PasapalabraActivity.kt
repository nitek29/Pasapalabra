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
import android.widget.ListAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.example.pasapalabra.tools.BlockServiceContext
import com.example.pasapalabra.tools.SpeechToTextTool
import com.example.pasapalabra.tools.ui.*
import kotlinx.android.synthetic.main.activity_tool_chain.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer

class PasapalabraActivity : AppCompatActivity() {
    val handler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: PasapalabraViewModel
    private val RecordAudioRequestCode = 1

    //create toolchain and its adapter
    private var toolChain = ToolChain()
    private var tool_chain_list_adapter = ToolChainAdapter(this.toolChain)
    lateinit var tool_list_adapter: ListAdapter

    private fun getTool(ind: Int) =
            object : ToolDisplay {
                override var title = ArrayList(LANGUAGES.values)[ind]
                override var output = ""
                override var code = ArrayList(LANGUAGES.keys)[ind]
                override var input = ""
                override val tool = object : Tool {
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
        val service = BlockServiceContext(this)
        //speechToText = service.speechToText()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_chain)

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
        this.tool_chain_list_adapter = adapter

        // description
        description.visibility = View.VISIBLE

        //see tool_list in activity_tool_chain.xml
        //simple list of ids
        tool_list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList(LANGUAGES.values))
        this.tool_list_adapter = tool_list.adapter
        tool_list.setOnItemClickListener { _, _, position, _ ->
            if (toolChain.size == 0) {
                toolChain.add(getTool(position))
            } else if (toolChain.size == 1) {
                description.visibility = View.GONE
                record_button.visibility = View.VISIBLE
                toolChain.add(getTool(position))
            } else {
                toolChain.add(getTool(position))
            }
        }

        // gestion bouton enregistrement message
        record_button.visibility = View.GONE
        record_button.setOnTouchListener { v, event ->
            this.toolChain = toolChain
            // Arrête les workers en cours avant de lancer les nouveaux
            viewModel.cancelWork()

            Log.d("Activity", "toolchain size ${this.toolChain.size}")
            // mise en place des observers sur les workers
            viewModel.outputWorkInfosSTT.observe(this, workInfosObserver(this.toolChain, TAG_STT))
            //viewModel.outputWorkInfosTT.observe(this, workInfosObserver(this.toolChain, TAG_TRANSLATOR))
            viewModel.outputWorkInfosTTS.observe(this, workInfosObserver(this.toolChain, TAG_OUTPUT))
            for (i in 0 until viewModel.workerList.size-1) {
                viewModel.workerList[i].observe(this, workInfosObserver(this.toolChain, TAG_TRANSLATOR+i.toString()))
            }
            viewModel.workerList.clear()

            speechToText = service.speechToText(Locale(toolChain.get(0).code))

            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d("Reco UI", "Button pressed")
                v.performClick()
                speechToText.start(object : SpeechToTextTool.Listener {
                    override fun onResult(text: String, isFinal: Boolean) {
                        if (isFinal) {
                            viewModel.applyTranslation(text, toolChain, speechToText)
                        }
                    }
                })
            } else if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Reco UI", "Button releases")
                speechToText.stop()
                //viewModel.applyTranslation(stt)


            }
            false
        }

        // Get the ViewModel
        viewModel = ViewModelProvider(this).get(PasapalabraViewModel::class.java)


    }

    private fun workInfosObserver(toolchain: ToolChain, tag: String): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            for (workInfo in listOfWorkInfo.indices) {
                var pos = 0
                if (listOfWorkInfo[workInfo].state.isFinished) {
                    // Normally this processing, which is not directly related to drawing views on
                    // screen would be in the ViewModel. For simplicity we are keeping it here.
                    var outputText: String? = ""
                    if (TAG_TRANSLATOR in tag)  {
                            outputText = listOfWorkInfo[workInfo].outputData.getString(KEY_STT)
                            if (!outputText.isNullOrEmpty()) {
                                pos = tag.split(TAG_TRANSLATOR)[1].toInt()
                                Log.d("Activity", "TAG $tag \tpos ${(pos+1)}")
                                toolchain.get(pos + 1).output = outputText
                                Log.d("Activity", "TAG : $tag\t workinfo :${pos + 1} \toutput : ${outputText} \t lang ${toolchain.get(pos + 1).title}\t output : ${toolchain.get(pos + 1).output} ")
                            }
                        }else if (TAG_STT in tag) {
                            outputText = listOfWorkInfo[workInfo].outputData.getString(KEY_STT)
                            if (!outputText.isNullOrEmpty()) {
                                toolchain.get(0).output = outputText
                                Log.d("Activity", "TAG : $tag\t workinfo :0\t output : ${outputText} \t lang ${toolchain.get(0).title}\t output : ${toolchain.get(0).output} ")
                            }

                        }else if (TAG_OUTPUT in tag){
                            outputText = listOfWorkInfo[workInfo].outputData.getString(KEY_TTS)
                            if (!outputText.isNullOrEmpty()) {
                                toolchain.get(toolchain.size - 1).output = outputText
                                Log.d("Activity", "TAG : $tag\t workinfo : ${toolchain.size - 1}\t output : ${outputText} \t lang ${toolchain.get(toolchain.size - 1).title}\t output : ${toolchain.get(toolchain.size - 1).output} ")
                            }
                        }
                    }
                    tool_chain_list.adapter = this.tool_chain_list_adapter
            }
        }
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