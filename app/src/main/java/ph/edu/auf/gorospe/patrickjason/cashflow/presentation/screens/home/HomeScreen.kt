// HomeScreen.kt
package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionData
import ph.edu.auf.gorospe.patrickjason.cashflow.data.UserRepository
import ph.edu.auf.gorospe.patrickjason.cashflow.domain.AccountViewModel
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.latesttransactions.LatestTransactionSection
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.CardsSection
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.ADD_CARD
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.greeting.GreetingSection
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.mainbalance.MainBalanceSection
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.mainbalance.dialogs.AddIncomeDialog
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.mainbalance.dialogs.ExpenseDialog
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.mainbalance.dialogs.TransferDialog
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.components.loadingscreen.LoadingScreen

@Composable
fun HomeScreen(navController: NavController, userRepository: UserRepository, accountViewModel: AccountViewModel) {
    var userName by remember { mutableStateOf("") }
    var balance by remember { mutableDoubleStateOf(0.00) }
    var accounts by remember { mutableStateOf(listOf<AccountCard>()) }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var transactions by remember { mutableStateOf(listOf<TransactionData>()) } // Add transactions state
    var isLoading by remember { mutableStateOf(true) } // Add loading state

    val coroutineScope = rememberCoroutineScope()

    fun refreshAppState() {
        coroutineScope.launch {
            isLoading = true // Set loading to true before fetching data
            val (fetchedUserName, fetchedBalance) = userRepository.getUserNameAndBalance()
            userName = fetchedUserName
            balance = fetchedBalance
            accounts = userRepository.getAccountCards()
            transactions = userRepository.getTransactions() // Fetch transactions from Firestore
            Log.d("HomeScreen", "App state refreshed: userName=$userName, balance=$balance, accounts=$accounts")
            isLoading = false // Set loading to false after data is fetched
        }
    }

    LaunchedEffect(Unit) {
        refreshAppState()
    }

    var showAddCardDialog by remember { mutableStateOf(false) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }

    if (isLoading) {
        LoadingScreen() // Display loading screen if data is being fetched
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(310.dp) // Adjust height for the blue header section
                    .clip(RoundedCornerShape(36.dp)) // Round only the bottom corners
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF24713E),
                                Color(0xFF29BDAB),
                                Color(0xFF517FA9)
                            ),
                            center = Offset(0f, 0f)
                        )
                    )
                ) {
                    Column {
                        GreetingSection(
                            userName = userName,
                            navController = navController,
                            userRepository = userRepository
                        )
                        MainBalanceSection(
                            userRepository = userRepository,
                            onIncomeClick = { showIncomeDialog = true },
                            onExpenseClick = { showExpenseDialog = true },
                            onTransferClick = { showTransferDialog = true }
                        )
                    }
                }
            }

            item {
                CardsSection(
                    navController = navController,
                    accounts = accounts + ADD_CARD,
                    onAddAccount = { newAccount ->
                        coroutineScope.launch {
                            val firebaseId = userRepository.addAccountCard(newAccount)
                            newAccount.id = firebaseId
                            Log.d("HomeScreen", "New account added with Firebase ID: $firebaseId")
                            refreshAppState()
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LatestTransactionSection(
                    transactions = transactions,
                    navController = navController,
                    onNavigateToHistory = {
                        navController.navigate("history") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }

        if (showIncomeDialog) {
            AddIncomeDialog(
                accounts = accounts,
                onDismiss = { showIncomeDialog = false },
                onAddTransaction = { incomeData ->
                    coroutineScope.launch {
                        userRepository.addIncome(
                            amount = incomeData.amount.toString(),
                            account = accounts.firstOrNull { it.cardName == incomeData.account } ?: return@launch,
                            category = incomeData.category,
                            date = incomeData.date,
                            notes = incomeData.notes
                        )
                        refreshAppState()
                        showIncomeDialog = false
                        refreshAppState()
                    }
                }
            )
        }

        if (showExpenseDialog) {
            ExpenseDialog(
                accounts = accounts,
                onDismiss = { showExpenseDialog = false },
                onAddTransaction = { expenseData ->
                    coroutineScope.launch {
                        userRepository.addExpense(
                            amount = expenseData.amount.toString(),
                            account = accounts.firstOrNull { it.cardName == expenseData.account } ?: return@launch,
                            category = expenseData.category,
                            date = expenseData.date,
                            notes = expenseData.notes
                        )
                        refreshAppState()
                        showExpenseDialog = false
                    }
                }
            )
        }

        if (showTransferDialog) {
            TransferDialog(
                accounts = accounts,
                onDismiss = { showTransferDialog = false },
                onAddTransaction = { transferData ->
                    Log.d("HomeScreen", "Starting transfer: $transferData")
                    coroutineScope.launch {
                        val sourceAccount =
                            accounts.firstOrNull { it.cardName == transferData.account } ?: run {
                                Log.e("HomeScreen", "Source account not found: ${transferData.account}")
                                return@launch
                            }
                        val targetAccount =
                            accounts.firstOrNull { it.cardName == transferData.targetAccount } ?: run {
                                Log.e(
                                    "HomeScreen",
                                    "Target account not found: ${transferData.targetAccount}"
                                )
                                return@launch
                            }
                        Log.d("HomeScreen", "Source account: ${sourceAccount.cardName}, Target account: ${targetAccount.cardName}")
                        userRepository.transfer(
                            amount = transferData.amount.toString(),
                            source = sourceAccount,
                            target = targetAccount
                        )
                        refreshAppState()
                        showTransferDialog = false
                        Log.d("HomeScreen", "Transfer completed")
                    }
                }
            )
        }
    }
}