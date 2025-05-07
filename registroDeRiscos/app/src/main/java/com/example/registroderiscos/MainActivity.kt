package com.example.registroderiscos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val emailLayout = findViewById<TextInputLayout>(R.id.editEmail)
        val passwordLayout = findViewById<TextInputLayout>(R.id.editSenha)
        emailInput = emailLayout.editText as TextInputEditText
        passwordInput = passwordLayout.editText as TextInputEditText
        loginButton = findViewById(R.id.button)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateInputs(email, password)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login bem-sucedido
                            startActivity(Intent(this, RiscosRegistradosActivity::class.java))
                            finish()
                        } else {
                            // Falha no login (senha errada, usuário não existe, etc)
                            Toast.makeText(this, "E-mail ou senha incorretos.", Toast.LENGTH_SHORT).show()
                        }
                    }
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
}