package com.example.data.firebase

import android.util.Log
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.Household
import com.example.data.model.PantryItem
import com.example.data.model.StockStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseManager {

    private val isAppInitialized: Boolean
        get() = try {
            FirebaseApp.getInstance() != null
        } catch (e: Exception) {
            false
        }

    private val firestore: FirebaseFirestore? by lazy {
        if (!isAppInitialized) {
            Log.i("FirebaseManager", "FirebaseApp not initialized. Operating in local mode.")
            null
        } else {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.i("FirebaseManager", "Firestore not available: ${e.message}")
                null
            }
        }
    }

    private val auth: FirebaseAuth? by lazy {
        if (!isAppInitialized) {
            null
        } else {
            try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                Log.i("FirebaseManager", "FirebaseAuth not available: ${e.message}")
                null
            }
        }
    }

    var isFirebaseAvailable: Boolean = false
        private set

    init {
        try {
            isFirebaseAvailable = firestore != null
            ensureAnonymousAuth()
        } catch (e: Exception) {
            isFirebaseAvailable = false
        }
    }

    private fun ensureAnonymousAuth() {
        auth?.let { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                firebaseAuth.signInAnonymously().addOnFailureListener { e ->
                    Log.w("FirebaseManager", "Anonymous auth failed: ${e.message}")
                }
            }
        }
    }

    fun observeExpenses(householdCode: String): Flow<List<Expense>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = db.collection("households")
            .document(householdCode)
            .collection("expenses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirebaseManager", "Listen expenses failed", error)
                    trySend(null)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            Expense(
                                id = doc.id,
                                amount = doc.getDouble("amount") ?: 0.0,
                                category = ExpenseCategory.fromString(doc.getString("category") ?: ""),
                                paidBy = doc.getString("paidBy") ?: "Alguien",
                                note = doc.getString("note") ?: "",
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                monthKey = doc.getString("monthKey") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list)
                }
            }

        awaitClose { listener.remove() }
    }

    fun observePantryItems(householdCode: String): Flow<List<PantryItem>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = db.collection("households")
            .document(householdCode)
            .collection("pantry_items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirebaseManager", "Listen pantry failed", error)
                    trySend(null)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            PantryItem(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                category = doc.getString("category") ?: "Almacén",
                                iconEmoji = doc.getString("iconEmoji") ?: "📦",
                                status = StockStatus.fromString(doc.getString("status") ?: "HAY"),
                                lastUpdatedBy = doc.getString("lastUpdatedBy") ?: "Sistema",
                                estimatedPrice = doc.getDouble("estimatedPrice") ?: 1000.0,
                                isCustom = doc.getBoolean("isCustom") ?: false,
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list)
                }
            }

        awaitClose { listener.remove() }
    }

    fun saveExpense(householdCode: String, expense: Expense) {
        val db = firestore ?: return
        val map = mapOf(
            "amount" to expense.amount,
            "category" to expense.category.name,
            "paidBy" to expense.paidBy,
            "note" to expense.note,
            "timestamp" to expense.timestamp,
            "monthKey" to expense.monthKey
        )
        db.collection("households")
            .document(householdCode)
            .collection("expenses")
            .document(expense.id)
            .set(map)
    }

    fun deleteExpense(householdCode: String, expenseId: String) {
        val db = firestore ?: return
        db.collection("households")
            .document(householdCode)
            .collection("expenses")
            .document(expenseId)
            .delete()
    }

    fun savePantryItem(householdCode: String, item: PantryItem) {
        val db = firestore ?: return
        val map = mapOf(
            "name" to item.name,
            "category" to item.category,
            "iconEmoji" to item.iconEmoji,
            "status" to item.status.name,
            "lastUpdatedBy" to item.lastUpdatedBy,
            "estimatedPrice" to item.estimatedPrice,
            "isCustom" to item.isCustom,
            "updatedAt" to item.updatedAt
        )
        db.collection("households")
            .document(householdCode)
            .collection("pantry_items")
            .document(item.id)
            .set(map)
    }

    fun saveHousehold(household: Household) {
        val db = firestore ?: return
        val map = mapOf(
            "name" to household.name,
            "code" to household.code,
            "members" to household.members,
            "createdAt" to household.createdAt
        )
        db.collection("households").document(household.code).set(map)
    }
}
