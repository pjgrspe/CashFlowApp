// AccountCard.kt
package ph.edu.auf.gorospe.patrickjason.cashflow.data

data class AccountCard(
    var id: String = "",
    var cardCategory: String = "",
    var cardNumber: String = "",
    var cardName: String = "",
    var balance: Double = 0.0,
    var cardColor: String = "",
    var dateCreated: Long = 0L
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", "", 0.0, "", 0L)
}