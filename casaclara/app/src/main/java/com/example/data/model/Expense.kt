package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Expense(
    val id: String = "",
    val amount: Double = 0.0,
    val category: ExpenseCategory = ExpenseCategory.SUPERMARKET,
    val paidBy: String = "Sofi",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val monthKey: String = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(timestamp))
) {
    fun formattedDate(): String {
        val sdf = SimpleDateFormat("dd 'de' MMMM", Locale("es", "ES"))
        return sdf.format(Date(timestamp)).capitalize(Locale("es", "ES"))
    }

    fun formattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
