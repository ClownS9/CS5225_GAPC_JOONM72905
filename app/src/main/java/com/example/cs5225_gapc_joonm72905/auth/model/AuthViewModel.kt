package com.example.cs5225_gapc_joonm72905.auth.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs5225_gapc_joonm72905.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    // LiveData for user authentication state
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _isVerified = MutableLiveData<Boolean>()
    val isVerified: LiveData<Boolean> get() = _isVerified

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    // Initialize user data by fetching from the repository
    init {
        updateUserData()
    }

    private fun updateUserData() {
        _userId.value = authRepository.getUserId()
        _email.value = authRepository.getUserEmail()
        _isVerified.value = authRepository.isVerified()
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    // Methods for handling authentication actions

    fun verifyEmail() {
        authRepository.sendEmailVerification()
    }

    fun refreshUser() {
        authRepository.refreshUser()
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        viewModelScope.launch {
            val result = authRepository.userLogin(email, password)
            if (result.isSuccess) {
                onSuccess()
                updateUserData()  // Update user data after login
            } else {
                onFailure(result.exceptionOrNull() as Exception)
            }
        }
    }

    fun signup(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        viewModelScope.launch {
            val result = authRepository.userSignup(email, password)
            if (result.isSuccess) {
                onSuccess()
                updateUserData()  // Update user data after signup
            } else {
                onFailure(result.exceptionOrNull() as Exception)
            }
        }
    }

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        viewModelScope.launch {
            val result = authRepository.changePassword(email)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onFailure(result.exceptionOrNull() as Exception)
            }
        }
    }

    fun signOut() {
        authRepository.logout()
        updateUserData()  // Update user data after logout
    }
}
