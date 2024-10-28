// UserRepository.kt
package ph.edu.auf.gorospe.patrickjason.cashflow.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ph.edu.auf.gorospe.patrickjason.cashflow.domain.AccountViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    // --- Account Management Functions ---

    suspend fun getAccountById(accountId: String): AccountCard {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val accountSnapshot = firestore.collection("users")
            .document(userId)
            .collection("accounts")
            .document(accountId)
            .get()
            .await()
        return accountSnapshot.toObject(AccountCard::class.java) ?: throw Exception("Account not found")
    }

//    fun getAccountCardsFlow(): Flow<List<AccountCard>> = callbackFlow {
//        val userId = auth.currentUser?.uid ?: return@callbackFlow
//        val listenerRegistration = firestore.collection("users")
//            .document(userId)
//            .collection("accounts")
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    close(e) // Close the flow if an error occurs
//                    return@addSnapshotListener
//                }
//                val accounts = snapshot?.toObjects(AccountCard::class.java) ?: emptyList()
//                trySend(accounts) // Emit the accounts list to the flow
//            }
//
//        // Wait for the flow to be canceled to clean up
//        awaitClose { listenerRegistration.remove() }
//    }

    suspend fun getAccountCards(): List<AccountCard> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val accountsSnapshot = firestore.collection("users").document(userId).collection("accounts").get().await()
        val accounts = accountsSnapshot.toObjects(AccountCard::class.java)
        Log.d("UserRepository", "Fetched account cards: ${accounts.map { it.cardName }}") // Log all account names
        return accounts
    }

    suspend fun addAccountCard(accountCard: AccountCard): String {
        val userId = auth.currentUser?.uid ?: return ""
        val documentRef = firestore.collection("users").document(userId).collection("accounts").add(accountCard).await()
        accountCard.id = documentRef.id // Ensure ID is set here
        documentRef.set(accountCard).await() // This updates the document with the ID
        return documentRef.id
    }

    suspend fun updateAccount(accountCard: AccountCard) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val accountRef = firestore.collection("users")
            .document(userId)
            .collection("accounts")
            .document(accountCard.id)
        accountRef.set(accountCard).await()
    }

    suspend fun deleteAccount(accountId: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val accountRef = firestore.collection("users")
            .document(userId)
            .collection("accounts")
            .document(accountId)
        accountRef.delete().await()
    }

    // --- User Information Functions ---

    suspend fun getUserNameAndBalance(): Pair<String, Double> {
        val userId = auth.currentUser?.uid ?: return Pair("User", 0.0)
        val userDoc = firestore.collection("users").document(userId).get().await()

        val userName = userDoc.getString("username") ?: "User"
        val balance = userDoc.getDouble("balance") ?: 0.0

        return Pair(userName, balance)
    }

    fun getCurrentUser(): User? {
        return auth.currentUser?.let { user ->
            User(
                id = user.uid,
                email = user.email,
                displayName = user.displayName
            )
        }
    }

    fun logout() {
        auth.signOut()
    }

    // --- Transaction Functions ---

    // UserRepository.kt
    suspend fun getTotalBalance(): Double {
        val userId = auth.currentUser?.uid ?: return 0.0
        val accountsSnapshot = firestore.collection("users").document(userId).collection("accounts").get().await()
        return accountsSnapshot.documents.sumOf { it.getDouble("balance") ?: 0.0 }
    }

    suspend fun getTransactions(): List<TransactionData> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val transactionsSnapshot = firestore.collection("users").document(userId).collection("transactions").get().await()
        return transactionsSnapshot.toObjects(TransactionData::class.java)
    }

    suspend fun addIncome(amount: String, account: AccountCard, category: String, date: String, notes: String) {
        val userId = auth.currentUser?.uid ?: return
        val accountRef = firestore.collection("users")
            .document(userId)
            .collection("accounts")
            .document(account.id)

        firestore.runTransaction { transaction ->
            val currentBalance = transaction.get(accountRef).getDouble("balance") ?: 0.0
            val updatedBalance = currentBalance + amount.toDouble()
            transaction.update(accountRef, "balance", updatedBalance)
        }.await()

        // Save the income transaction
        val incomeTransaction = TransactionData(
            type = TransactionType.INCOME,
            amount = amount.toDouble(),
            account = account.cardName,
            category = category,
            date = date,
            notes = notes
        )
        firestore.collection("users").document(userId).collection("transactions").add(incomeTransaction).await()
    }

    suspend fun addExpense(amount: String, account: AccountCard, category: String, date: String, notes: String) {
        val userId = auth.currentUser?.uid ?: return
        val accountRef = firestore.collection("users")
            .document(userId)
            .collection("accounts")
            .document(account.id)

        firestore.runTransaction { transaction ->
            val currentBalance = transaction.get(accountRef).getDouble("balance") ?: 0.0
            val updatedBalance = currentBalance - amount.toDouble()
            transaction.update(accountRef, "balance", updatedBalance)
        }.await()

        // Save the expense transaction
        val expenseTransaction = TransactionData(
            type = TransactionType.EXPENSE,
            amount = amount.toDouble(),
            account = account.cardName,
            category = category,
            date = date,
            notes = notes
        )
        firestore.collection("users").document(userId).collection("transactions").add(expenseTransaction).await()
    }

    // --- Transfer Function ---

    suspend fun transfer(amount: String, source: AccountCard, target: AccountCard) {
        Log.d("UserRepository", "Starting transfer: amount=$amount, source=${source.cardName}, target=${target.cardName}")
        val transferAmount = amount.toDoubleOrNull() ?: return

        val userId = auth.currentUser?.uid ?: return
        val sourceRef = firestore.collection("users").document(userId).collection("accounts").document(source.id)
        val targetRef = firestore.collection("users").document(userId).collection("accounts").document(target.id)

        try {
            firestore.runTransaction { transaction ->
                val sourceSnapshot = transaction.get(sourceRef)
                val targetSnapshot = transaction.get(targetRef)

                val sourceBalance = sourceSnapshot.getDouble("balance") ?: 0.0
                val targetBalance = targetSnapshot.getDouble("balance") ?: 0.0

                if (sourceBalance >= transferAmount) {
                    transaction.update(sourceRef, "balance", sourceBalance - transferAmount)
                    transaction.update(targetRef, "balance", targetBalance + transferAmount)

                    // Add transaction records
                    val sourceTransaction = TransactionData(
                        type = TransactionType.TRANSFER_OUTGOING,
                        amount = transferAmount,
                        account = source.cardName,
                        category = "Transfer",
                        date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                        notes = "Transfer to ${target.cardName}"
                    )
                    val targetTransaction = TransactionData(
                        type = TransactionType.TRANSFER_INCOMING,
                        amount = transferAmount,
                        account = target.cardName,
                        category = "Transfer",
                        date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                        notes = "Transfer from ${source.cardName}"
                    )

                    firestore.collection("users").document(userId).collection("transactions").add(sourceTransaction)
                    firestore.collection("users").document(userId).collection("transactions").add(targetTransaction)
                } else {
                    throw Exception("Insufficient funds")
                }
            }.await()
            Log.d("UserRepository", "Transfer completed successfully")
        } catch (e: Exception) {
            Log.e("UserRepository", "Transfer failed: ${e.message}")
        }
    }

    // --- ViewModel Factory ---

    fun provideAccountViewModelFactory(userRepository: UserRepository): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AccountViewModel(userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    // --- Additional Data Classes ---

    data class User(val id: String, val email: String?, val displayName: String?)
}
