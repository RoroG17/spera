package com.example.spera.data.auth

import android.content.Context

/**
 * Détient le `Context` applicatif nécessaire au stockage persistant Android.
 * À initialiser au démarrage (voir `MainActivity`) avant tout accès à la session.
 */
object AndroidAppContext {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    internal fun peek(): Context? = appContext
}

actual class SessionStorage {
    // Absent en preview Compose (contexte non initialisé) : on dégrade en no-op.
    private val prefs = AndroidAppContext.peek()
        ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual fun read(): String? = prefs?.getString(KEY, null)

    actual fun write(value: String?) {
        val editor = prefs?.edit() ?: return
        if (value == null) editor.remove(KEY) else editor.putString(KEY, value)
        editor.apply()
    }

    private companion object {
        const val PREFS_NAME = "spera_session"
        const val KEY = "current_user"
    }
}
