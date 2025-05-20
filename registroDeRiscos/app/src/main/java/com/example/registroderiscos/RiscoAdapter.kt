package com.example.registroderiscos

import Risco
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiscoAdapter(
    private val riscos: List<Risco>,
    private val onExcluirClick: (Risco) -> Unit,
    private val onEditarClick: (Risco) -> Unit,
    private val onDetalheClick: (Risco) -> Unit
) : RecyclerView.Adapter<RiscoAdapter.RiscoViewHolder>() {

    class RiscoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipoRisco: TextView = itemView.findViewById(R.id.tvTipoRisco)
        val tvDetalhesRisco: TextView = itemView.findViewById(R.id.tvDetalhesRisco)
        val btnExcluir: Button = itemView.findViewById(R.id.btnExcluirRisco)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditarRisco)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiscoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_risco, parent, false)
        return RiscoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiscoViewHolder, position: Int) {
        val risco = riscos[position]
        val latLng = if (risco.latitude != null && risco.longitude != null) {
            "Lat: %.5f, Lon: %.5f".format(risco.latitude, risco.longitude)
        } else {
            "Localização não disponível"
        }
        holder.tvTipoRisco.text = risco.tipo_risco
        holder.tvDetalhesRisco.text = "${risco.localizacao} · ${risco.nivel_risco} · $latLng"

        holder.btnExcluir.setOnClickListener { onExcluirClick(risco) }
        holder.btnEditar.setOnClickListener { onEditarClick(risco) }
        holder.itemView.setOnClickListener { onDetalheClick(risco) } // Clique no quadrado inteiro
    }

    override fun getItemCount(): Int = riscos.size
}