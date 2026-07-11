package com.example.spera.data.auth

import platform.Foundation.NSUserDefaults

actual class SessionStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun read(): String? = defaults.stringForKey(KEY)

    actual fun write(value: String?) {
        if (value == null) {
            defaults.removeObjectForKey(KEY)
        } else {
            defaults.setObject(value, KEY)
        }
    }

    private companion object {
        const val KEY = "spera_current_user"
    }
}
