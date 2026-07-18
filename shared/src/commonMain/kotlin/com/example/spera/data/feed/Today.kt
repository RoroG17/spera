package com.example.spera.data.feed

/**
 * Date du jour au format ISO `yyyy-MM-dd` (même format que les dates du fil,
 * trié lexicographiquement). Utilisée pour dater les posts créés (US6).
 */
expect fun todayIso(): String
