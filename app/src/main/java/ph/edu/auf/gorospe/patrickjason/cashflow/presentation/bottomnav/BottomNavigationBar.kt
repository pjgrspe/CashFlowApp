package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.bottomnav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import ph.edu.auf.gorospe.patrickjason.cashflow.data.BottomNavigationItem

val items = listOf(
    BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        badgeCount = null
    ),
    BottomNavigationItem(
        title = "History",
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt,
        badgeCount = null
    ),
    BottomNavigationItem(
        title = "Alerts",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        badgeCount = 2
    ),
    BottomNavigationItem(
        title = "Profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        badgeCount = null
    )
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    fun updateSelectedItem(route: String) {
        selectedItemIndex = items.indexOfFirst { it.title.equals(route, ignoreCase = true) }
    }

    NavigationBar {
        Row(
            modifier = Modifier
                .background(color = Color(0xFF1E1E1E), // Dark gray for modern dark theme
            )
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItemIndex == index,
                    onClick = {
                        selectedItemIndex = index
                        navController.navigate(item.title)
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if(item.badgeCount != null){
                                    Badge {
                                        Text(text = item.badgeCount.toString())
                                    }
                                }
                            }
                        ){
                            Icon(
                                imageVector = if(index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                tint = Color.White
                            )
                        }
                    },
                    label = {
                        Text(text = item.title,
                            color = Color.White
                        )
                    }
                )
            }
        }

        // Observe navigation changes to update the selected item
        LaunchedEffect(navController) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                updateSelectedItem(destination.route ?: "")
            }
        }
    }
}