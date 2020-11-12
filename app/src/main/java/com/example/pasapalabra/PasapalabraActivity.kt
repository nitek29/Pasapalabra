package com.example.pasapalabra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

class PasapalabraActivity : AppCompatActivity() {
    private lateinit var viewModel : PasapalabraViewModel
    //private lateinit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the ViewModel
        viewModel = ViewModelProvider(this).get(PasapalabraViewModel::class.java)
    }
}