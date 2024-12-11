package com.example.luxeinn

data class Pregunta(val pregunta: String, val respuesta: String)
data class Categoria(val nombre: String, val preguntas: List<Pregunta>, var expandido: Boolean = false)