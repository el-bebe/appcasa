package com.example.data.repository

import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.PantryItem
import com.example.data.model.ServiceBill
import com.example.data.model.ServiceType
import com.example.data.model.StockStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DefaultData {
    val defaultServiceBills = listOf(
        ServiceBill(
            id = "bill_1",
            name = "Factura de Luz",
            serviceType = ServiceType.LIGHT,
            iconEmoji = "💡",
            amount = 12400.0,
            dueDate = "Vence el 15 de este mes",
            isPaid = false
        ),
        ServiceBill(
            id = "bill_2",
            name = "Internet Fibra",
            serviceType = ServiceType.INTERNET,
            iconEmoji = "🌐",
            amount = 18900.0,
            dueDate = "Vence el 20 de este mes",
            isPaid = false
        ),
        ServiceBill(
            id = "bill_3",
            name = "Agua Potable",
            serviceType = ServiceType.WATER,
            iconEmoji = "💧",
            amount = 4200.0,
            dueDate = "Vence el 28 de este mes",
            isPaid = false
        ),
        ServiceBill(
            id = "bill_4",
            name = "Gas Natural",
            serviceType = ServiceType.GAS,
            iconEmoji = "🔥",
            amount = 6500.0,
            dueDate = "Vence el 10 de este mes",
            isPaid = false
        )
    )

    val defaultPantryItems = listOf(
        PantryItem("item_1", "Arroz", "Almacén", "🍚", StockStatus.HAY, "Sofi", 1200.0),
        PantryItem("item_2", "Leche", "Frescos", "🥛", StockStatus.QUEDA_POCO, "Ale", 1400.0),
        PantryItem("item_3", "Papel Higiénico", "Limpieza", "🧻", StockStatus.FALTA, "Sofi", 3500.0),
        PantryItem("item_4", "Aceite de Oliva", "Almacén", "🫒", StockStatus.HAY, "Ale", 4800.0),
        PantryItem("item_5", "Huevos", "Frescos", "🥚", StockStatus.FALTA, "Sofi", 2800.0),
        PantryItem("item_6", "Jabón de Ropa", "Limpieza", "🧼", StockStatus.HAY, "Ale", 3200.0),
        PantryItem("item_7", "Café molido", "Almacén", "☕", StockStatus.HAY, "Sofi", 5500.0),
        PantryItem("item_8", "Detergente", "Limpieza", "🧽", StockStatus.QUEDA_POCO, "Ale", 1800.0),
        PantryItem("item_9", "Pan de Molde", "Frescos", "🍞", StockStatus.FALTA, "Sofi", 2200.0),
        PantryItem("item_10", "Fideos", "Almacén", "🍝", StockStatus.HAY, "Ale", 1100.0),
        PantryItem("item_11", "Manteca", "Frescos", "🧈", StockStatus.HAY, "Sofi", 1600.0),
        PantryItem("item_12", "Queso Crema", "Frescos", "🧀", StockStatus.QUEDA_POCO, "Ale", 2400.0),
        PantryItem("item_13", "Servilletas", "Limpieza", "🧻", StockStatus.HAY, "Sofi", 900.0),
        PantryItem("item_14", "Lavandina", "Limpieza", "🧴", StockStatus.HAY, "Ale", 1500.0),
        PantryItem("item_15", "Agua Mineral", "Bebidas", "💧", StockStatus.FALTA, "Sofi", 1200.0),
        PantryItem("item_16", "Dentífrico", "Limpieza", "🪥", StockStatus.HAY, "Ale", 2100.0),
        PantryItem("item_17", "Galletitas", "Almacén", "🍪", StockStatus.HAY, "Sofi", 1300.0),
        PantryItem("item_18", "Puré de Tomate", "Almacén", "🥫", StockStatus.HAY, "Ale", 800.0),
        PantryItem("item_19", "Sal Fina", "Almacén", "🧂", StockStatus.HAY, "Sofi", 700.0),
        PantryItem("item_20", "Azúcar", "Almacén", "🍬", StockStatus.HAY, "Ale", 1100.0)
    )

    fun defaultExpenses(): List<Expense> {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        val currentMonthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(now))

        return listOf(
            Expense(
                id = "exp_1",
                amount = 18500.0,
                category = ExpenseCategory.SUPERMARKET,
                paidBy = "Sofi",
                note = "Compra semanal Coto",
                timestamp = now - (0.1 * oneDay).toLong(),
                monthKey = currentMonthKey
            ),
            Expense(
                id = "exp_2",
                amount = 4200.0,
                category = ExpenseCategory.CLEANING,
                paidBy = "Ale",
                note = "Lavandina y papel higiénico",
                timestamp = now - (1 * oneDay),
                monthKey = currentMonthKey
            ),
            Expense(
                id = "exp_3",
                amount = 12400.0,
                category = ExpenseCategory.SERVICES,
                paidBy = "Sofi",
                note = "Factura de Luz",
                timestamp = now - (2 * oneDay),
                monthKey = currentMonthKey
            ),
            Expense(
                id = "exp_4",
                amount = 8900.0,
                category = ExpenseCategory.ENTERTAINMENT,
                paidBy = "Ale",
                note = "Cine + Pochoclos",
                timestamp = now - (3 * oneDay),
                monthKey = currentMonthKey
            ),
            Expense(
                id = "exp_5",
                amount = 3100.0,
                category = ExpenseCategory.TRANSPORT,
                paidBy = "Sofi",
                note = "Carga SUBE",
                timestamp = now - (4 * oneDay),
                monthKey = currentMonthKey
            )
        )
    }
}
