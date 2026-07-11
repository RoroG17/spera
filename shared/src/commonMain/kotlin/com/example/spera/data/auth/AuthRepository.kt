package com.example.spera.data.auth

import com.example.spera.models.User

/**
 * Résultat métier d'une tentative de connexion (US2).
 * Les erreurs Supabase seront mappées ici quand le backend sera branché.
 */
sealed interface SignInResult {
    data class Success(val user: User) : SignInResult
    data object InvalidCredentials : SignInResult
    data class Error(val message: String) : SignInResult
}

interface AuthRepository {
    suspend fun signIn(email: String, password: String): SignInResult
}
