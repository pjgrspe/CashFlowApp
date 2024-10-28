package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.home.sections.mainbalance

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.auf.gorospe.patrickjason.cashflow.data.UserRepository

@Composable
fun MainBalanceSection(
    userRepository: UserRepository,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransferClick: () -> Unit
) {
    var totalBalance by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        totalBalance = userRepository.getTotalBalance()
    }

    // Update balance when income or expense is added
    val updateBalance: @Composable () -> Unit = {
        LaunchedEffect(Unit) {
            totalBalance = userRepository.getTotalBalance()
        }
    }

    var showIncomeDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    val formattedBalance2 = if (totalBalance.toString().contains(".") && totalBalance.toString().split(".")[1] == "0") {
        totalBalance.toString().split(".")[0] // Display whole number only
    } else {
        totalBalance.toString() // Display the original balance
    }
    val formattedBalance = formatBalance(totalBalance)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
//            .background(
//                color = Color(0xFF6A1B9A), // Custom purple color
//                shape = RoundedCornerShape(45.dp)
//            )
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Main Balance Title
            Text(
                text = "Main balance",
                color = Color.White.copy(alpha = 0.7f), // Semi-transparent white
                style = MaterialTheme.typography.bodyMedium
            )

            val customBalanceTextStyle = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 55.sp, // Adjust the size as needed
                fontWeight = FontWeight.Bold
            )

            // Balance Amount
            Text(
                text = "₱${formattedBalance}",
                color = Color.White,
                style = customBalanceTextStyle,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Income, Expense, Transfer options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),

                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Income
                OptionItem("Income", Icons.Default.ArrowUpward) {
                    onIncomeClick() // Trigger the onIncomeClick callback
                }

                // Divider
                VerticalDivider()

                // Expense
                OptionItem("Expense", Icons.Default.ArrowDownward) {
                    onExpenseClick()
                }

                // Divider
                VerticalDivider()

                // Transfer
                OptionItem("Transfer", Icons.Default.Sync){
                    onTransferClick()
                }
            }
        }
    }
}

@Composable
fun OptionItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.2f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                onClick = {
                    isClicked = true
                    onClick()
                    isClicked = false
                },
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 8.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

fun formatBalance(balance: Double): String {
    return when {
        balance >= 1_000_000 -> String.format("%.1fM", balance / 1_000_000) // For millions
        balance >= 1_000 -> String.format("%,d", balance.toInt()) // For thousands (no decimal)
        balance % 1 == 0.0 -> balance.toInt().toString() // Return as Int if no decimal part
        else -> balance.toString() // For amounts with decimals
    }
}




@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(50.dp) // Adjust height as needed
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.5f)) // Semi-transparent white divider
    )
}