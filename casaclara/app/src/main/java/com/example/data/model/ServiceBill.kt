package com.example.data.model

enum class ServiceType(
    val displayName: String,
    val defaultEmoji: String
) {
    LIGHT("Luz", "💡"),
    GAS("Gas", "🔥"),
    WATER("Agua", "💧"),
    INTERNET("Internet / Cable", "🌐"),
    STREAMING("Streaming / Suscripciones", "📺"),
    EXPENSES("Expensas", "🏢"),
    PHONE("Celular / Telefonía", "📱"),
    GYM("Gimnasio / Club", "🏋️"),
    INSURANCE("Seguro", "🛡️"),
    TAXES("Impuestos / Tasas", "📑"),
    OTHER("Otros servicios", "⚙️");

    companion object {
        fun fromName(name: String): ServiceType {
            return entries.firstOrNull { 
                it.displayName.equals(name, ignoreCase = true) || it.name.equals(name, ignoreCase = true) 
            } ?: OTHER
        }
    }
}

data class ServiceBill(
    val id: String = "",
    val name: String = "",
    val serviceType: ServiceType = ServiceType.LIGHT,
    val iconEmoji: String = "💡",
    val amount: Double = 0.0,
    val dueDate: String = "",
    val isPaid: Boolean = false,
    val paidBy: String? = null,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
