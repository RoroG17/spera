package com.example.spera.viewmodels.states

import com.example.spera.data.feed.FeedItem
import com.example.spera.data.feed.PostType

/**
 * État UI de l'écran « Nouvelle publication » (US6). Le doc "UI State" ne
 * détaille pas cette feature : on s'aligne sur les états "Creation" des
 * features Recette / Entraînement et le pattern [LoginUiState].
 */
sealed interface NewPostUiState {

    /** Saisie en cours. [error] non nul = message à afficher sous le formulaire. */
    data class Editing(
        val type: PostType? = null,
        val name: String = "",
        val description: String = "",
        val error: String? = null,
    ) : NewPostUiState

    /** Publication en cours. */
    data object Submitting : NewPostUiState

    /** Post publié : l'UI revient au fil et le recharge. */
    data class Published(val post: FeedItem) : NewPostUiState
}
