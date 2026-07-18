package com.example.spera.data.recipes

import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée de [RecipeRepository] (US9, sans backend).
 * Renvoie les recettes de [RecipeMockData] ; les favoris sont ceux du compte
 * de démo quel que soit l'utilisateur (suffisant tant que l'app est mockée).
 */
class MockRecipeRepository : RecipeRepository {

    override suspend fun loadRecipes(user: User): RecipesResult {
        delay(700) // simulation d'un appel réseau

        return RecipesResult.Success(
            recipes = RecipeMockData.recipes.sortedByDescending { it.date },
            favoriteIds = RecipeMockData.demoFavoriteIds,
        )
    }
}
