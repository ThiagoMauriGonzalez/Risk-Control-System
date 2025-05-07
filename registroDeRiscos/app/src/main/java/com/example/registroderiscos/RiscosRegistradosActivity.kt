package com.example.registroderiscos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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
        riscoAdapter = RiscoAdapter(listaRiscos)
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

    private fun carregarRiscosFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val referencia = FirebaseDatabase.getInstance().getReference("risco").child(userId!!)

        referencia.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaRiscos.clear()
                for (riscoSnapshot in snapshot.children) {
                    val risco = riscoSnapshot.getValue(Risco::class.java)
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