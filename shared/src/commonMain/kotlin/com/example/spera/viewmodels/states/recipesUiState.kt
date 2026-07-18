package com.example.spera.viewmodels.states

import com.example.spera.models.Recipe

/** Filtre de la liste des recettes (chips de la maquette 8). */
enum class RecipeFilter(val label: String) {
    All("Toutes"),
    Mine("Mes recettes"),
    Favorites("Favoris"),
}

/**
 * État UI de l'onglet Recettes (US9), aligné sur le doc "UI State" → Recette
 * (Success / Loading / Error ; les états Creation / Update viendront avec les
 * US d'édition de recette).
 */
sealed interface RecipesUiState {

    /** Chargement des recettes. */
    data object Loading : RecipesUiState

    /** Échec du chargement (aucune recette affichable). */
    data class Error(val message: String) : RecipesUiState

    /**
     * Recettes chargées. [recipes] est déjà filtrée selon [filter] ;
     * [favoriteIds] pilote le marqueur favori des cartes (toggle en US8).
     */
    data class Success(
        val recipes: List<Recipe>,
        val filter: RecipeFilter,
        val favoriteIds: Set<String>,
    ) : RecipesUiState
}
