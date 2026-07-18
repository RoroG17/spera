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

/**
 * Résultat métier d'une création de compte (US1).
 */
sealed interface SignUpResult {
    data class Success(val user: User) : SignUpResult
    data object EmailAlreadyUsed : SignUpResult
    data class Error(val message: String) : SignUpResult
}

interface AuthRepository {
    suspend fun signIn(email: String, password: String): SignInResult

    /**
     * Crée un compte à partir des identifiants et du profil [user].
     * L'email de [user] fait foi ; le mot de passe n'est jamais stocké côté profil.
     */
    suspend fun signUp(email: String, password: String, user: User): SignUpResult
}
