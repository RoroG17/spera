package com.example.spera.viewmodels.states

import com.example.spera.data.feed.FeedItem

/**
 * État UI du fil d'actualité / écran d'accueil (US4), aligné sur le doc
 * "UI State" → Actualité (Success / Loading / Error).
 */
sealed interface HomeUiState {

    /** Chargement de la première page. */
    data object Loading : HomeUiState

    /** Échec du chargement initial (aucun post affichable). */
    data class Error(val message: String) : HomeUiState

    /**
     * Fil chargé.
     * [isLoadingMore] : une page supplémentaire est en cours de chargement.
     * [canLoadMore] : il reste des posts à charger (infinite scroll).
     */
    data class Success(
        val posts: List<FeedItem>,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = true,
    ) : HomeUiState
}
