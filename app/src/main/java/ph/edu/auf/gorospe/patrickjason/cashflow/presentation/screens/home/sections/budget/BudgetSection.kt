package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.auf.gorospe.patrickjason.cashflow.data.Budget
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.BlueStart
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.GreenStart
import ph.edu.auf.gorospe.patrickjason.cashflow.ui.theme.OrangeStart

val budgetList = listOf(
    Budget(
        icon = Icons.Rounded.StarHalf,
        name = "My\nBusiness",
        background = OrangeStart
    ),

    Budget(
        icon = Icons.Rounded.Wallet,
        name = "My\nWallet",
        background = BlueStart
    ),

    Budget(
        icon = Icons.Rounded.MonetizationOn,
        name = "My\nAnalytics",
        background = GreenStart
    ),
)

@Preview(showBackground = true)
@Composable
fun BudgetSection() {
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ){
        Text(
            text = "Budget",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyRow{
            items(budgetList.size){
                BudgetItem(it)
            }
        }
    }
}

@Composable
fun BudgetItem(
    index: Int
){
    val budget = budgetList[index]
    var lastItemPaddingEnd = 0.dp
    if(index == budgetList.size - 1){
        lastItemPaddingEnd = 16.dp
    }

    Box(modifier = Modifier.padding(start = 16.dp, end = lastItemPaddingEnd)){
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .size(120.dp)
                .clickable { /*TODO*/ }
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(budget.background)
                    .padding(6.dp)
            ){
                Icon(
                    imageVector = budget.icon,
                    contentDescription = budget.name,
                    tint = Color.White
                )
            }

            Text(
                text = budget.name,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}