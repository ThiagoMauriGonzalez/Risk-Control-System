package com.example.registroderiscos

import android.os.Bundle
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class CadastroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollCadastro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val cpfInput = findViewById<TextInputLayout>(R.id.editCPF)

        //aplica a máscara no campo CPF
        cpfInput.editText?.addTextChangedListener(cpfMaskWatcher())

        btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                // Ir para a tela de login
                startActivity(Intent(this, MainActivity::class.java))
                finish() // fecha a tela de cadastro
            }
        }

        btnCancelar.setOnClickListener {
            // Volta para a tela de login
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
