package com.example.spera

import MyAppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.spera.data.auth.AuthProvider
import com.example.spera.ui.screens.home.HomeScreen
import com.example.spera.ui.screens.login.LoginScreen

@Composable
@Preview
fun App() {
    MyAppTheme {
        // Le maintien de session pilote la navigation (US2).
        val currentUser by AuthProvider.sessionManager.currentUser.collectAsState()

        val user = currentUser
        if (user == null) {
            LoginScreen()
        } else {
            HomeScreen(
                user = user,
                onLogout = { AuthProvider.sessionManager.clear() },
            )
        }
    }
}
