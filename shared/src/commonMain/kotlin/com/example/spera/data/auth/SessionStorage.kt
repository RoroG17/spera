package com.example.spera.data.auth

/**
 * Stockage clé-valeur persistant, spécifique à la plateforme (US2).
 *
 * Android : `SharedPreferences`. iOS : `NSUserDefaults`.
 * Permet à la session de survivre à la fermeture de l'application : on y range
 * l'instantané de l'utilisateur connecté, rechargé au prochain démarrage.
 */
expect class SessionStorage() {
    fun read(): String?
    fun write(value: String?)
}
