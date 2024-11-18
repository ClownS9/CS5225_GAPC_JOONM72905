package com.example.cs5225_gapc_joonm72905.storage.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs5225_gapc_joonm72905.storage.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageModel @Inject constructor() : ViewModel() {
    private val repository = StorageRepository()

    fun uploadProfData(
        userId: String,
        uri: Uri?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.uploadProfPic(userId, uri)

            result.onSuccess { downloadUrl ->
                onSuccess(downloadUrl)
            }.onFailure { ex ->
                onFailure(ex.localizedMessage!!)
            }
        }
    }
}