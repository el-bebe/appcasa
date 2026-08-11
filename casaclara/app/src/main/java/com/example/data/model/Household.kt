package com.example.data.model

data class Household(
    val id: String = "CASACLARA_DEMO",
    val name: String = "Casa de Sofi y Ale",
    val code: String = "CLARA7",
    val members: List<String> = listOf("Sofi", "Ale"),
    val createdAt: Long = System.currentTimeMillis()
)
