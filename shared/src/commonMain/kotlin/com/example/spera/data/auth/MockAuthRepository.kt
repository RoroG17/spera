package com.example.spera.data.auth

import kotlinx.coroutines.delay

/**
 * Implémentation mockée d'[AuthRepository] (US2, sans backend).
 *
 * Vérifie les identifiants contre [MockData.accounts], simule une latence
 * réseau, et enregistre la session en cas de succès.
 */
class MockAuthRepository(
    private val sessionManager: SessionManager,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): SignInResult {
        delay(600) // simulation d'un appel réseau

        val normalizedEmail = email.trim().lowercase()
        val account = MockData.accounts.firstOrNull { it.email.lowercase() == normalizedEmail }
            ?: return SignInResult.InvalidCredentials

        if (account.password != password) {
            return SignInResult.InvalidCredentials
        }

        sessionManager.setSession(account.user)
        return SignInResult.Success(account.user)
    }
}
