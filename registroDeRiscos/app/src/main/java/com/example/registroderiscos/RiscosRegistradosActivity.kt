package com.example.registroderiscos

import Risco
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class RiscosRegistradosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var riscoAdapter: RiscoAdapter
    private val listaRiscos = mutableListOf<Risco>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riscos_registrados)

        recyclerView = findViewById(R.id.recyclerViewRiscos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        riscoAdapter = RiscoAdapter(
            listaRiscos,
            onExcluirClick = { risco -> excluirRisco(risco) },
            onEditarClick = { risco -> editarRisco(risco) },
            onDetalheClick = { risco -> mostrarDetalhesRisco(risco) }
        )
        recyclerView.adapter = riscoAdapter

        carregarRiscosFirebase()

        findViewById<Button>(R.id.btnNovoRisco).setOnClickListener {
            startActivity(Intent(this, RegistroDeRiscos::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnDeslogar).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun excluirRisco(risco: Risco) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && risco.id != null) {
            val referencia = FirebaseDatabase.getInstance()
                .getReference("risco")
                .child(userId)
                .child(risco.id!!)
            referencia.removeValue().addOnSuccessListener {
                Toast.makeText(this, "Risco excluído com sucesso", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao excluir risco", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editarRisco(risco: Risco) {
        val editText = EditText(this)
        editText.setText(risco.descricao)

        AlertDialog.Builder(this)
            .setTitle("Editar descrição")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, _ ->
                val novaDescricao = editText.text.toString()
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null && risco.id != null) {
                    val referencia = FirebaseDatabase.getInstance()
                        .getReference("risco")
                        .child(userId)
                        .child(risco.id!!)
                    referencia.child("descricao").setValue(novaDescricao)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Descrição atualizada!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao atualizar descrição", Toast.LENGTH_SHORT).show()
                        }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun mostrarDetalhesRisco(risco: Risco) {
        val detalhes = """
        Tipo: ${risco.tipo_risco}
        Descrição: ${risco.descricao}
        Localização: ${risco.localizacao}
        Nível: ${risco.nivel_risco}
        Data: ${risco.data}
        Latitude: ${risco.latitude ?: "N/A"}
        Longitude: ${risco.longitude ?: "N/A"}
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detalhes do Risco")
            .setMessage(detalhes)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun carregarRiscosFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val referencia = FirebaseDatabase.getInstance().getReference("risco").child(userId!!)

        referencia.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaRiscos.clear()
                for (riscoSnapshot in snapshot.children) {
                    val risco = riscoSnapshot.getValue(Risco::class.java)
                    // Garante que o id do risco seja preenchido
                    risco?.id = riscoSnapshot.key
                    risco?.let { listaRiscos.add(it) }
                }
                riscoAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RiscosRegistradosActivity, "Erro ao carregar dados: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}