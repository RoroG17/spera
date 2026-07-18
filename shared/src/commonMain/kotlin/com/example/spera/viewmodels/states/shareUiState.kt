package com.example.spera.viewmodels.states

/** État du bouton « Partager sur le fil » (détail recette / détail séance). */
sealed interface ShareUiState {

    data object Idle : ShareUiState

    data object Sharing : ShareUiState

    /**
     * [postId] est sur le fil (nouveau partage ou déjà présent). L'id permet
     * à l'UI d'ignorer un état résiduel d'un autre post (le VM survit à
     * l'écran).
     */
    data class Shared(val postId: String) : ShareUiState

    data class Error(val message: String) : ShareUiState
}
