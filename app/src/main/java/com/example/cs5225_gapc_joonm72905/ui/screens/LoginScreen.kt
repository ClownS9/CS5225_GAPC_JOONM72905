package com.example.cs5225_gapc_joonm72905.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cs5225_gapc_joonm72905.auth.model.AuthViewModel
import com.example.cs5225_gapc_joonm72905.datastore.model.FirestoreViewModel
import com.example.cs5225_gapc_joonm72905.ui.layout.Buttons
import com.example.cs5225_gapc_joonm72905.ui.layout.ErrorModal
import com.example.cs5225_gapc_joonm72905.ui.layout.Inputs
import com.example.cs5225_gapc_joonm72905.ui.layout.TextButtons

class LoginScreen(val navController: NavController) {
    private val email = mutableStateOf("")
    private val password = mutableStateOf("")
    private val errorMessage = mutableStateOf<String?>(null)

    @Composable
    fun Show() {
        val authModel: AuthViewModel = hiltViewModel()
        val storeModel: FirestoreViewModel = hiltViewModel()
        val uid by authModel.userId.observeAsState("")
        Component(authModel, storeModel, uid)
    }

    @Composable
    private fun Component(authModel: AuthViewModel, storeModel: FirestoreViewModel, uid: String) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                "Login to gain access our features.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Inputs(email, "E-mail Address", false)
            Spacer(modifier = Modifier.height(24.dp))
            Inputs(password, "Password", true)
            Spacer(modifier = Modifier.height(15.dp))
            Buttons(onBtnAction = { onLogin(authModel, storeModel, uid) }, "Login")
            ErrorModal(errorMessage)
            TextButtons(onBtnAction = { navController.navigate("forgot") }, "Forget Password?")
            TextButtons(
                onBtnAction = { navController.navigate("signup") },
                "Don't have an account? Sign up"
            )
        }
    }

    private fun onLogin(authModel: AuthViewModel, storeModel: FirestoreViewModel, uid: String) {
        authModel.login(
            email.value,
            password.value,
            onSuccess = {
                authModel.refreshUser()
                storeModel.updateStatus(uid, true)
                navController.navigate("home")
            },
            onFailure = { ex ->
                errorMessage.value = ex.localizedMessage
            }
        )
    }

}