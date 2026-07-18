package com.example.spera.data.auth

import com.example.spera.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Maintien de session (US2).
 *
 * Détient l'utilisateur connecté et le persiste via [SessionStorage] pour que la
 * session survive à la fermeture de l'app : au démarrage, la session est restaurée
 * depuis le stockage plateforme (SharedPreferences Android / NSUserDefaults iOS),
 * de sorte qu'un utilisateur déjà connecté n'a pas à se ré-authentifier.
 */
class SessionManager(
    private val storage: SessionStorage = SessionStorage(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val _currentUser = MutableStateFlow(restore())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean
        get() = _currentUser.value != null

    fun setSession(user: User) {
        _currentUser.value = user
        storage.write(json.encodeToString(user.toStored()))
    }

    fun clear() {
        _currentUser.value = null
        storage.write(null)
    }

    /** Recharge la session persistée au démarrage, ou `null` si aucune / illisible. */
    private fun restore(): User? {
        val raw = storage.read() ?: return null
        return runCatching { json.decodeFromString<StoredUser>(raw).toUser() }.getOrNull()
    }
}
