package com.android.example.preguntas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PreguntasAdapter(private val categorias: List<Categoria>) :
    RecyclerView.Adapter<PreguntasAdapter.CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria)

        holder.itemView.setOnClickListener {
            categoria.expandido = !categoria.expandido
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = categorias.size

    inner class CategoriaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val categoriaText: TextView = view.findViewById(R.id.textCategoria)
        private val preguntasText: TextView = view.findViewById(R.id.textPreguntas)

        fun bind(categoria: Categoria) {
            categoriaText.text = categoria.nombre
            preguntasText.visibility = if (categoria.expandido) View.VISIBLE else View.GONE

            val preguntasFormatted = categoria.preguntas.joinToString("\n\n") {
                "Q: ${it.pregunta}\nA: ${it.respuesta}"
            }
            preguntasText.text = preguntasFormatted
        }
    }
}
