package com.example.registroderiscos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailLayout = findViewById<TextInputLayout>(R.id.editEmail)
        val passwordLayout = findViewById<TextInputLayout>(R.id.editSenha)

        emailInput = emailLayout.editText as TextInputEditText
        passwordInput = passwordLayout.editText as TextInputEditText
        loginButton = findViewById(R.id.button)

        loginButton.setOnClickListener {
            performLogin()
        }

        val textCadastro = findViewById<TextView>(R.id.textCadastro)
        textCadastro.setOnClickListener {
            // Redirecionar para outra tela de cadastro
            val intent = Intent(this, activityResultRegistry::class.java)
            startActivity(intent)
        }

    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()


        if (validateInputs(email, password)) {
            if (authenticateUser(email, password)) {

                // Obtendo o nome do usuário (parte antes do '@')
                val userName = email.substringBefore("@")

                // Passar o nome do usuário para a BoasVindasActivity
                val intent = Intent(this, activityResultRegistry::class.java)
                intent.putExtra("USERNAME", userName) // Adicionando extra ao Intent
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login falhou. Verifique suas credenciais.", Toast.LENGTH_SHORT).show()
            }
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

    private fun authenticateUser(email: String, password: String): Boolean {

        return email == "usuario@kotlin.com" && password == "1234"


    }
}