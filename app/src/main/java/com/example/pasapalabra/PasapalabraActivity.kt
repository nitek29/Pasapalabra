package com.example.pasapalabra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import fr.enssat.babelblock.ui.*
import kotlinx.android.synthetic.main.activity_tool_chain.*

class PasapalabraActivity : AppCompatActivity() {
    val handler = Handler(Looper.getMainLooper())
    val languages = arrayOf<String>("Afrikaans","Allemand","Anglais","Arabe","Assamese","Bangla","Bosniaque","Bulgare","Canara","Cantonais","Catalan","Chinois","Coréen","Croate","Danois","Dari","Espagnol","Estonien","Fidjien","Filipino","Finnois","Français","Gallois","Grec","Gujarâtî","Haïtien","Hébreu","Hindi","Hmonddaw","Hongrois","Indonésien","Irlandais","Islandais","Italien","Japonais","Kazakh","Klingon","Kurdish","Letton","Lituanien","Malaisien","Malayalam","Malgache","Maltais","Maori","Marathi","Néerlandais","Norvégien","Odia","Pastho","Perse","Polonais","Portugais","Punjabi","Querétaro Otomi","Roumain","Russe","Samoan","Serbe","Slovaque","Slovène","Suédois","Swahili","Tahitien","Tamil","Tchèque","Telugu","Thaï","Tongien","Turc","Ukrainien","Urdu","Vietnamien","Yucatec Maya")

    private fun getTool(ind : Int) =
        object : ToolDisplay {
            override var title  = languages[ind]
            override var output = ""
            override var input  = ""
            override val tool   = object : Tool {
                //override run method of Tool interface
                override fun run(input: String, output: (String) -> Unit) {
                    handler.postDelayed({output("$input $ind")},1000)
                }
                override fun close() {
                    Log.d(title, "close")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_chain)

        //create toolchain and its adapter
        val toolChain = ToolChain()
        val adapter = ToolChainAdapter(toolChain)

        //dedicated drag and drop mover helper
        val moveHelper = ToolChainMoveHelper.create(adapter)
        moveHelper.attachToRecyclerView(tool_chain_list)

        //see tool_chain_list in activity_tool_chain.xml
        //chain of tools
        tool_chain_list.adapter = adapter

        //see tool_list in activity_tool_chain.xml
        //simple list of ids
        tool_list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        tool_list.setOnItemClickListener { _, _, position, _ ->
            toolChain.add(getTool(position))
        }
    }

}