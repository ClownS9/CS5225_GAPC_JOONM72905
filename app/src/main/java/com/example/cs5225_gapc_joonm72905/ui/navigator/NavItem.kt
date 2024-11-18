package com.example.cs5225_gapc_joonm72905.ui.navigator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : NavItem("home", Icons.Filled.Home, "Home")
    data object Block : NavItem("block", Icons.Filled.AppBlocking, "Block")
    data object Settings : NavItem("profile", Icons.Filled.Person, "Profile")
}