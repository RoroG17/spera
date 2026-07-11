package com.example.spera.viewmodels.states

import com.example.spera.models.User

/**
 * État UI de l'écran de connexion (US2), aligné sur le doc "UI State".
 */
sealed interface LoginUiState {
    /** Saisie en cours. [error] non nul = message à afficher sous le formulaire. */
    data class Editing(
        val email: String = "",
        val password: String = "",
        val error: String? = null,
    ) : LoginUiState

    /** Appel d'authentification en cours. */
    data object Loading : LoginUiState

    /** Connexion réussie, session ouverte. */
    data class Success(val user: User) : LoginUiState
}
