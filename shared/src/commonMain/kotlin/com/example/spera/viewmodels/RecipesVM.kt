package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.SessionManager
import com.example.spera.data.recipes.RecipeProvider
import com.example.spera.data.recipes.RecipeRepository
import com.example.spera.data.recipes.RecipesResult
import com.example.spera.models.Recipe
import com.example.spera.viewmodels.states.RecipeFilter
import com.example.spera.viewmodels.states.RecipesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de l'onglet Recettes (US9) : charge les recettes visibles et
 * applique le filtre Toutes / Mes recettes / Favoris localement.
 * Référence de patron : [LoginVM] / [HomeVM].
 */
class RecipesVM(
    private val recipeRepository: RecipeRepository = RecipeProvider.recipeRepository,
    private val sessionManager: SessionManager = AuthProvider.sessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    /** Jeu complet (non filtré), conservé pour changer de filtre sans recharger. */
    private var allRecipes: List<Recipe> = emptyList()
    private var favoriteIds: Set<String> = emptySet()
    private var userId: String? = null

    init {
        load()
    }

    /** (Re)charge les recettes puis applique le filtre « Toutes ». */
    fun load() {
        _uiState.value = RecipesUiState.Loading

        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            if (user == null) {
                _uiState.value = RecipesUiState.Error("Session expirée, reconnecte-toi.")
                return@launch
            }
            userId = user.id
            when (val result = recipeRepository.loadRecipes(user)) {
                is RecipesResult.Success -> {
                    allRecipes = result.recipes
                    favoriteIds = result.favoriteIds
                    _uiState.value = RecipesUiState.Success(
                        recipes = allRecipes,
                        filter = RecipeFilter.All,
                        favoriteIds = favoriteIds,
                    )
                }

                is RecipesResult.Error ->
                    _uiState.value = RecipesUiState.Error(result.message)
            }
        }
    }

    /** Applique un filtre sur le jeu déjà chargé (pas d'appel réseau). */
    fun onFilterSelect(filter: RecipeFilter) {
        val current = _uiState.value as? RecipesUiState.Success ?: return
        val filtered = when (filter) {
            RecipeFilter.All -> allRecipes
            RecipeFilter.Mine -> allRecipes.filter { it.users.id == userId }
            RecipeFilter.Favorites -> allRecipes.filter { it.id in favoriteIds }
        }
        _uiState.value = current.copy(recipes = filtered, filter = filter)
    }
}
