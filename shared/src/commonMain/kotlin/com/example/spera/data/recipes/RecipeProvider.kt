package com.example.spera.data.recipes

/**
 * Câblage manuel léger (en attendant Koin), sur le modèle d'`AuthProvider` :
 * une seule instance de [RecipeRepository] partagée dans l'app.
 */
object RecipeProvider {
    val recipeRepository: RecipeRepository by lazy { MockRecipeRepository() }
}
