package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.StockStatus
import com.example.data.viewmodel.CasaClaraViewModel
import com.example.ui.components.AddCustomItemSheet
import com.example.ui.components.AddExpenseSheet
import com.example.ui.components.AddServiceBillSheet
import com.example.ui.components.CasaClaraBottomNav
import com.example.ui.components.CasaClaraTopBar
import com.example.ui.components.HouseholdSettingsModal
import com.example.ui.components.MarkBoughtDialog
import com.example.ui.components.NotificationBanner
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.FutureFeaturesScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PantryScreen
import com.example.ui.theme.CasaClaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CasaClaraViewModel = viewModel()

            CasaClaraTheme {
                CasaClaraApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CasaClaraApp(
    viewModel: CasaClaraViewModel = viewModel()
) {
    val household by viewModel.household.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val pantryItems by viewModel.pantryItems.collectAsState()
    val serviceBills by viewModel.serviceBills.collectAsState()

    val activeTab by viewModel.activeTab.collectAsState()
    val selectedMonthCalendar by viewModel.selectedMonthCalendar.collectAsState()
    val latestAlert by viewModel.latestAlert.collectAsState()

    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val showAddExpenseModal by viewModel.showAddExpenseModal.collectAsState()
    val showAddCustomItemModal by viewModel.showAddCustomItemModal.collectAsState()
    val showAddServiceBillModal by viewModel.showAddServiceBillModal.collectAsState()
    val itemToMarkBought by viewModel.itemToMarkBought.collectAsState()
    val showHouseholdSettings by viewModel.showHouseholdSettings.collectAsState()

    val missingItemsCount = pantryItems.count { it.status == StockStatus.FALTA }

    if (showOnboarding) {
        OnboardingScreen(
            onFinishOnboarding = { viewModel.toggleOnboarding(false) }
        )
    } else {
        Scaffold(
            topBar = {
                CasaClaraTopBar(
                    household = household,
                    currentUser = currentUser,
                    onUserToggle = {
                        val nextUser = if (currentUser == "Sofi") "Ale" else "Sofi"
                        viewModel.setCurrentUser(nextUser)
                    },
                    onHouseholdSettingsClick = { viewModel.toggleHouseholdSettings(true) },
                    onOnboardingClick = { viewModel.toggleOnboarding(true) }
                )
            },
            bottomBar = {
                CasaClaraBottomNav(
                    selectedTab = activeTab,
                    onTabSelected = { viewModel.setTab(it) },
                    missingItemsCount = missingItemsCount
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Notification Banner
                    NotificationBanner(
                        alert = latestAlert,
                        onDismiss = { viewModel.dismissAlert() }
                    )

                    // Active Tab Animated Transition
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally(animationSpec = tween(350)) { width -> width / 3 } +
                                        fadeIn(animationSpec = tween(300)) +
                                        scaleIn(initialScale = 0.96f, animationSpec = tween(350))) togetherWith
                                        (slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 3 } +
                                                fadeOut(animationSpec = tween(250)))
                            } else {
                                (slideInHorizontally(animationSpec = tween(350)) { width -> -width / 3 } +
                                        fadeIn(animationSpec = tween(300)) +
                                        scaleIn(initialScale = 0.96f, animationSpec = tween(350))) togetherWith
                                        (slideOutHorizontally(animationSpec = tween(300)) { width -> width / 3 } +
                                                fadeOut(animationSpec = tween(250)))
                            }.using(SizeTransform(clip = false))
                        },
                        label = "tab_navigation_animated",
                        modifier = Modifier.weight(1f)
                    ) { tabIndex ->
                        when (tabIndex) {
                            0 -> ExpensesScreen(
                                expenses = expenses,
                                currentUser = currentUser,
                                members = household.members,
                                selectedMonthCalendar = selectedMonthCalendar,
                                onChangeMonth = { delta -> viewModel.changeMonth(delta) },
                                onOpenAddExpense = { viewModel.toggleAddExpenseModal(true) },
                                onDeleteExpense = { id -> viewModel.deleteExpense(id) }
                            )

                            1 -> PantryScreen(
                                items = pantryItems,
                                onStatusChange = { id, newStatus ->
                                    viewModel.updateStockStatus(id, newStatus)
                                },
                                onOpenAddCustomItem = { viewModel.toggleAddCustomItemModal(true) },
                                onOpenMarkBought = { item -> viewModel.setItemToMarkBought(item) },
                                onAddMultipleItemsToShoppingList = { ids ->
                                    viewModel.addMultipleItemsToShoppingList(ids)
                                }
                            )

                            2 -> FutureFeaturesScreen(
                                serviceBills = serviceBills,
                                onOpenAddService = { viewModel.toggleAddServiceBillModal(true) },
                                onTogglePaid = { billId, recordExpense ->
                                    viewModel.toggleServiceBillPaid(billId, recordExpense)
                                },
                                onDeleteService = { billId ->
                                    viewModel.deleteServiceBill(billId)
                                }
                            )
                        }
                    }
                }
            }

            // Modal Sheets and Dialogs
            if (showAddExpenseModal) {
                AddExpenseSheet(
                    currentUser = currentUser,
                    members = household.members,
                    onDismiss = { viewModel.toggleAddExpenseModal(false) },
                    onSaveExpense = { expense -> viewModel.addExpense(expense) }
                )
            }

            if (showAddCustomItemModal) {
                AddCustomItemSheet(
                    onDismiss = { viewModel.toggleAddCustomItemModal(false) },
                    onSaveItem = { name, cat, emo, price, status ->
                        viewModel.addCustomPantryItem(name, cat, emo, price, status)
                    }
                )
            }

            if (showAddServiceBillModal) {
                AddServiceBillSheet(
                    onDismiss = { viewModel.toggleAddServiceBillModal(false) },
                    onSaveService = { name, type, amount, dueDate, emoji ->
                        viewModel.addServiceBill(name, type, amount, dueDate, emoji)
                    }
                )
            }

            itemToMarkBought?.let { item ->
                MarkBoughtDialog(
                    item = item,
                    onDismiss = { viewModel.setItemToMarkBought(null) },
                    onConfirmBought = { convertToExpense, amount ->
                        viewModel.markItemBought(item, convertToExpense, amount)
                    }
                )
            }

            if (showHouseholdSettings) {
                HouseholdSettingsModal(
                    household = household,
                    currentUser = currentUser,
                    onDismiss = { viewModel.toggleHouseholdSettings(false) },
                    onJoinHousehold = { code, userName -> viewModel.joinHousehold(code, userName) },
                    onCreateHousehold = { name -> viewModel.createHousehold(name) },
                    onSetUser = { user -> viewModel.setCurrentUser(user) }
                )
            }
        }
    }
}
