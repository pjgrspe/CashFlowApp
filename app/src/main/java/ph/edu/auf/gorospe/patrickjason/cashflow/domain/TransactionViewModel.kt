package ph.edu.auf.gorospe.patrickjason.cashflow.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TransactionViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Add Transaction
    suspend fun addTransaction(userId: String, accountId: String, amount: Double, account: String, category: String, note: String): Result<String> {
        return try {
            // Create transaction document
            val transactionData = hashMapOf(
                "amount" to amount,
                "account" to account,
                "category" to category,
                "note" to note,
                "transactionNo" to System.currentTimeMillis() // Use timestamp as transaction number
            )
            firestore.collection("users").document(userId)
                .collection("accounts").document(accountId)
                .collection("transactions").add(transactionData).await()

            // Update budget's expense field
            val budgetRef = firestore.collection("users").document(userId).collection("accounts").document(accountId)
            firestore.runTransaction { transaction ->
                val budgetSnapshot = transaction.get(budgetRef)
                val newExpense = (budgetSnapshot.getDouble("expense") ?: 0.0) + amount
                transaction.update(budgetRef, "expense", newExpense)
            }.await()

            Result.success("Transaction added successfully")
        } catch (e: Exception) {
            Log.e("TransactionViewModel", "Adding transaction failed", e)
            Result.failure(e)
        }
    }
}
