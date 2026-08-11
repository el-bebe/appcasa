package com.example.data.repository

import com.example.data.firebase.FirebaseManager
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.Household
import com.example.data.model.PantryItem
import com.example.data.model.ServiceBill
import com.example.data.model.ServiceType
import com.example.data.model.StockStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class NotificationAlert(
    val title: String,
    val message: String,
    val iconEmoji: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HouseholdRepository(
    private val firebaseManager: FirebaseManager = FirebaseManager()
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _household = MutableStateFlow(Household())
    val household: StateFlow<Household> = _household.asStateFlow()

    private val _currentUser = MutableStateFlow("Sofi")
    val currentUser: StateFlow<String> = _currentUser.asStateFlow()

    private val _expenses = MutableStateFlow(DefaultData.defaultExpenses())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _pantryItems = MutableStateFlow(DefaultData.defaultPantryItems)
    val pantryItems: StateFlow<List<PantryItem>> = _pantryItems.asStateFlow()

    private val _serviceBills = MutableStateFlow(DefaultData.defaultServiceBills)
    val serviceBills: StateFlow<List<ServiceBill>> = _serviceBills.asStateFlow()

    private val _notificationAlerts = MutableSharedFlow<NotificationAlert>(extraBufferCapacity = 5)
    val notificationAlerts: SharedFlow<NotificationAlert> = _notificationAlerts.asSharedFlow()

    init {
        observeFirebaseData()
    }

    private fun observeFirebaseData() {
        val code = _household.value.code
        repositoryScope.launch {
            firebaseManager.observeExpenses(code).collect { remoteExpenses ->
                if (remoteExpenses != null && remoteExpenses.isNotEmpty()) {
                    _expenses.value = remoteExpenses.sortedByDescending { it.timestamp }
                }
            }
        }

        repositoryScope.launch {
            firebaseManager.observePantryItems(code).collect { remoteItems ->
                if (remoteItems != null && remoteItems.isNotEmpty()) {
                    _pantryItems.value = remoteItems
                }
            }
        }
    }

    fun setCurrentUser(user: String) {
        _currentUser.value = user
    }

    fun setHousehold(household: Household) {
        _household.value = household
        firebaseManager.saveHousehold(household)
        observeFirebaseData()
    }

    fun createHousehold(name: String): Household {
        val randomCode = "CLARA" + (10..99).random()
        val newH = Household(
            id = UUID.randomUUID().toString(),
            name = name,
            code = randomCode,
            members = listOf("Sofi", "Ale")
        )
        setHousehold(newH)
        
        // Seed default items to new household
        DefaultData.defaultPantryItems.forEach { item ->
            firebaseManager.savePantryItem(randomCode, item)
        }
        DefaultData.defaultExpenses().forEach { expense ->
            firebaseManager.saveExpense(randomCode, expense)
        }
        return newH
    }

    fun joinHousehold(code: String, userName: String): Boolean {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.isBlank()) return false

        val updatedMembers = if (!_household.value.members.contains(userName)) {
            _household.value.members + userName
        } else {
            _household.value.members
        }

        val updated = _household.value.copy(
            code = cleanCode,
            members = updatedMembers
        )
        setHousehold(updated)
        _currentUser.value = userName
        return true
    }

    fun addExpense(expense: Expense) {
        val id = if (expense.id.isBlank()) "exp_" + UUID.randomUUID().toString().take(8) else expense.id
        val newExpense = expense.copy(id = id, paidBy = _currentUser.value)
        val currentList = _expenses.value.toMutableList()
        currentList.add(0, newExpense)
        _expenses.value = currentList

        firebaseManager.saveExpense(_household.value.code, newExpense)
    }

    fun deleteExpense(expenseId: String) {
        _expenses.value = _expenses.value.filterNot { it.id == expenseId }
        firebaseManager.deleteExpense(_household.value.code, expenseId)
    }

    fun updatePantryItemStatus(itemId: String, newStatus: StockStatus) {
        val updatedUser = _currentUser.value
        val currentItems = _pantryItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == itemId }

        if (index != -1) {
            val oldItem = currentItems[index]
            val updatedItem = oldItem.copy(
                status = newStatus,
                lastUpdatedBy = updatedUser,
                updatedAt = System.currentTimeMillis()
            )
            currentItems[index] = updatedItem
            _pantryItems.value = currentItems

            firebaseManager.savePantryItem(_household.value.code, updatedItem)

            // Trigger Notification Alert if status changed to FALTA
            if (newStatus == StockStatus.FALTA && oldItem.status != StockStatus.FALTA) {
                _notificationAlerts.tryEmit(
                    NotificationAlert(
                        title = "¡Falta en la Alacena! 🚨",
                        message = "$updatedUser marcó como FALTA: ${oldItem.name}",
                        iconEmoji = oldItem.iconEmoji
                    )
                )
            }
        }
    }

    fun addCustomPantryItem(
        name: String,
        category: String,
        emoji: String,
        price: Double,
        status: StockStatus = StockStatus.FALTA
    ) {
        val newItem = PantryItem(
            id = "item_" + UUID.randomUUID().toString().take(8),
            name = name,
            category = category,
            iconEmoji = emoji,
            status = status,
            lastUpdatedBy = _currentUser.value,
            estimatedPrice = price,
            isCustom = true
        )
        val currentList = _pantryItems.value.toMutableList()
        currentList.add(0, newItem)
        _pantryItems.value = currentList

        firebaseManager.savePantryItem(_household.value.code, newItem)
    }

    fun markItemBoughtAndOptionallyAddExpense(
        item: PantryItem,
        addAsExpense: Boolean,
        actualAmount: Double? = null
    ) {
        updatePantryItemStatus(item.id, StockStatus.HAY)

        if (addAsExpense) {
            val expenseCategory = when (item.category.lowercase()) {
                "limpieza" -> com.example.data.model.ExpenseCategory.CLEANING
                "frescos" -> com.example.data.model.ExpenseCategory.SUPERMARKET
                "bebidas" -> com.example.data.model.ExpenseCategory.SUPERMARKET
                else -> com.example.data.model.ExpenseCategory.SUPERMARKET
            }

            addExpense(
                Expense(
                    amount = actualAmount ?: item.estimatedPrice,
                    category = expenseCategory,
                    paidBy = _currentUser.value,
                    note = "Compra de ${item.name} ${item.iconEmoji}"
                )
            )
        }
    }

    fun addServiceBill(
        name: String,
        serviceType: ServiceType,
        amount: Double,
        dueDate: String,
        iconEmoji: String = serviceType.defaultEmoji
    ) {
        val newBill = ServiceBill(
            id = "bill_" + UUID.randomUUID().toString().take(8),
            name = name.ifBlank { serviceType.displayName },
            serviceType = serviceType,
            iconEmoji = iconEmoji,
            amount = amount,
            dueDate = dueDate.ifBlank { "Vence este mes" },
            isPaid = false,
            isCustom = true
        )
        val currentList = _serviceBills.value.toMutableList()
        currentList.add(0, newBill)
        _serviceBills.value = currentList
    }

    fun toggleServiceBillPaid(billId: String, recordAsExpense: Boolean = true) {
        val currentList = _serviceBills.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == billId }
        if (index != -1) {
            val oldBill = currentList[index]
            val newIsPaid = !oldBill.isPaid
            val updatedBill = oldBill.copy(
                isPaid = newIsPaid,
                paidBy = if (newIsPaid) _currentUser.value else null
            )
            currentList[index] = updatedBill
            _serviceBills.value = currentList

            // If marking as paid, optionally create an Expense in category SERVICES
            if (newIsPaid && recordAsExpense) {
                addExpense(
                    Expense(
                        amount = oldBill.amount,
                        category = ExpenseCategory.SERVICES,
                        paidBy = _currentUser.value,
                        note = "Pago de ${oldBill.name} ${oldBill.iconEmoji}"
                    )
                )
            }
        }
    }

    fun deleteServiceBill(billId: String) {
        _serviceBills.value = _serviceBills.value.filterNot { it.id == billId }
    }
}
