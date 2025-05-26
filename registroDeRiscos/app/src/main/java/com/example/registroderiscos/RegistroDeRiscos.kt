package com.example.registroderiscos

import Risco
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RegistroDeRiscos : AppCompatActivity() {

    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerNivel: Spinner
    private lateinit var btnFoto: Button
    private lateinit var editLocal: EditText
    private lateinit var editDescricao: EditText
    private lateinit var btnEnviar: Button
    private lateinit var btnCancelar: Button

    private var imagemBase64: String? = null

    // Localização
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val selecionarImagem = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                imagemBase64 = converterImagemParaBase64(it)
                Toast.makeText(this, "Imagem carregada e convertida!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_de_riscos)

        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerNivel = findViewById(R.id.spinnerNivel)
        btnFoto = findViewById(R.id.btnFoto)
        editLocal = findViewById(R.id.editLocal)
        editDescricao = findViewById(R.id.editDescricao)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnCancelar = findViewById(R.id.btnCancelar)

        val tipos = arrayOf("Químico", "Físico", "Ergonômico", "Biológico", "Outros")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        val niveis = arrayOf("1 - Baixo", "2 - Médio", "3 - Alto", "4 - Crítico", "5 - Fatal")
        spinnerNivel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, niveis)

        btnFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selecionarImagem.launch(intent)
        }

        btnEnviar.setOnClickListener {
            salvarNoFirebase()
        }

        btnCancelar.setOnClickListener {
            startActivity(Intent(this, RiscosRegistradosActivity::class.java))
            finish()
        }

        // Inicializa o serviço de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obterLocalizacao()
    }

    private fun obterLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacao()
        }
    }

    private fun converterImagemParaBase64(uri: Uri): String {
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream)
        val byteArray = stream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)

        return "data:$mimeType;base64,$base64"
    }

    private fun salvarNoFirebase() {
        val tipoRisco = spinnerTipo.selectedItem.toString()
        val nivelRisco = spinnerNivel.selectedItem.toString()
        val localizacao = editLocal.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()
        val imagem = imagemBase64 ?: ""

        if (descricao.isEmpty() || localizacao.isEmpty()) {
            Toast.makeText(this, "Preencha a descrição e a localização.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        val dataAtual = dateFormat.format(Date())

        val risco = Risco(
            descricao = descricao,
            foto = imagem,
            localizacao = localizacao,
            nivel_risco = nivelRisco,
            tipo_risco = tipoRisco,
            latitude = latitude,
            longitude = longitude,
            data = dataAtual // <-- Passe a data aqui
        )

        val referencia = FirebaseDatabase.getInstance().getReference("risco").child(userId)
        referencia.push().setValue(risco)
            .addOnSuccessListener {
                Toast.makeText(this, "Risco salvo com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, RiscosRegistradosActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
