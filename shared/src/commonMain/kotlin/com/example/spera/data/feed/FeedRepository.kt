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

/** Type de publication choisi sur l'écran « Nouvelle publication » (US6). */
enum class PostType { Training, Recipe }

/** Résultat métier de la création d'un post (US6). */
sealed interface CreatePostResult {
    data class Success(val post: FeedItem) : CreatePostResult
    data class Error(val message: String) : CreatePostResult
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

    /**
     * Publie un nouveau post ([Recipe] ou [Training] selon [type]) daté du jour,
     * au nom de [author]. Quand Supabase sera branché : insertion dans la table
     * `recette` ou `entrainement` (RLS : auteur = utilisateur authentifié).
     */
    suspend fun createPost(
        author: User,
        type: PostType,
        name: String,
        description: String,
        photo: String,
    ): CreatePostResult
}
