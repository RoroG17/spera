package com.example.spera.data.recipes

import com.example.spera.models.Recipe
import com.example.spera.models.User

/**
 * Résultat métier du chargement des recettes (US9).
 * Les erreurs Supabase seront mappées ici quand le backend sera branché.
 *
 * [favoriteIds] : ids des recettes favorites de l'utilisateur (liste
 * `favoriteRecipes` de `data.txt`) — le toggle favori lui-même relève d'US8.
 */
sealed interface RecipesResult {
    data class Success(val recipes: List<Recipe>, val favoriteIds: Set<String>) : RecipesResult
    data class Error(val message: String) : RecipesResult
}

/** Résultat métier de la création d'une recette. */
sealed interface CreateRecipeResult {
    data class Success(val recipe: Recipe) : CreateRecipeResult
    data class Error(val message: String) : CreateRecipeResult
}

interface RecipeRepository {
    /**
     * Renvoie les recettes visibles par [user] (les siennes + celles des autres
     * membres), triées de la plus récente à la plus ancienne, ainsi que ses
     * favoris. Les filtres (Toutes / Mes recettes / Favoris) s'appliquent côté
     * ViewModel.
     */
    suspend fun loadRecipes(user: User): RecipesResult

    /**
     * Enregistre [recipe] dans les recettes de [user] (liste `recipes` de
     * `data.txt`). Le partage éventuel sur le fil passe par `FeedRepository`.
     */
    suspend fun createRecipe(user: User, recipe: Recipe): CreateRecipeResult
}
