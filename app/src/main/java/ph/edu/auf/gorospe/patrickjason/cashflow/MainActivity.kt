// MainActivity.kt
package ph.edu.auf.gorospe.patrickjason.cashflow

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionData
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.components.bottomnav.BottomNavigationBar
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.HomeScreen
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.login.LoginScreen
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.register.RegisterScreen
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.history.HistoryScreen
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.CardDetailScreen
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.CardsSection
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.CashFlowTheme
import ph.edu.auf.gorospe.patrickjason.cashflow.data.UserRepository
import ph.edu.auf.gorospe.patrickjason.cashflow.domain.AccountViewModel
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.ADD_CARD
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.notification.NotificationScreen
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.profile.ProfileScreen

class MainActivity : ComponentActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        userRepository = UserRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
        accountViewModel = ViewModelProvider(this, userRepository.provideAccountViewModelFactory(userRepository)).get(AccountViewModel::class.java)

        setContent {
            CashFlowTheme(darkTheme = true) {
                SetBarColor(color = Color(0xFF1E1E1E))
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1E1E1E)
                ) {
                    CashFlowApp(accountViewModel)
                }
            }
        }
    }
    @Composable
    private fun SetBarColor(color: Color){
        val systemUIController = rememberSystemUiController()
        SideEffect {
            systemUIController.setSystemBarsColor(color = color)
        }
    }

    private fun initializeComponents() {
        userRepository = UserRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
        accountViewModel = ViewModelProvider(this, userRepository.provideAccountViewModelFactory(userRepository)).get(AccountViewModel::class.java)
    }

    fun onUserLoggedOut() {
        userRepository.logout()
        initializeComponents() // Reinitialize components for the new user
    }
}

@Composable
fun CashFlowApp(accountViewModel: AccountViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userRepository = UserRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
    var cards by remember { mutableStateOf(listOf<AccountCard>()) }
    var transactions by remember { mutableStateOf(listOf<TransactionData>()) }
    val coroutineScope = rememberCoroutineScope()
    val user by remember { mutableStateOf(userRepository.getCurrentUser()) }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cards = userRepository.getAccountCards()
            transactions = userRepository.getTransactions()
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf("login", "register")) {
                BottomNavigationBar(navController)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (user != null) "home" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("history") { HistoryScreen(navController, transactions) }
            composable("alerts") { NotificationScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("home") {
                HomeScreen(
                    navController = navController,
                    userRepository = userRepository,
                    accountViewModel = accountViewModel
                )
            }
            composable(
                "card_details/{accountId}",
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) }
            ) { backStackEntry ->
                val accountId = backStackEntry.arguments?.getString("accountId")
                Log.d("MainActivity", "Navigating to CardDetailScreen with accountId: $accountId")
                if (accountId != null) {
                    CardDetailScreen(accountId = accountId, onBackClick = { navController.popBackStack() }, viewModel = accountViewModel)
                } else {
                    Log.e("MainActivity", "Account ID is null")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCashFlowApp() {
    CashFlowTheme {
        CashFlowApp(
            accountViewModel = AccountViewModel(UserRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())))
    }
}