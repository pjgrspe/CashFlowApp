// CardsSection.kt
package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ph.edu.auf.gorospe.patrickjason.cashflow.R
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs.AddAccountScreen
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.mainbalance.formatBalance
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.BlueEnd
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.BlueStart
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.GrayEnd
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.GrayStart
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.GreenEnd
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.GreenStart
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.OrangeEnd
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.OrangeStart
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.PurpleEnd
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.PurpleStart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val ADD_CARD = AccountCard(
    id = "9999",
    cardCategory = "ADD",
    cardNumber = "",
    cardName = "",
    balance = 0.0,
    cardColor = "Gray"
)

fun getGradientByColorName(colorName: String): Brush {
    return when (colorName.lowercase()) {
        "green" -> getGradient(GreenStart, GreenEnd)
        "purple" -> getGradient(PurpleStart, PurpleEnd)
        "orange" -> getGradient(OrangeStart, OrangeEnd)
        "blue" -> getGradient(BlueStart, BlueEnd) // Example using Gray for "blue"
        else -> getGradient(GrayStart, GrayEnd) // Default to Gray if color name is unknown
    }
}

fun getGradient(
    startColor: Color,
    endColor: Color,
): Brush {
    return Brush.horizontalGradient(
        colors = listOf(
            startColor,
            endColor
        )
    )
}



// Function to format card numbers with spaces
fun formatCardNumber(cardNumber: String): String {
    return cardNumber.chunked(4).joinToString(" ")
}

fun detectCardImageResource(card: AccountCard): Int {
    return when (card.cardCategory) {
        "Cash" -> R.drawable.ic_cash
        "Card" -> {
            when {
                card.cardNumber.startsWith("4") -> R.drawable.ic_visa
                card.cardNumber.startsWith("51") || card.cardNumber.startsWith("52") ||
                        card.cardNumber.startsWith("53") || card.cardNumber.startsWith("54") ||
                        card.cardNumber.startsWith("55") || card.cardNumber.startsWith("2221") ||
                        card.cardNumber.startsWith("2222") || card.cardNumber.startsWith("2223") ||
                        card.cardNumber.startsWith("2224") || card.cardNumber.startsWith("2225") ||
                        card.cardNumber.startsWith("2226") || card.cardNumber.startsWith("2227") ||
                        card.cardNumber.startsWith("2228") || card.cardNumber.startsWith("2229") ||
                        card.cardNumber.startsWith("223") || card.cardNumber.startsWith("224") ||
                        card.cardNumber.startsWith("225") || card.cardNumber.startsWith("226") ||
                        card.cardNumber.startsWith("227") || card.cardNumber.startsWith("228") ||
                        card.cardNumber.startsWith("229") || card.cardNumber.startsWith("23") ||
                        card.cardNumber.startsWith("24") || card.cardNumber.startsWith("25") ||
                        card.cardNumber.startsWith("26") || card.cardNumber.startsWith("270") ||
                        card.cardNumber.startsWith("271") || card.cardNumber.startsWith("2720") -> R.drawable.ic_mastercard
                else -> R.drawable.ic_visa // Default fallback if card type isn't matched
            }
        }
        else -> R.drawable.ic_visa // Default fallback if no category is matched
    }
}

@Composable
fun CardsSection(navController: NavController, accounts: List<AccountCard>, onAddAccount: (AccountCard) -> Unit) {
    var showAddCardDialog by remember { mutableStateOf(false) }
    var accountList by remember { mutableStateOf(accounts) }

    LaunchedEffect(accounts) {
        accountList = accounts
        Log.d("CardsSection", "Account list updated: $accountList")
    }

    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "Accounts",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow {
            items(accountList.size) { index ->
                CardItem(accountList[index]) { account ->
                    Log.d("CardsSection", "Card clicked: ${account.id}")
                    if (account.id == "9999") {  // Handle the "Add" card
                        showAddCardDialog = true
                    } else { // Navigate to the details screen with the specific account
                        navController.navigate("card_details/${account.id}") {
                            launchSingleTop = true // Avoid multiple instances in the backstack
                        }
                    }
                }
            }
        }

        if (showAddCardDialog) {
            AddAccountScreen(
                onDismiss = { showAddCardDialog = false },
                onAddAccount = { newAccount ->
                    onAddAccount(newAccount)
                    accountList = accountList + newAccount
                    showAddCardDialog = false
                    Log.d("CardsSection", "New account added: $newAccount")
                }
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun CardItem(card: AccountCard, onCardClick: (AccountCard) -> Unit) {
    val gradient = getGradientByColorName(card.cardColor) // Convert cardColor String to Brush

    Box(
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable { onCardClick(card) }
    ) {
        if (card.cardCategory == "ADD") {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(gradient)
                    .width(250.dp)
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add +",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(gradient)
                    .width(250.dp)
                    .height(160.dp)
                    .clickable { onCardClick(card) }
                    .padding(vertical = 18.dp, horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val imageResource = detectCardImageResource(card)

                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = card.cardName,
                    modifier = Modifier.width(50.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = card.cardName,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$ ${formatBalance(card.balance)}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Only display card number if it's not a "Cash" account
                if (card.cardCategory != "Cash") {
                    Text(
                        text = formatCardNumber(card.cardNumber), // Format card number with spaces
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}