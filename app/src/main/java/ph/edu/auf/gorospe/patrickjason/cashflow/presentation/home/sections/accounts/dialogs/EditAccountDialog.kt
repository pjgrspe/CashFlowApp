package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.home.sections.accounts.dialogs

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountDialog(
    account: AccountCard,
    onDismiss: () -> Unit,
    onSaveChanges: (AccountCard) -> Unit
) {
    var name by remember { mutableStateOf(account.cardName) }
    var category by remember { mutableStateOf(account.cardCategory) }
    var color by remember { mutableStateOf(account.cardColor) }
    var number by remember { mutableStateOf(account.cardNumber) }
    var balance by remember { mutableStateOf(account.balance.toString()) }
    var colorMenuExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var balanceError by remember { mutableStateOf(false) }
    var numberError by remember { mutableStateOf(false) }

    val colorOptions = listOf("Green", "Blue", "Orange", "Purple")

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Edit Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                InputLabel(label = "Account Name")
                StyledTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    placeholder = "Enter Account Name",
                    isError = nameError,
                    errorMessage = if (nameError) "Account Name is required" else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                InputLabel(label = "Balance")
                StyledTextField(
                    value = balance,
                    onValueChange = {
                        balance = it
                        balanceError = it.toDoubleOrNull() == null
                    },
                    placeholder = "Enter Balance",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = balanceError,
                    errorMessage = if (balanceError) "Enter a valid balance" else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                InputLabel(label = "Card Color")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { colorMenuExpanded = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = color,
                            color = if (color == "Select Color") Color.Gray else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Icon",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = colorMenuExpanded,
                        onDismissRequest = { colorMenuExpanded = false },
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ) {
                        colorOptions.forEach { colorOption ->
                            DropdownMenuItem(
                                onClick = {
                                    color = colorOption
                                    colorMenuExpanded = false
                                },
                                text = {
                                    Text(
                                        text = colorOption,
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                InputLabel(label = "Category")
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("Bank", "Cash", "Other").forEach { categoryName ->
                        CategoryButton(
                            categoryName = categoryName,
                            isSelected = category == categoryName,
                            onSelect = { category = categoryName }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (category == "Bank") {
                    InputLabel(label = "Card Number")
                    StyledTextField(
                        value = number,
                        onValueChange = { input ->
                            number = input.filter { it.isDigit() }.take(16)
                            numberError = number.length != 16
                        },
                        placeholder = "Enter Card Number",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = numberError,
                        errorMessage = if (numberError) "Invalid Card Number" else null,
                        visualTransformation = CreditCardVisualTransformation()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isEmpty()) {
                    nameError = true
                } else if (!nameError && !balanceError && !numberError) {
                    val updatedAccount = account.copy(
                        cardName = name,
                        cardCategory = category,
                        cardNumber = number,
                        balance = balance.toDoubleOrNull() ?: 0.0,
                        cardColor = color
                    )
                    onSaveChanges(updatedAccount)
                    onDismiss()
                }
            }) {
                Text("Save Changes")
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
