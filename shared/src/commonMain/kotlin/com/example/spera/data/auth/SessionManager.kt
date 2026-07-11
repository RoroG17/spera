package com.example.spera.data.auth

import com.example.spera.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Maintien de session (US2).
 *
 * Détient l'utilisateur connecté en mémoire pour la durée de vie de l'app.
 * La session survit à la navigation ; la persistance inter-redémarrage
 * (DataStore Android / UserDefaults iOS) reste à brancher côté plateforme.
 */
class SessionManager {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean
        get() = _currentUser.value != null

    fun setSession(user: User) {
        _currentUser.value = user
    }

    fun clear() {
        _currentUser.value = null
    }
}
