package com.example.cs5225_gapc_joonm72905.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cs5225_gapc_joonm72905.auth.model.AuthViewModel
import com.example.cs5225_gapc_joonm72905.datastore.model.FirestoreViewModel
import com.example.cs5225_gapc_joonm72905.ui.layout.Cards
import com.example.cs5225_gapc_joonm72905.ui.layout.GreetingsSection
import com.example.cs5225_gapc_joonm72905.ui.layout.LineWithText
import com.example.cs5225_gapc_joonm72905.ui.layout.SetScreenTimeModal
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.delay

class HomeScreen(val navController: NavController) {

    @Composable
    fun Show() {
        val authModel: AuthViewModel = hiltViewModel()
        val storeModel: FirestoreViewModel = hiltViewModel()
        val userState by storeModel.user.collectAsState()
        val uid by authModel.userId.observeAsState("")

        LaunchedEffect(uid) {
            storeModel.fetchUser(uid)
        }

        when {
            userState?.isSuccess == true -> {
                val user = userState?.getOrNull()
                val role = user?.getString("role")
                if (role == "parent") {
                    ParentSection(authModel, storeModel, user)
                } else {
                    ChildSection(authModel, storeModel, user)
                }
            }

            userState?.isFailure == true -> {
                Text(userState?.exceptionOrNull()?.localizedMessage.toString())
            }

            else -> {
                CircularProgressIndicator()
            }
        }
    }

    @Composable
    private fun ParentSection(
        authModel: AuthViewModel,
        storeModel: FirestoreViewModel,
        user: DocumentSnapshot?
    ) {
        val toggleState = remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
        val childState by storeModel.users.collectAsState()
        val userStatusMap by storeModel.userStatusMap.collectAsState()
        val showScreenTimeModal = remember { mutableStateOf(false) }
        val selectedChildId = remember { mutableStateOf<String?>(null) }
        val remainingTimeMap = remember { mutableStateMapOf<String, Long>() }
        val isLoggedIn by authModel.isLoggedIn.observeAsState(false)
        val isVerified by authModel.isVerified.observeAsState(false)
        val uid by authModel.userId.observeAsState("")
        val mail by authModel.email.observeAsState("")

        if (isLoggedIn && isVerified) {
            LaunchedEffect(uid) {
                storeModel.listenToUserStatus(uid)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                val username = user?.getString("username")
                val imgUrl = user?.getString("imageUrl")
                val isOnline = userStatusMap[uid] ?: false

                GreetingsSection(
                    greetings = "Hello, ",
                    content1 = username.toString(),
                    content2 = mail,
                    imgUrl = imgUrl.toString(),
                    isOnline = isOnline
                )

                LineWithText("My Kids")

                when {
                    childState?.isSuccess == true -> {
                        val child = childState?.getOrNull()?.documents ?: emptyList()
                        LazyColumn {
                            items(child, key = { user -> user.id }) { user ->
                                val username = user.getString("username")
                                val imgUrl = user.getString("imageUrl")
                                val userId = user.id
                                val isOnline = userStatusMap[userId] ?: false

                                // Fetch screen time limit for the child
                                LaunchedEffect(userId) {
                                    storeModel.fetchScreenTimeLimit(userId)
                                }

                                // Observe screen time limits and start times
                                val screenTimeLimit by storeModel.screenTimeLimits.collectAsState()
                                val screenTimeStart =
                                    user.getLong("screenTimeStart") ?: System.currentTimeMillis()

                                // Set up a timer for this child
                                LaunchedEffect(userId, screenTimeLimit[userId]) {
                                    while (true) {
                                        delay(1000) // Delay for 1 second
                                        val totalTimeLimitInSeconds =
                                            (screenTimeLimit[userId] ?: 0) * 60
                                        val timeElapsedInSeconds =
                                            (System.currentTimeMillis() - screenTimeStart) / 1000
                                        val remainingTimeInSeconds =
                                            totalTimeLimitInSeconds - timeElapsedInSeconds
                                        remainingTimeMap[userId] = remainingTimeInSeconds
                                    }
                                }

                                // Get the remaining time for this child
                                val remainingTimeInSeconds = remainingTimeMap[userId] ?: 0

                                // Format the remaining time
                                val minutesRemaining = remainingTimeInSeconds / 60
                                val secondsRemaining = remainingTimeInSeconds % 60

                                val remainingTimeText = when {
                                    remainingTimeInSeconds > 0 -> {
                                        "${minutesRemaining}m ${secondsRemaining}s"
                                    }

                                    remainingTimeInSeconds <= 0 -> {
                                        "Time's up!"
                                    }

                                    else -> {
                                        "00m 00s"
                                    }
                                }

                                // Toggle state dynamically based on screenTimeLimit
                                val currentToggleState = toggleState.value[userId]
                                    ?: ((screenTimeLimit[userId] ?: 0) > 0)

                                val onToggleChange: (Boolean) -> Unit = { newState ->
                                    toggleState.value = toggleState.value.toMutableMap().apply {
                                        this[userId] = newState
                                    }

                                    if (newState) {
                                        selectedChildId.value = userId
                                        showScreenTimeModal.value = true
                                    } else {
                                        storeModel.clearScreenTimeLimit(userId) // Clear screen time limit
                                    }
                                }

                                // Display the cards with the remaining time or "Time's up!" if toggle is off
                                Cards(
                                    imgUrl = imgUrl.toString(),
                                    contentTitle = username.toString(),
                                    contentDesc = if (!currentToggleState) "Time's up!" else remainingTimeText,
                                    checked = currentToggleState,
                                    onChange = onToggleChange,
                                    hasToggle = true,
                                    isOnline = isOnline
                                )
                            }
                        }
                    }

                    childState?.isFailure == true -> {
                        Text(childState?.exceptionOrNull()?.localizedMessage.toString())
                    }

                    else -> {
                        CircularProgressIndicator()
                    }
                }

                if (showScreenTimeModal.value) {
                    SetScreenTimeModal(
                        onDismiss = { showScreenTimeModal.value = false },
                        onTimeSet = { time ->
                            selectedChildId.value?.let { childId ->
                                storeModel.updateScreenTimeLimit(childId, time)
                            }
                            showScreenTimeModal.value = false
                        }
                    )
                }

                LaunchedEffect(mail) {
                    storeModel.fetchUsersByParentEmail(mail)
                }
            }
        }
    }


    @Composable
    private fun ChildSection(
        authModel: AuthViewModel,
        storeModel: FirestoreViewModel,
        user: DocumentSnapshot?
    ) {
        val isLoggedIn by authModel.isLoggedIn.observeAsState(false)
        val isVerified by authModel.isVerified.observeAsState(false)
        val uid by authModel.userId.observeAsState("")
        val mail by authModel.email.observeAsState("")
        val userStatusMap by storeModel.userStatusMap.collectAsState()

        if (isLoggedIn && isVerified) {
            val username = user?.getString("username")
            val imgUrl = user?.getString("imageUrl")
            val isOnline = userStatusMap[uid] ?: false

            LaunchedEffect(uid) {
                storeModel.listenToUserStatus(uid)
            }

            LaunchedEffect(uid) {
                storeModel.fetchScreenTimeLimit(uid)
            }

            val screenTimeStart = user?.getLong("screenTimeStart") ?: System.currentTimeMillis()

            // Set up a mutable state to hold the remaining time
            val remainingTimeInSecondsState = remember { mutableStateOf(0L) }

            // Periodically update the remaining time every second
            LaunchedEffect(screenTimeStart) {
                while (true) {
                    delay(1000) // Delay for 1 second
                    val totalTimeLimitInSeconds =
                        (user?.getLong("screenTimeLimit") ?: 0) * 60 // Limit in seconds
                    val timeElapsedInSeconds =
                        (System.currentTimeMillis() - screenTimeStart) / 1000 // Time elapsed since screen time started
                    val remainingTimeInSeconds = totalTimeLimitInSeconds - timeElapsedInSeconds
                    remainingTimeInSecondsState.value = remainingTimeInSeconds
                }
            }

            // Format the remaining time for display
            val minutesRemaining = remainingTimeInSecondsState.value / 60
            val secondsRemaining = remainingTimeInSecondsState.value % 60

            val remainingTimeText = when {
                remainingTimeInSecondsState.value > 0 -> {
                    // Countdown is still active
                    "${minutesRemaining}m ${secondsRemaining}s"
                }

                remainingTimeInSecondsState.value <= 0 -> {
                    // Time is up
                    "Time's up!"
                }

                else -> {
                    // Default case if calculation fails
                    "00m 00s"
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                // Display greetings and profile information
                GreetingsSection(
                    greetings = "Hello, ",
                    content1 = username.toString(),
                    content2 = mail,
                    imgUrl = imgUrl.toString(),
                    isOnline = isOnline
                )

                // Line with text for section header
                LineWithText("Screen Time")

                // Display remaining screen time
                Text(
                    "Remaining Screen Time: $remainingTimeText",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

}