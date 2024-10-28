// RegisterViewModel.kt
package ph.edu.auf.gorospe.patrickjason.cashflow.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Register User
    suspend fun registerUser(username: String, email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")

            // Create user document in Firestore
            val user = hashMapOf(
                "username" to username,
                "email" to email,
                "balance" to 0.0,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(userId).set(user).await()

            // Create an initial account document
            val accountId = firestore.collection("users").document(userId).collection("accounts").document().id
            val account = hashMapOf(
                "id" to accountId,
                "cardCategory" to "Cash",
                "cardNumber" to "0000 0000 0000 0000",
                "cardName" to "General",
                "balance" to 0.0,
                "cardColor" to "Green",
                "dateCreated" to System.currentTimeMillis()
            )
            firestore.collection("users").document(userId).collection("accounts").document(accountId).set(account).await()

            Result.success("Registration successful")
        } catch (e: Exception) {
            Log.e("RegisterViewModel", "Registration failed", e)
            Result.failure(e)
        }
    }
}