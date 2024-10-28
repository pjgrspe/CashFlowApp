package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionData
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionType
import ph.edu.auf.gorospe.patrickjason.cashflow.data.UserRepository
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs.StyledTextField
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.components.loadingscreen.LoadingScreen
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    transactions: List<TransactionData>,
    userRepository: UserRepository = UserRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
) {
    var isLoading by remember { mutableStateOf(true) }
    val transactionsState = remember { mutableStateOf<List<TransactionData>>(emptyList()) }

    LaunchedEffect(true) {
        isLoading = true
        transactionsState.value = userRepository.getTransactions()
        isLoading = false
    }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val groupedTransactions = transactionsState.value
        .sortedBy { dateFormatter.parse(it.date) }
        .groupBy { dateFormatter.format(dateFormatter.parse(it.date) ?: it.date) }
        .toSortedMap(compareBy { it })

    var searchQuery by remember { mutableStateOf("") }

    if (isLoading) {
        LoadingScreen()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF1E1E1E))
        ) {
            if (groupedTransactions.isEmpty()) {
                Text(
                    text = "No transactions available",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Transaction History",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        }
                    }

                    item {
                        StyledTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Search transactions...",
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    val filteredTransactions = groupedTransactions.filter { (date, transactionsForDay) ->
                        transactionsForDay.any { transaction ->
                            (transaction.category ?: "").lowercase().contains(searchQuery.lowercase()) ||
                                    (transaction.account ?: "").lowercase().contains(searchQuery.lowercase()) ||
                                    (transaction.targetAccount ?: "").lowercase().contains(searchQuery.lowercase()) ||
                                    (transaction.date ?: "").lowercase().contains(searchQuery.lowercase())
                        }
                    }.mapValues { (_, transactionsForDay) ->
                        transactionsForDay.filter { transaction ->
                            (transaction.category ?: "").lowercase().contains(searchQuery.lowercase()) ||
                                    (transaction.account ?: "").lowercase().contains(searchQuery.lowercase()) ||
                                    (transaction.targetAccount ?: "").lowercase().contains(searchQuery.lowercase()) ||
                                    (transaction.date ?: "").lowercase().contains(searchQuery.lowercase())
                        }
                    }

                    filteredTransactions.forEach { (day, transactionsForDay) ->
                        item {
                            Text(
                                text = day,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Gray
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(transactionsForDay) { transaction ->
                            TransactionItem(transaction)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun TransactionItem(transaction: TransactionData) {
    val displayCategory = when (transaction.type) {
        TransactionType.TRANSFER_OUTGOING -> "${transaction.category} (to ${transaction.targetAccount ?: "unknown"})"
        TransactionType.TRANSFER_INCOMING -> "${transaction.category} (from ${transaction.account})"
        else -> transaction.category
    }

    val amountText = when (transaction.type) {
        TransactionType.EXPENSE, TransactionType.TRANSFER_OUTGOING -> "-${transaction.amount}"
        else -> transaction.amount.toString()
    }

    val amountColor = when (transaction.type) {
        TransactionType.INCOME, TransactionType.TRANSFER_INCOMING -> Color(0xFF4CAF50) // Green for income
        TransactionType.EXPENSE, TransactionType.TRANSFER_OUTGOING -> Color(0xFFF44336) // Red for expenses
        else -> Color.Gray
    }

    val icon = when (transaction.type) {
        TransactionType.INCOME, TransactionType.TRANSFER_INCOMING -> Icons.Default.TrendingUp
        else -> Icons.Default.TrendingDown
    }

    val iconColor = when (transaction.type) {
        TransactionType.INCOME, TransactionType.TRANSFER_INCOMING -> Color(0xFF4CAF50)
        else -> Color(0xFFF44336)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = Color(0xFF2A2A2A).copy(alpha = 0.8f), // Slight transparency for modern glass-like effect
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = transaction.category,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = displayCategory,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White // White text for dark theme
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = amountText,
            color = amountColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}



