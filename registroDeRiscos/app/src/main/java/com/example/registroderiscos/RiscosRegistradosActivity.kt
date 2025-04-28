package com.example.registroderiscos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RiscosRegistradosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riscos_registrados)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollRiscoRegistrado)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnNovoRisco = findViewById<Button>(R.id.btnNovoRisco)

        btnNovoRisco.setOnClickListener {

            startActivity(Intent(this, RegistroDeRiscos::class.java))
            finish()
        }
    }
}