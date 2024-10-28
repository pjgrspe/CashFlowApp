package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.home.sections.latesttransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ph.edu.auf.gorospe.patrickjason.cashflow.data.TransactionData
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.history.TransactionItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LatestTransactionSection(
    transactions: List<TransactionData>,
    navController: NavController,
    onNavigateToHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1E1E1E), // Dark gray for modern dark theme
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
            )
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            // View All text
            Text(
                text = "View All",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(36.dp))
                    .clickable(onClick = { onNavigateToHistory() })
                    .padding(16.dp)
                    .align(Alignment.CenterVertically),
                fontSize = 14.sp // Adjust size for smaller text
            )
        }

        // Check if there are any transactions
        if (transactions.isEmpty()) {
            // No recent transactions message
            Text(
                text = "No recent transactions",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp) // Add some vertical padding
            )
        } else {
            // Date formatting and grouping of transactions
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val groupedTransactions = transactions
                .groupBy { dateFormatter.format(dateFormatter.parse(it.date) ?: it.date) }
                .toSortedMap(compareByDescending { it })

            groupedTransactions.forEach { (day, transactionsForDay) ->

                // Date header
                Text(
                    text = day,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                )

                // Render each transaction item
                transactionsForDay.take(3).forEach { transaction ->
                    TransactionItem(
                        transaction = transaction
                    )
                }
            }
        }
    }
}



