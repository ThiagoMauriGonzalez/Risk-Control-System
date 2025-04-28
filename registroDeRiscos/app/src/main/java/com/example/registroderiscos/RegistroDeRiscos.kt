package com.example.registroderiscos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class RegistroDeRiscos : AppCompatActivity() {

    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerNivel: Spinner
    private lateinit var btnFoto: Button
    private lateinit var editLocal: EditText
    private lateinit var btnLocalizacao: Button

    private val selecionarImagem = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Imagem selecionada: ${it.lastPathSegment}", Toast.LENGTH_SHORT).show()
            // Aqui você pode usar o URI como quiser
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_de_riscos)

        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerNivel = findViewById(R.id.spinnerNivel)
        btnFoto = findViewById(R.id.btnFoto)
        editLocal = findViewById(R.id.editLocal)
        btnLocalizacao = findViewById(R.id.btnCancelar) // usando o botão Cancelar como exemplo para localização

        // Preencher spinner de Tipo
        val tipos = arrayOf("Químico", "Físico", "Ergonômico", "Biológico", "Outros")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        // Preencher spinner de Nível
        val niveis = arrayOf("1 - Baixo", "2", "3", "4", "5 - Crítico")
        spinnerNivel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, niveis)

        // Selecionar imagem
        btnFoto.setOnClickListener {
            selecionarImagem.launch("image/*")
        }

        // Pegar localização atual
        btnLocalizacao.setOnClickListener {
            obterLocalizacao()
        }
    }

    private fun obterLocalizacao() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        location?.let {
            val latitude = it.latitude
            val longitude = it.longitude
            editLocal.setText("Lat: $latitude\nLon: $longitude")
        } ?: run {
            Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
        }
    }
}
