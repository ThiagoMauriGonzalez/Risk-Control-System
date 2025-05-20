package com.example.registroderiscos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        auth = FirebaseAuth.getInstance()

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val cpfInput = findViewById<TextInputLayout>(R.id.editCPF)

        // Máscara no CPF
        cpfInput.editText?.addTextChangedListener(cpfMaskWatcher())

        btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                val nome = findViewById<TextInputLayout>(R.id.editNome).editText?.text.toString().trim()
                val email = findViewById<TextInputLayout>(R.id.editEmail).editText?.text.toString().trim()
                val cpf = findViewById<TextInputLayout>(R.id.editCPF).editText?.text.toString().trim()
                val senha = findViewById<TextInputLayout>(R.id.editSenha).editText?.text.toString().trim()
                val setor = findViewById<TextInputLayout>(R.id.editSetor).editText?.text.toString().trim()

                // Verifica se já existe usuário com o mesmo CPF
                val database = FirebaseDatabase.getInstance()
                val ref = database.getReference("usuario")
                ref.orderByChild("cpf").equalTo(cpf)
                    .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                        override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(this@CadastroActivity, "Já existe um usuário cadastrado com este CPF.", Toast.LENGTH_LONG).show()
                            } else {
                                // CPF não existe, pode cadastrar
                                auth.createUserWithEmailAndPassword(email, senha)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val userId = auth.currentUser?.uid ?: ""
                                            val userData = mapOf(
                                                "nome" to nome,
                                                "email" to email,
                                                "cpf" to cpf,
                                                "setor" to setor,
                                                "user_id" to userId
                                            )
                                            ref.child(userId).setValue(userData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this@CadastroActivity, "Cadastro salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                                    auth.signOut()
                                                    startActivity(Intent(this@CadastroActivity, MainActivity::class.java))
                                                    finish()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(this@CadastroActivity, "Erro ao salvar: ${it.message}", Toast.LENGTH_LONG).show()
                                                }
                                        } else {
                                            Toast.makeText(this@CadastroActivity, "Erro ao cadastrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }
                        }

                        override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                            Toast.makeText(this@CadastroActivity, "Erro ao verificar CPF: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }

        btnCancelar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun cpfMaskWatcher(): TextWatcher {
        var isUpdating = false
        val mask = "###.###.###-##"
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                isUpdating = true
                val str = s.toString().filter { it.isDigit() }
                var cpfComMascara = ""
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#') {
                        cpfComMascara += m
                        continue
                    }
                    if (i >= str.length) break
                    cpfComMascara += str[i]
                    i++
                }
                s?.replace(0, s.length, cpfComMascara)
                isUpdating = false
            }
        }
    }

    private fun validarCampos(): Boolean {
        val nomeInput = findViewById<TextInputLayout>(R.id.editNome)
        val emailInput = findViewById<TextInputLayout>(R.id.editEmail)
        val cpfInput = findViewById<TextInputLayout>(R.id.editCPF)
        val senhaInput = findViewById<TextInputLayout>(R.id.editSenha)
        val setorInput = findViewById<TextInputLayout>(R.id.editSetor)

        val nome = nomeInput.editText?.text.toString().trim()
        val email = emailInput.editText?.text.toString().trim()
        val cpf = cpfInput.editText?.text.toString().trim()
        val senha = senhaInput.editText?.text.toString().trim()
        val setor = setorInput.editText?.text.toString().trim()

        var valido = true

        if (nome.isEmpty()) {
            nomeInput.error = "Informe seu nome completo"
            valido = false
        } else {
            nomeInput.error = null
        }

        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            emailInput.error = "Informe um e-mail válido"
            valido = false
        } else {
            emailInput.error = null
        }

        if (!isCpfValid(cpf)) {
            cpfInput.error = "CPF inválido"
            valido = false
        } else {
            cpfInput.error = null
        }

        if (senha.isEmpty()) {
            senhaInput.error = "Informe uma senha"
            valido = false
        } else {
            senhaInput.error = null
        }

        if (setor.isEmpty()) {
            setorInput.error = "Informe seu setor ou local de trabalho"
            valido = false
        } else {
            setorInput.error = null
        }

        return valido
    }

    private fun isCpfValid(cpf: String): Boolean {
        val cleanCpf = cpf.filter { it.isDigit() }

        if (cleanCpf.length != 11 || cleanCpf.all { it == cleanCpf[0] }) return false

        try {
            val numbers = cleanCpf.map { it.toString().toInt() }

            val dv1 = (0..8).map { (10 - it) * numbers[it] }.sum().let {
                val resto = it % 11
                if (resto < 2) 0 else 11 - resto
            }

            val dv2 = (0..9).map { (11 - it) * numbers[it] }.sum().let {
                val resto = it % 11
                if (resto < 2) 0 else 11 - resto
            }

            return dv1 == numbers[9] && dv2 == numbers[10]
        } catch (e: Exception) {
            return false
        }
    }
}