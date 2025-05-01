package com.example.registroderiscos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailLayout = findViewById<TextInputLayout>(R.id.editEmail)
        val passwordLayout = findViewById<TextInputLayout>(R.id.editSenha)
        emailInput = emailLayout.editText as TextInputEditText
        passwordInput = passwordLayout.editText as TextInputEditText
        loginButton = findViewById(R.id.button)
        database = FirebaseDatabase.getInstance().getReference("usuario")

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val senha = passwordInput.text.toString().trim()

            if (validateInputs(email, senha)) {
                autenticarUsuario(email, senha)
            }
        }

        val textCadastro = findViewById<TextView>(R.id.textCadastro)
        textCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                Toast.makeText(this, "Por favor, insira seu e-mail.", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Por favor, insira sua senha.", Toast.LENGTH_SHORT).show()
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun autenticarUsuario(email: String, senha: String) {
        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val userSenha = userSnapshot.child("senha").value.toString()
                            if (userSenha == senha) {
                                // Login bem-sucedido
                                startActivity(Intent(this@MainActivity, RiscosRegistradosActivity::class.java))
                                finish()
                                return
                            }
                        }
                        Toast.makeText(this@MainActivity, "Senha incorreta.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Erro no login: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
