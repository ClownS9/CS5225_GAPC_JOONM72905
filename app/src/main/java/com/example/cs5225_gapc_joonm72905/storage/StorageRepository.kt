package com.example.cs5225_gapc_joonm72905.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepository @Inject constructor() {
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadProfPic(userId: String, uri: Uri?): Result<String> {
        return try {
            val timestamp = System.currentTimeMillis()
            val ref = storage.reference.child("profile_images/$userId$timestamp.jpg")

            ref.putFile(uri!!).await()
            val downloadUrl = ref.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}