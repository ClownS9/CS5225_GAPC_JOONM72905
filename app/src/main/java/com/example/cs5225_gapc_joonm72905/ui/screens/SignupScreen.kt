package com.example.cs5225_gapc_joonm72905.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.cs5225_gapc_joonm72905.storage.model.StorageModel
import com.example.cs5225_gapc_joonm72905.ui.layout.Buttons
import com.example.cs5225_gapc_joonm72905.ui.layout.ErrorModal
import com.example.cs5225_gapc_joonm72905.ui.layout.Inputs
import com.example.cs5225_gapc_joonm72905.ui.layout.RadioButtonData
import com.example.cs5225_gapc_joonm72905.ui.layout.RadioButtons
import com.example.cs5225_gapc_joonm72905.ui.layout.SuccessModal
import com.example.cs5225_gapc_joonm72905.ui.layout.TextButtons
import com.example.cs5225_gapc_joonm72905.ui.layout.UploadPic
import com.example.cs5225_gapc_joonm72905.utils.FormatUtils

class SignupScreen(private val navController: NavController) {
    private val email = mutableStateOf("")
    private val password = mutableStateOf("")
    private val username = mutableStateOf("")
    private val parentEmail = mutableStateOf("")
    private val selectedRole = mutableStateOf("parent")
    private val profilePicUri = mutableStateOf<Uri?>(null)
    private val successMessage = mutableStateOf<String?>(null)
    private val errorMessage = mutableStateOf<String?>(null)
    private val radioList = listOf(
        RadioButtonData("Parent", "parent"),
        RadioButtonData("Child", "child")
    )

    @Composable
    fun Show() {
        val authModel: AuthViewModel = hiltViewModel()
        val storeModel: FirestoreViewModel = hiltViewModel()
        val storageModel: StorageModel = hiltViewModel()
        val uid by authModel.userId.observeAsState("")
        Component(authModel, storeModel, storageModel, uid)
    }

    @Composable
    private fun Component(
        authModel: AuthViewModel,
        storeModel: FirestoreViewModel,
        storageModel: StorageModel,
        uid: String
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                "Create an account to gain access.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            UploadPic(onProfileSelected = { uri -> profilePicUri.value = uri }, profilePicUri)
            Spacer(modifier = Modifier.height(24.dp))
            Inputs(username, "Username", isMask = false)
            Spacer(modifier = Modifier.height(24.dp))
            Inputs(email, "E-mail Address", isMask = false)
            Spacer(modifier = Modifier.height(24.dp))
            Inputs(password, "Password", isMask = true)
            Spacer(modifier = Modifier.height(50.dp))
            RadioButtons(radioList, "Select Role:", selectedRole, parentEmail)
            Buttons(onBtnAction = {
                onSignup(authModel, storeModel, storageModel, uid)
            }, "Sign Up")
            ErrorModal(errorMessage)
            SuccessModal(successMessage, onClick = {
                reset()
                navController.navigate("login")
            })
            TextButtons(
                onBtnAction = { navController.navigate("login") },
                "Already have an account? Log in"
            )
        }
    }

    private fun reset() {
        successMessage.value = null
        errorMessage.value = null
        email.value = ""
        password.value = ""
        username.value = ""
        parentEmail.value = ""
        selectedRole.value = "parent"
        profilePicUri.value = null
    }

    private fun onSignup(
        authModel: AuthViewModel,
        storeModel: FirestoreViewModel,
        storageModel: StorageModel,
        uid: String
    ) {

        if (profilePicUri.value == null) {
            errorMessage.value = "No profile picture selected."
            return
        }

        if (!FormatUtils.isValidEmail(parentEmail.value) && selectedRole.value == "child") {
            errorMessage.value = "The parents email address is badly formatted."
            return
        }

        storeModel.checkParentEmail(
            parentEmail = parentEmail.value,
            onSuccess = { parentExists ->
                if (!parentExists && selectedRole.value == "child") {
                    errorMessage.value = "No parent email found in the database."
                } else {
                    authModel.signup(
                        email = email.value,
                        password = password.value,
                        onSuccess = {
                            onAction(authModel, storeModel, storageModel, uid)
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

    private fun onAction(
        authModel: AuthViewModel,
        storeModel: FirestoreViewModel,
        storageModel: StorageModel,
        uid: String
    ) {
        authModel.verifyEmail()
        onUpload(uid, storageModel) { imageUrl ->
            val userData = mutableMapOf(
                "username" to username.value,
                "email" to email.value,
                "parentEmail" to parentEmail.value,
                "role" to selectedRole.value,
                "screenTimeLimit" to 0,
                "screenTimeStart" to 0,
                "blockedApps" to emptyList<String>(),
                "isBlocked" to false,
                "status" to "offline",
                "imageUrl" to imageUrl
            )

            storeModel.addUser(userData)
        }
        successMessage.value = "Sign up successful! Please verify your email."
    }

    private fun onUpload(
        uid: String,
        storageModel: StorageModel,
        onComplete: (String) -> Unit,
    ) {
        profilePicUri.value.let { uri ->
            storageModel.uploadProfData(
                userId = uid,
                uri = uri,
                onSuccess = { downloadUrl ->
                    onComplete(downloadUrl)
                },
                onFailure = { message ->
                    errorMessage.value = message
                }
            )
        }
    }
}