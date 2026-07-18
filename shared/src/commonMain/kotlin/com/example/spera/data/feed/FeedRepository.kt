package com.example.spera.data.feed

import com.example.spera.models.User

/**
 * Résultat métier du chargement d'une page du fil (US4).
 * Les erreurs Supabase seront mappées ici quand le backend sera branché.
 */
sealed interface FeedResult {
    data class Success(val posts: List<FeedItem>, val hasMore: Boolean) : FeedResult
    data class Error(val message: String) : FeedResult
}

interface FeedRepository {
    /**
     * Renvoie une page du fil d'actualité : les posts (séances + recettes) publiés
     * par les abonnements de [user], triés du plus récent au plus ancien.
     *
     * @param page index de page à partir de 0.
     * @param pageSize nombre de posts par page (chargement optimisé / infinite scroll).
     */
    suspend fun loadFeed(user: User, page: Int, pageSize: Int): FeedResult
}
