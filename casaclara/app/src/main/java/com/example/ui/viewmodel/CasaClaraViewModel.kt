package com.example.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Expense
import com.example.data.model.Household
import com.example.data.model.PantryItem
import com.example.data.model.ServiceBill
import com.example.data.model.ServiceType
import com.example.data.model.StockStatus
import com.example.data.repository.HouseholdRepository
import com.example.data.repository.NotificationAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CasaClaraViewModel(
    val repository: HouseholdRepository = HouseholdRepository()
) : ViewModel() {

    val household: StateFlow<Household> = repository.household
    val currentUser: StateFlow<String> = repository.currentUser
    val expenses: StateFlow<List<Expense>> = repository.expenses
    val pantryItems: StateFlow<List<PantryItem>> = repository.pantryItems
    val serviceBills: StateFlow<List<ServiceBill>> = repository.serviceBills

    private val _activeTab = MutableStateFlow(0) // 0: Gastos, 1: Stock / Alacena
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _selectedMonthCalendar = MutableStateFlow(Calendar.getInstance())
    val selectedMonthCalendar: StateFlow<Calendar> = _selectedMonthCalendar.asStateFlow()

    private val _latestAlert = MutableStateFlow<NotificationAlert?>(null)
    val latestAlert: StateFlow<NotificationAlert?> = _latestAlert.asStateFlow()

    private val _showOnboarding = MutableStateFlow(false)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _showAddExpenseModal = MutableStateFlow(false)
    val showAddExpenseModal: StateFlow<Boolean> = _showAddExpenseModal.asStateFlow()

    private val _showAddCustomItemModal = MutableStateFlow(false)
    val showAddCustomItemModal: StateFlow<Boolean> = _showAddCustomItemModal.asStateFlow()

    private val _showAddServiceBillModal = MutableStateFlow(false)
    val showAddServiceBillModal: StateFlow<Boolean> = _showAddServiceBillModal.asStateFlow()

    private val _itemToMarkBought = MutableStateFlow<PantryItem?>(null)
    val itemToMarkBought: StateFlow<PantryItem?> = _itemToMarkBought.asStateFlow()

    private val _showHouseholdSettings = MutableStateFlow(false)
    val showHouseholdSettings: StateFlow<Boolean> = _showHouseholdSettings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.notificationAlerts.collect { alert ->
                _latestAlert.value = alert
            }
        }
    }

    fun dismissAlert() {
        _latestAlert.value = null
    }

    fun setTab(index: Int) {
        _activeTab.value = index
    }

    fun toggleOnboarding(show: Boolean) {
        _showOnboarding.value = show
    }

    fun toggleAddExpenseModal(show: Boolean) {
        _showAddExpenseModal.value = show
    }

    fun toggleAddCustomItemModal(show: Boolean) {
        _showAddCustomItemModal.value = show
    }

    fun toggleAddServiceBillModal(show: Boolean) {
        _showAddServiceBillModal.value = show
    }

    fun setItemToMarkBought(item: PantryItem?) {
        _itemToMarkBought.value = item
    }

    fun toggleHouseholdSettings(show: Boolean) {
        _showHouseholdSettings.value = show
    }

    fun setCurrentUser(user: String) {
        repository.setCurrentUser(user)
    }

    fun changeMonth(delta: Int) {
        val cal = _selectedMonthCalendar.value.clone() as Calendar
        cal.add(Calendar.MONTH, delta)
        _selectedMonthCalendar.value = cal
    }

    fun getSelectedMonthKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(_selectedMonthCalendar.value.time)
    }

    fun getSelectedMonthFormatted(): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        return sdf.format(_selectedMonthCalendar.value.time).capitalize(Locale("es", "ES"))
    }

    fun addExpense(expense: Expense) {
        repository.addExpense(expense)
        _showAddExpenseModal.value = false
    }

    fun deleteExpense(expenseId: String) {
        repository.deleteExpense(expenseId)
    }

    fun updateStockStatus(itemId: String, status: StockStatus) {
        repository.updatePantryItemStatus(itemId, status)
    }

    fun addCustomPantryItem(
        name: String,
        category: String,
        emoji: String,
        price: Double,
        status: StockStatus = StockStatus.FALTA
    ) {
        repository.addCustomPantryItem(name, category, emoji, price, status)
        _showAddCustomItemModal.value = false
    }

    fun addMultipleItemsToShoppingList(itemIds: Set<String>) {
        itemIds.forEach { id ->
            repository.updatePantryItemStatus(id, StockStatus.FALTA)
        }
    }

    fun markItemBought(item: PantryItem, convertToExpense: Boolean, amount: Double? = null) {
        repository.markItemBoughtAndOptionallyAddExpense(item, convertToExpense, amount)
        _itemToMarkBought.value = null
    }

    fun createHousehold(name: String) {
        repository.createHousehold(name)
        _showHouseholdSettings.value = false
    }

    fun joinHousehold(code: String, userName: String): Boolean {
        val success = repository.joinHousehold(code, userName)
        if (success) {
            _showHouseholdSettings.value = false
        }
        return success
    }

    fun addServiceBill(
        name: String,
        serviceType: ServiceType,
        amount: Double,
        dueDate: String,
        iconEmoji: String = serviceType.defaultEmoji
    ) {
        repository.addServiceBill(name, serviceType, amount, dueDate, iconEmoji)
        _showAddServiceBillModal.value = false
    }

    fun toggleServiceBillPaid(billId: String, recordAsExpense: Boolean = true) {
        repository.toggleServiceBillPaid(billId, recordAsExpense)
    }

    fun deleteServiceBill(billId: String) {
        repository.deleteServiceBill(billId)
    }
}
