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
import com.example.cs5225_gapc_joonm72905.ui.layout.SuccessModal
import com.example.cs5225_gapc_joonm72905.ui.layout.TextButtons

class ForgotScreen(val navController: NavController) {

    private val email = mutableStateOf("")
    private val errorMessage = mutableStateOf<String?>(null)
    private val successMessage = mutableStateOf<String?>(null)

    @Composable
    fun Show() {
        val authModel: AuthViewModel = hiltViewModel()
        val storeModel: FirestoreViewModel = hiltViewModel()
        Component(authModel, storeModel)
    }

    @Composable
    private fun Component(authModel: AuthViewModel, storeModel: FirestoreViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Forget Password",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                "An instruction will be given to reset password ",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Inputs(email, "E-mail Address", isMask = false)
            Buttons(onBtnAction = { onForgot(authModel, storeModel) }, "Reset Password")
            ErrorModal(errorMessage)
            TextButtons(onBtnAction = { navController.navigate("login") }, "Back To Login")
            SuccessModal(successMessage, onClick = {
                reset()
                navController.navigate("login")
            })
        }
    }

    private fun reset() {
        successMessage.value = null
        errorMessage.value = null
        email.value = ""
    }

    private fun onForgot(authModel: AuthViewModel, storeModel: FirestoreViewModel) {
        storeModel.checkEmail(
            email = email.value,
            onSuccess = { exists ->
                if (!exists) {
                    errorMessage.value = "No account found with this email."
                } else {
                    authModel.resetPassword(
                        email = email.value,
                        onSuccess = {
                            successMessage.value = "Password reset email sent."
                        },
                        onFailure = { ex ->
                            errorMessage.value = ex.localizedMessage
                        }
                    )
                }
            },
            onFailure = { ex ->
                errorMessage.value = ex.localizedMessage
            }
        )
    }
}