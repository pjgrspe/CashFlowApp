package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.home.sections.mainbalance.dialogs

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionData
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionType
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.home.sections.accounts.dialogs.InputLabel
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.home.sections.accounts.dialogs.StyledTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeDialog(
    accounts: List<AccountCard>,
    onDismiss: () -> Unit,
    onAddTransaction: (TransactionData) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<AccountCard?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var expandedAccount by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    // Error states for validations
    var amountError by remember { mutableStateOf(false) }
    var accountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    val categories = listOf("Salary", "Savings", "Investment", "Gift", "Other")
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Date and time format

    val successColor = Color(0xFF4CAF50)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Add Income",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = successColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Amount Input
                InputLabel(label = "Amount")
                StyledTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = it.isEmpty() || it.toDoubleOrNull() == null
                    },
                    placeholder = "Enter Amount",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = if (amountError) "Amount is required" else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Select Account Dropdown
                InputLabel(label = "Select Account")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { expandedAccount = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedAccount?.cardName ?: "Select Account",
                            color = if (selectedAccount == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Icon",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = expandedAccount,
                        onDismissRequest = { expandedAccount = false }
                    ) {
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedAccount = account
                                    expandedAccount = false
                                    accountError = false
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
                if (accountError) {
                    Text("Account is required", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))


                // Select Category Dropdown
                InputLabel(label = "Category")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { expandedCategory = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedCategory ?: "Select Category",
                            color = if (selectedCategory == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Icon",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedCategory = category
                                    expandedCategory = false
                                    categoryError = false
                                },
                                text = {
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }
                if (categoryError) {
                    Text("Category is required", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date Picker
                InputLabel(label = "Date")
                Box(
                    modifier = Modifier.clickable {
                        DatePickerDialog(
                            context,
                            { _, selectedYear, selectedMonth, selectedDay ->
                                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                                    GregorianCalendar(selectedYear, selectedMonth, selectedDay).time
                                )
                                date = selectedDate
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                ) {
                    StyledDateField(
                        value = date,
                        onValueChange = { date = it },
                        placeholder = "Select Date",
                        isError = false,
                        errorMessage = null
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notes Input
                InputLabel(label = "Notes")
                StyledTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Enter Notes",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                accountError = selectedAccount == null
                amountError = amount.isEmpty() || amount.toDoubleOrNull() == null
                categoryError = selectedCategory == null

                if (!amountError && !accountError && !categoryError) {
                    // Set to full date and time if not selected
                    if (date.isEmpty()) {
                        date = dateFormat.format(Date())
                    }

                    val newTransaction = TransactionData(
                        type = TransactionType.INCOME,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        account = selectedAccount?.cardName ?: "",
                        category = selectedCategory ?: "",
                        date = date,
                        notes = notes
                    )
                    onAddTransaction(newTransaction)
                    onDismiss()
                }
            }) {
                Text("Add Income")
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

@Composable
fun StyledDateField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    errorMessage: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, if (isError) Color.Red else Color.Gray, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (value.isEmpty()) placeholder else value,
            color = if (value.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
        )
    }
}

data class IncomeData(
    val account: String,
    val amount: Double,
    val category: String,
    val date: String,
    val notes: String
)
