package com.example.cs5225_gapc_joonm72905.datastore.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs5225_gapc_joonm72905.datastore.FirestoreRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirestoreViewModel @Inject constructor() : ViewModel() {
    private val repository = FirestoreRepository()

    private val _users = MutableStateFlow<Result<QuerySnapshot>?>(null)
    val users = _users

    private val _user = MutableStateFlow<Result<DocumentSnapshot>?>(null)
    val user = _user.asStateFlow()

    private val _userStatusMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val userStatusMap = _userStatusMap.asStateFlow()

    private val _screenTimeLimits = MutableStateFlow<Map<String, Int>>(emptyMap())
    val screenTimeLimits: StateFlow<Map<String, Int>> get() = _screenTimeLimits

    fun fetchUsers() {
        viewModelScope.launch {
            repository.getUsers().collect { result ->
                _users.value = result
            }
        }
    }

    fun listenToUserStatus(userId: String) {
        repository.listenToUserStatus(userId).observeForever { status ->
            _userStatusMap.value = _userStatusMap.value.toMutableMap().apply {
                this[userId] = status
            }
        }
    }

    fun fetchScreenTimeLimit(childId: String) {
        viewModelScope.launch {
            val result = repository.getScreenTimeLimit(childId)
            result.fold(
                onSuccess = { limit ->
                    _screenTimeLimits.value = _screenTimeLimits.value.toMutableMap().apply {
                        this[childId] = limit
                    }
                },
                onFailure = { _screenTimeLimits.value = emptyMap() }
            )
        }
    }

    fun checkParentEmail(
        parentEmail: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.isParentEmailExist(parentEmail)
            result.fold(
                onSuccess = { parentExists ->
                    onSuccess(parentExists)
                },
                onFailure = { ex ->
                    onFailure(ex as Exception)
                }
            )
        }
    }

    fun checkEmail(email: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            val result = repository.isEmailExist(email)
            result.fold(
                onSuccess = { exists ->
                    onSuccess(exists)
                },
                onFailure = { ex ->
                    onFailure(ex as Exception)
                }
            )
        }
    }

    fun fetchUsersByParentEmail(parentEmail: String) {
        viewModelScope.launch {
            repository.getUsersByParentEmail(parentEmail).collect { result ->
                _users.value = result
            }
        }
    }

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            repository.getUser(userId).collect { result ->
                _user.value = result
            }
        }
    }

    fun updateStatus(userId: String, isOnline: Boolean) {
        viewModelScope.launch {
            val result = repository.updateUserStatus(userId, isOnline)
            result.fold(
                onSuccess = {
                    Log.d("FirestoreViewModel", "User status updated successfully.")
                },
                onFailure = { ex ->
                    Log.e(
                        "FirestoreViewModel",
                        "Failed to update user status: ${ex.localizedMessage}"
                    )
                }
            )
        }
    }

    fun updateScreenTimeLimit(childId: String, newTime: Int) {
        viewModelScope.launch {
            val result = repository.setScreenTimeLimit(childId, newTime)
            result.fold(
                onSuccess = {
                    _screenTimeLimits.value = _screenTimeLimits.value.toMutableMap().apply {
                        this[childId] = newTime
                    }
                },
                onFailure = { /** Handle failure */ }
            )
        }
    }

    fun clearScreenTimeLimit(childId: String) {
        viewModelScope.launch {
            repository.clearScreenTimeLimit(childId)
            _screenTimeLimits.value = _screenTimeLimits.value.toMutableMap().apply {
                remove(childId)
            }
        }
    }

    fun addUser(userData: Map<String, Any>) {
        viewModelScope.launch {
            val result = repository.addUser(userData)
            if (result.isSuccess) {
                fetchUsers()
            }
        }
    }
}