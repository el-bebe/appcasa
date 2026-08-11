package com.example.data.model

data class PantryItem(
    val id: String = "",
    val name: String = "",
    val category: String = "Almacén",
    val iconEmoji: String = "📦",
    val status: StockStatus = StockStatus.HAY,
    val lastUpdatedBy: String = "Sistema",
    val estimatedPrice: Double = 1500.0,
    val isCustom: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
