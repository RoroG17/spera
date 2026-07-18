package com.example.spera.viewmodels.states

/** Ligne d'ingrédient en cours de saisie (quantité en texte, validée à la création). */
data class IngredientDraft(
    /** Clé stable pour la liste d'édition (pas persistée). */
    val id: Int,
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
)

/**
 * État UI de l'écran « Nouvelle recette », aligné sur le doc "UI State" →
 * Recette : l'état « Creation » (Success / Loading / Error sont portés par la
 * liste, cf. [RecipesUiState] ; « Update » viendra avec l'édition).
 */
sealed interface NewRecipeUiState {

    /** Saisie de la recette. */
    data class Creation(
        val name: String = "",
        val description: String = "",
        val prepMinutes: String = "",
        val cookMinutes: String = "",
        val ingredients: List<IngredientDraft> = listOf(IngredientDraft(id = 1)),
        val shareToFeed: Boolean = false,
        val error: String? = null,
    ) : NewRecipeUiState

    /** Création (et partage éventuel) en cours. */
    data object Submitting : NewRecipeUiState

    /** Recette créée ; [shared] : également partagée sur le fil. */
    data class Created(val shared: Boolean) : NewRecipeUiState
}
