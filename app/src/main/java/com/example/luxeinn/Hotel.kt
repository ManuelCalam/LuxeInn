package com.example.luxeinn

data class Hotel(
    val name: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val info: String = "",
    val imageUrl: String = "",
    val rooms: Map<String, Room> = mapOf(),
    val services: List<String> = listOf(),
    val available: Boolean = true
)