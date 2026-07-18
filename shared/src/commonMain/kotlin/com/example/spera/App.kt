package com.example.spera

import MyAppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.spera.data.auth.AuthProvider
import com.example.spera.ui.navigation.MainScaffold
import com.example.spera.ui.screens.login.LoginScreen
import com.example.spera.ui.screens.signup.SignUpScreen
import com.example.spera.ui.screens.welcome.WelcomeScreen

private enum class AuthScreen { Welcome, Login, SignUp }

@Composable
@Preview
fun App() {
    MyAppTheme {
        // Le maintien de session pilote la navigation (US2).
        val currentUser by AuthProvider.sessionManager.currentUser.collectAsState()

        val user = currentUser
        if (user != null) {
            // Coque authentifiée : header + footer communs à toutes les sections.
            MainScaffold(
                user = user,
                onLogout = { AuthProvider.sessionManager.clear() },
            )
        } else {
            // Flux d'authentification : accueil -> connexion (US2) / création (US1).
            var screen by remember { mutableStateOf(AuthScreen.Welcome) }
            when (screen) {
                AuthScreen.Welcome -> WelcomeScreen(
                    onCreateAccount = { screen = AuthScreen.SignUp },
                    onLogin = { screen = AuthScreen.Login },
                )

                AuthScreen.Login -> LoginScreen(
                    onNavigateToSignUp = { screen = AuthScreen.SignUp },
                    onNavigateBack = { screen = AuthScreen.Welcome },
                )

                AuthScreen.SignUp -> SignUpScreen(
                    onNavigateToLogin = { screen = AuthScreen.Login },
                    onNavigateBack = { screen = AuthScreen.Welcome },
                )
            }
        }
    }
}
