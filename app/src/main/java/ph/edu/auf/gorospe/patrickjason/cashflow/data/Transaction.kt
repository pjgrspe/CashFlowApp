package ph.edu.auf.gorospe.patrickjason.cashflow.data

data class TransactionData(
    val type: TransactionType,
    val account: String,
    val amount: Double,
    val category: String,
    val date: String,
    val notes: String,
    val targetAccount: String? = null
) {
    constructor() : this(
        TransactionType.INCOME, // default value
        "", // default value
        0.0, // default value
        "", // default value
        "", // default value
        "", // default value
        null // default value
    )
}

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER, TRANSFER_INCOMING, TRANSFER_OUTGOING
}