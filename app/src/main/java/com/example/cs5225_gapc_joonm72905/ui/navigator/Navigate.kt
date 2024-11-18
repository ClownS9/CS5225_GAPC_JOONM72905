package com.example.cs5225_gapc_joonm72905.ui.navigator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cs5225_gapc_joonm72905.R
import com.example.cs5225_gapc_joonm72905.ui.layout.BottomNav
import com.example.cs5225_gapc_joonm72905.ui.screens.ForgotScreen
import com.example.cs5225_gapc_joonm72905.ui.screens.HomeScreen
import com.example.cs5225_gapc_joonm72905.ui.screens.LoginScreen
import com.example.cs5225_gapc_joonm72905.ui.screens.ProfileScreen
import com.example.cs5225_gapc_joonm72905.ui.screens.SignupScreen
import kotlinx.coroutines.delay

class Navigate(private val navController: NavHostController) {
    private val signup = SignupScreen(navController)
    private val forgot = ForgotScreen(navController)
    private val login = LoginScreen(navController)
    private val home = HomeScreen(navController)
    private val profile = ProfileScreen(navController)
    private val authRoute = listOf("home", "block", "profile")

    @Composable
    fun Routes() {
        Scaffold(
            bottomBar = {
                val currentBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry.value?.destination?.route

                if (currentRoute in authRoute) {
                    BottomNav(navController)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(navController = navController, startDestination = "splash") {
                    locate(this)
                }
            }
        }
    }

    private fun locate(builder: NavGraphBuilder) {
        builder.composable("splash") { Splash() }
        builder.composable("login") { login.Show() }
        builder.composable("signup") { signup.Show() }
        builder.composable("forgot") { forgot.Show() }
        builder.composable("home") { home.Show() }
        builder.composable("block") { /** Add later */ }
        builder.composable("profile") { profile.Show() }
    }

    @Composable
    private fun Splash() {
        LaunchedEffect(Unit) {
            delay(3000L)
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
        LogoImage()
    }

    @Composable
    private fun LogoImage() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
        }
    }
}