// TransferDialog.kt
package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.mainbalance.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionData
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionType
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs.InputLabel
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs.StyledTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TransferDialog.kt
@Composable
fun TransferDialog(
    accounts: List<AccountCard>,
    onDismiss: () -> Unit,
    onAddTransaction: (TransactionData) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedSourceAccount by remember { mutableStateOf<AccountCard?>(null) }
    var selectedTargetAccount by remember { mutableStateOf<AccountCard?>(null) }
    var expandedSourceAccount by remember { mutableStateOf(false) }
    var expandedTargetAccount by remember { mutableStateOf(false) }

    // Error states for validations
    var amountError by remember { mutableStateOf(false) }
    var sourceAccountError by remember { mutableStateOf(false) }
    var targetAccountError by remember { mutableStateOf(false) }

    // Function to handle target account selection
    fun onTargetAccountSelected(account: AccountCard) {
        selectedTargetAccount = account
        expandedTargetAccount = false
        targetAccountError = false
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Transfer Funds",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Source Account Dropdown
                InputLabel(label = "Source Account")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { expandedSourceAccount = true }
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSourceAccount?.cardName ?: "Select Source Account",
                            color = if (selectedSourceAccount == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Icon",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = expandedSourceAccount,
                        onDismissRequest = { expandedSourceAccount = false }
                    ) {
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedSourceAccount = account
                                    expandedSourceAccount = false
                                    sourceAccountError = false
                                },
                                text = {
                                    Text(
                                        text = account.cardName,
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }
                if (sourceAccountError) {
                    Text("Source Account is required", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Target Account Dropdown
                InputLabel(label = "Target Account")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { expandedTargetAccount = true }
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTargetAccount?.cardName ?: "Select Target Account",
                            color = if (selectedTargetAccount == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Icon",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = expandedTargetAccount,
                        onDismissRequest = { expandedTargetAccount = false }
                    ) {
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                onClick = {
                                    onTargetAccountSelected(account)
                                },
                                text = {
                                    Text(
                                        text = account.cardName,
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }
                if (targetAccountError) {
                    Text("Target Account is required", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Amount Input
                InputLabel(label = "Amount")
                StyledTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = "Enter Amount",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    errorMessage = if (amountError) "Invalid amount" else null
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                sourceAccountError = selectedSourceAccount == null
                targetAccountError = selectedTargetAccount == null
                amountError = amount.isEmpty() || amount.toDoubleOrNull() == null
                if (!amountError && !sourceAccountError && !targetAccountError) {
                    val newTransaction = TransactionData(
                        type = TransactionType.TRANSFER,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        account = selectedSourceAccount?.cardName ?: "",
                        category = "Transfer",
                        date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                        notes = "Transfer to ${selectedTargetAccount?.cardName}",
                        targetAccount = selectedTargetAccount?.cardName
                    )
                    onAddTransaction(newTransaction)
                    onDismiss()
                }
            }) {
                Text("Transfer")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
        }
    )
}