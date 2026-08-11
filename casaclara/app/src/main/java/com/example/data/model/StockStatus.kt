package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class StockStatus(
    val label: String,
    val emoji: String,
    val color: Color,
    val order: Int
) {
    HAY("Hay", "🟢", Color(0xFF5E96AE), 0),
    QUEDA_POCO("Queda poco", "🟡", Color(0xFFE08963), 1),
    FALTA("Falta", "🔴", Color(0xFFE55353), 2);

    fun nextState(): StockStatus {
        return when (this) {
            HAY -> QUEDA_POCO
            QUEDA_POCO -> FALTA
            FALTA -> HAY
        }
    }

    companion object {
        fun fromString(value: String): StockStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: HAY
        }
    }
}
