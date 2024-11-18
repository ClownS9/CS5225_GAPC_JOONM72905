package com.example.cs5225_gapc_joonm72905.ui.layout

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cs5225_gapc_joonm72905.ui.navigator.NavItem
import com.example.cs5225_gapc_joonm72905.ui.theme.CS5225_GAPC_JOONM72905Theme

@Composable
fun BottomNav(navController: NavController) {
    val navItems = listOf(NavItem.Home, NavItem.Block, NavItem.Settings)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        navItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { navController.navigate(navItem.route) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.label,
                        tint = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                    )
                },
                label = {
                    Text(
                        text = navItem.label,
                        color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun BottomNavPreview() {
    CS5225_GAPC_JOONM72905Theme {
        BottomNav(rememberNavController())
    }
}