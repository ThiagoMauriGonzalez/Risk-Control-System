    package com.example.registroderiscos

    import android.app.Activity
    import android.content.Intent
    import android.graphics.Bitmap
    import android.net.Uri
    import android.os.Bundle
    import android.provider.MediaStore
    import android.util.Base64
    import android.widget.*
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.FirebaseDatabase
    import java.io.ByteArrayOutputStream

    class RegistroDeRiscos : AppCompatActivity() {

        private lateinit var spinnerTipo: Spinner
        private lateinit var spinnerNivel: Spinner
        private lateinit var btnFoto: Button
        private lateinit var editLocal: EditText
        private lateinit var editDescricao: EditText
        private lateinit var btnEnviar: Button
        private lateinit var btnCancelar: Button

        private var imagemBase64: String? = null

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

            val niveis = arrayOf("1 - Baixo", "2", "3", "4", "5 - Crítico")
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

            // Crie o objeto Risco usando a data class
            val risco = Risco(
                descricao = descricao,
                foto = imagem,
                localizacao = localizacao,
                nivel_risco = nivelRisco,
                tipo_risco = tipoRisco
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
