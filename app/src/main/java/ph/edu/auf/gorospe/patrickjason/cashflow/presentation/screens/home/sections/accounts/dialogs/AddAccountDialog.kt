package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs

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
fun AddAccountScreen(
    onDismiss: () -> Unit,
    onAddAccount: (AccountCard) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Cash") }
    var color by remember { mutableStateOf("Select Color") }
    var number by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var colorMenuExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var balanceError by remember { mutableStateOf(false) }
    var numberError by remember { mutableStateOf(false) }

    val colorOptions = listOf("Green", "Blue", "Orange", "Purple")

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Add New Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
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
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(8.dp)
                        )
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
                            color = if (color == "Select Color")
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = colorMenuExpanded,
                        onDismissRequest = { colorMenuExpanded = false },
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
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
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                InputLabel(label = "Category")
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                if (category == "Bank") {
                    Spacer(modifier = Modifier.height(8.dp))
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
                        errorMessage = if (numberError) {
                            when {
                                number.length < 13 -> "Card Number must be at least 13 digits"
                                number.length > 16 -> "Card Number must be 16 digits or less"
                                else -> "Invalid Card Number"
                            }
                        } else null,
                        visualTransformation = CreditCardVisualTransformation()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isEmpty()) {
                        nameError = true
                    } else if (!nameError && !balanceError && !numberError) {
                        val newAccount = AccountCard(
                            id = UUID.randomUUID().toString(),
                            cardCategory = category,
                            cardNumber = number,
                            cardName = name,
                            balance = balance.toDoubleOrNull() ?: 0.0,
                            cardColor = color,
                            dateCreated = System.currentTimeMillis()
                        )
                        onAddAccount(newAccount)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Add Account")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                // Remove indicator colors to eliminate the underline
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = keyboardOptions,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    if (isError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                ),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon?.let {
                @Composable {
                    Box(modifier = Modifier.size(24.dp)) {
                        it()
                    }
                }
            }
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun InputLabel(label: String) {
    Text(
        text = label,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun CategoryButton(
    categoryName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = categoryName,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(16) // Limit input to 16 characters
        val formattedText = trimmed.chunked(4).joinToString(" ")
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when (offset) {
                    in 0..3 -> offset
                    in 4..7 -> (offset + 1).coerceAtMost(formattedText.length)
                    in 8..11 -> (offset + 2).coerceAtMost(formattedText.length)
                    in 12..15 -> (offset + 3).coerceAtMost(formattedText.length)
                    else -> formattedText.length // Ensure it is within the range
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when (offset) {
                    in 0..3 -> offset
                    in 4..8 -> (offset - 1).coerceAtMost(trimmed.length)
                    in 9..13 -> (offset - 2).coerceAtMost(trimmed.length)
                    in 14..18 -> (offset - 3).coerceAtMost(trimmed.length)
                    else -> trimmed.length // Ensure it is within the range
                }
            }
        }
        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}


