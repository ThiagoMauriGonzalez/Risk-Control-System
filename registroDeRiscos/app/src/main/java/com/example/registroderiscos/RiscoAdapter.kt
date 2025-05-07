package com.example.registroderiscos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiscoAdapter(private val riscos: List<Risco>) :
    RecyclerView.Adapter<RiscoAdapter.RiscoViewHolder>() {

    class RiscoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipoRisco: TextView = itemView.findViewById(R.id.tvTipoRisco)
        val tvDetalhesRisco: TextView = itemView.findViewById(R.id.tvDetalhesRisco)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiscoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_risco, parent, false)
        return RiscoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiscoViewHolder, position: Int) {
        val risco = riscos[position]
        holder.tvTipoRisco.text = risco.tipo_risco
        holder.tvDetalhesRisco.text = "${risco.localizacao} · ${risco.nivel_risco}"
    }

    override fun getItemCount(): Int = riscos.size
}