package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val displayName: String,
    val emoji: String,
    val icon: ImageVector,
    val color: Color,
    val darkColor: Color
) {
    SUPERMARKET("Comida", "🛒", Icons.Default.ShoppingCart, Color(0xFFE08963), Color(0xFFFFBFA3)),
    CLEANING("Limpieza", "🧹", Icons.Default.CleaningServices, Color(0xFFD6C2B0), Color(0xFFFAF2EB)),
    SERVICES("Servicios", "💡", Icons.Default.Lightbulb, Color(0xFF5E96AE), Color(0xFF90C2D8)),
    ENTERTAINMENT("Ocio", "🍿", Icons.Default.Movie, Color(0xFFFFBFA3), Color(0xFFFFD8C7)),
    TRANSPORT("Transporte", "🚗", Icons.Default.DirectionsCar, Color(0xFF4EA8DE), Color(0xFF80C2ED)),
    OTHER("Otros", "🏠", Icons.Default.HomeWork, Color(0xFFB5838D), Color(0xFFD4AAB2));

    companion object {
        fun fromString(name: String): ExpenseCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) }
                ?: OTHER
        }
    }
}
