// CardDetailScreen.kt
package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import ph.edu.auf.gorospe.patrickjason.cashflow.domain.AccountViewModel
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs.DeleteDialog
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs.EditAccountDialog
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.mainbalance.formatBalance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CardDetailScreen(
    accountId: String,
    onBackClick: () -> Unit,
    viewModel: AccountViewModel
) {
    val accountDetails by viewModel.accountDetails.observeAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }


    LaunchedEffect(accountId) {
        Log.d("CardDetailScreen", "Fetching details for card ID: $accountId")
        viewModel.getAccountById(accountId)
    }

    accountDetails?.let { currentAccountDetails ->
        if (showEditDialog) {
            EditAccountDialog(
                account = currentAccountDetails,
                onDismiss = { showEditDialog = false },
                onSaveChanges = { updatedAccount ->
                    viewModel.updateAccount(updatedAccount)
                    showEditDialog = false
                    viewModel.getAccountById(accountId) // Refresh account details
                }
            )
        }
        if (showDeleteConfirmation) {
            DeleteDialog(
                onConfirm = {
                    viewModel.deleteAccount(accountId)
                    showDeleteConfirmation = false
                    onBackClick() // Navigate back after deletion
                },
                onDismiss = { showDeleteConfirmation = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable(onClick = onBackClick)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Centered content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 24.dp), // Add padding top to avoid overlapping with back button
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card Details Layout
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(getGradientByColorName(currentAccountDetails.cardColor))
                            .width(300.dp)
                            .height(200.dp)
                            .padding(vertical = 18.dp, horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val imageResource = detectCardImageResource(currentAccountDetails)

                            Image(
                                painter = painterResource(id = imageResource),
                                contentDescription = currentAccountDetails.cardName,
                                modifier = Modifier.width(50.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentAccountDetails.cardName,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "$ ${formatBalance(currentAccountDetails.balance)}",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Only display card number if it's not a "Cash" account
                            if (currentAccountDetails.cardCategory != "Cash") {
                                Text(
                                    text = formatCardNumber(currentAccountDetails.cardNumber), // Format card number with spaces
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Card Information
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "Category: ${currentAccountDetails.cardCategory}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Date Added: ${formatDate(currentAccountDetails.dateCreated)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = { showEditDialog = true } // Set showEditDialog to true
                    ) {
                        Text(
                            text = "Edit Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(
                        onClick = { showDeleteConfirmation = true }
                    ) {
                        Text(
                            text = "Delete Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}


//
//fun formatDate(timestamp: Long): String {
//    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    return sdf.format(Date(timestamp))
//}
//
//// Remove the duplicate formatBalance function
//fun formatBalance(balance: Double): String {
//    return String.format("%,.2f", balance)
//}