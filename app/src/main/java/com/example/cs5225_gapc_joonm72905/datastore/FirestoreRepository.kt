package com.example.cs5225_gapc_joonm72905.datastore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {
    private val store = FirebaseFirestore.getInstance()
    private val collectUser = store.collection("users")

    suspend fun addUser(data: Map<String, Any>): Result<Unit> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: UUID.randomUUID().toString()

        return try {
            collectUser.document(userId).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getScreenTimeLimit(childId: String): Result<Int> {
        return try {
            val document = collectUser.document(childId).get().await()
            val screenTimeLimit = document.getLong("screenTimeLimit")?.toInt() ?: 0
            Result.success(screenTimeLimit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsersByParentEmail(parentEmail: String): Flow<Result<QuerySnapshot>> = flow {
        try {
            val query = collectUser
                .whereEqualTo("parentEmail", parentEmail)
                .get()
                .await()

            emit(Result.success(query))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun isParentEmailExist(parentEmail: String): Result<Boolean> {
        return try {
            val query = collectUser
                .whereEqualTo("role", "parent")
                .whereEqualTo("email", parentEmail)
                .get()
                .await()

            val isExist = query.isEmpty.not()
            Result.success(isExist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isEmailExist(email: String): Result<Boolean> {
        return try {
            val query = collectUser
                .whereEqualTo("email", email)
                .get()
                .await()
            val isExist = query.isEmpty.not()
            Result.success(isExist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsers(): Flow<Result<QuerySnapshot>> = flow {
        try {
            val snapshot = collectUser.get().await()
            emit(Result.success(snapshot))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun updateUserStatus(userId: String, isOnline: Boolean): Result<Unit> {
        return try {
            collectUser.document(userId)
                .update(mapOf("status" to if (isOnline) "online" else "offline")).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setScreenTimeLimit(childId: String, timeInMinutes: Int): Result<Unit> {
        return try {
            collectUser.document(childId)
                .update(
                    mapOf(
                        "screenTimeLimit" to timeInMinutes,
                        "screenTimeStart" to System.currentTimeMillis()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearScreenTimeLimit(childId: String): Result<Unit> {
        return try {
            collectUser.document(childId)
                .update(
                    mapOf(
                        "screenTimeLimit" to FieldValue.delete(),
                        "screenTimeStart" to FieldValue.delete()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenToUserStatus(userId: String): LiveData<Boolean> {
        val statusLiveData = MutableLiveData<Boolean>()
        val userStatusRef = collectUser.document(userId)

        userStatusRef.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                statusLiveData.value = false
                return@addSnapshotListener
            }

            val status = snapshot.getString("status") ?: "offline"
            statusLiveData.value = status == "online"
        }
        return statusLiveData
    }

    suspend fun getUser(userId: String): Flow<Result<DocumentSnapshot>> = flow {
        try {
            val snapshot = collectUser.document(userId).get().await()
            emit(Result.success(snapshot))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}