package com.example.spera.data.recipes

import com.example.spera.models.Recipe
import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée de [RecipeRepository] (US9 + création, sans backend).
 * Sert les recettes de [RecipeMockData] dans une liste mutable en mémoire
 * pour que les recettes créées apparaissent dans « Mes recettes » (perdues au
 * redémarrage, comme le reste du mock) ; les favoris sont ceux du compte de
 * démo quel que soit l'utilisateur.
 */
class MockRecipeRepository : RecipeRepository {

    private val recipes = RecipeMockData.recipes.toMutableList()

    override suspend fun loadRecipes(user: User): RecipesResult {
        delay(700) // simulation d'un appel réseau

        return RecipesResult.Success(
            recipes = recipes.sortedByDescending { it.date },
            favoriteIds = RecipeMockData.demoFavoriteIds,
        )
    }

    override suspend fun createRecipe(user: User, recipe: Recipe): CreateRecipeResult {
        delay(400) // simulation d'un appel réseau

        recipes += recipe
        return CreateRecipeResult.Success(recipe)
    }
}
