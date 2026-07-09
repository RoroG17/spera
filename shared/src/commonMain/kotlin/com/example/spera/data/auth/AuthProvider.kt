package com.example.spera.data.auth

/**
 * Câblage manuel léger (en attendant Koin) : une seule instance de
 * [SessionManager] et d'[AuthRepository] partagée dans l'app.
 */
object AuthProvider {
    val sessionManager: SessionManager by lazy { SessionManager() }
    val authRepository: AuthRepository by lazy { MockAuthRepository(sessionManager) }
}
