package com.example.luxeinn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoriaAdapter(private val categorias: List<Categoria>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CATEGORIA = 0
    private val VIEW_TYPE_PREGUNTA = 1

    private val expandedCategories = mutableSetOf<Int>() // Para saber qué categorías están abiertas
    private val expandedQuestions = mutableSetOf<Pair<Int, Int>>() // Para saber qué preguntas están abiertas

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is Categoria) VIEW_TYPE_CATEGORIA else VIEW_TYPE_PREGUNTA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CATEGORIA) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_categoria, parent, false)
            CategoriaViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pregunta, parent, false)
            PreguntaViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is CategoriaViewHolder) {
            holder.bind(item as Categoria)
        } else if (holder is PreguntaViewHolder) {
            val (categoriaIndex, pregunta) = item as Pair<Int, Pregunta>
            holder.bind(categoriaIndex, pregunta)
        }
    }

    override fun getItemCount(): Int {
        return categorias.sumOf { categoria ->
            if (expandedCategories.contains(categorias.indexOf(categoria))) {
                1 + categoria.preguntas.size
            } else {
                1
            }
        }
    }

    private fun getItem(position: Int): Any {
        var currentPosition = 0
        for ((categoriaIndex, categoria) in categorias.withIndex()) {
            if (currentPosition == position) {
                return categoria
            }
            currentPosition++
            if (expandedCategories.contains(categoriaIndex)) {
                for (pregunta in categoria.preguntas) {
                    if (currentPosition == position) {
                        return Pair(categoriaIndex, pregunta)
                    }
                    currentPosition++
                }
            }
        }
        throw IndexOutOfBoundsException("Invalid position $position")
    }

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textCategoria: TextView = itemView.findViewById(R.id.textCategoria)
        private val iconCategoria: ImageView = itemView.findViewById(R.id.iconCategoria)

        fun bind(categoria: Categoria) {
            textCategoria.text = categoria.nombre

            // Configurar el ícono basado en el nombre de la categoría
            val iconRes = when (categoria.nombre) {
                "Registro y Perfil de Usuario" -> R.drawable.registro
                "Métodos de Pago" -> R.drawable.payment
                "Gestión de Reservas" -> R.drawable.travel
                "Soporte y Problemas" -> R.drawable.support
                else -> R.drawable.default_icon // Ícono por defecto
            }
            iconCategoria.setImageResource(iconRes)

            textCategoria.setOnClickListener {
                val categoriaIndex = categorias.indexOf(categoria)
                if (expandedCategories.contains(categoriaIndex)) {
                    expandedCategories.remove(categoriaIndex)
                } else {
                    expandedCategories.add(categoriaIndex)
                }
                notifyDataSetChanged()
            }
        }
    }


    inner class PreguntaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textPregunta: TextView = itemView.findViewById(R.id.textPregunta)
        private val textRespuesta: TextView = itemView.findViewById(R.id.textRespuesta)
        private val imageExpandIcon: ImageView = itemView.findViewById(R.id.imageExpandIcon)

        fun bind(categoriaIndex: Int, pregunta: Pregunta) {
            textPregunta.text = pregunta.pregunta
            textRespuesta.text = pregunta.respuesta

            val key = Pair(categoriaIndex, categorias[categoriaIndex].preguntas.indexOf(pregunta))
            val isExpanded = expandedQuestions.contains(key)

            // Mostrar u ocultar la respuesta
            textRespuesta.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Cambiar el ícono dependiendo del estado expandido
            imageExpandIcon.setImageResource(
                if (isExpanded) R.drawable.baseline_minimize_24 else R.drawable.baseline_add_24
            )

            // Manejar clics en la pregunta
            textPregunta.setOnClickListener {
                if (isExpanded) {
                    expandedQuestions.remove(key)
                } else {
                    expandedQuestions.add(key)
                }
                notifyDataSetChanged()
            }

            // Manejar clics en el ícono
            imageExpandIcon.setOnClickListener {
                if (isExpanded) {
                    expandedQuestions.remove(key)
                } else {
                    expandedQuestions.add(key)
                }
                notifyDataSetChanged()
            }
        }
    }




}
