package com.example.spera.data.auth

import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée d'[AuthRepository] (US1/US2, sans backend).
 *
 * Les comptes vivent en mémoire : la liste initiale vient de [MockData], et les
 * comptes créés via [signUp] s'y ajoutent pour la durée de vie de l'app.
 */
class MockAuthRepository(
    private val sessionManager: SessionManager,
) : AuthRepository {

    private val accounts: MutableList<MockAccount> = MockData.accounts.toMutableList()

    override suspend fun signIn(email: String, password: String): SignInResult {
        delay(600) // simulation d'un appel réseau

        val normalizedEmail = email.trim().lowercase()
        val account = accounts.firstOrNull { it.email.lowercase() == normalizedEmail }
            ?: return SignInResult.InvalidCredentials

        if (account.password != password) {
            return SignInResult.InvalidCredentials
        }

        sessionManager.setSession(account.user)
        return SignInResult.Success(account.user)
    }

    override suspend fun signUp(email: String, password: String, user: User): SignUpResult {
        delay(600) // simulation d'un appel réseau

        val normalizedEmail = email.trim().lowercase()
        if (accounts.any { it.email.lowercase() == normalizedEmail }) {
            return SignUpResult.EmailAlreadyUsed
        }

        val account = MockAccount(email = normalizedEmail, password = password, user = user)
        accounts.add(account)
        sessionManager.setSession(user)
        return SignUpResult.Success(user)
    }
}
