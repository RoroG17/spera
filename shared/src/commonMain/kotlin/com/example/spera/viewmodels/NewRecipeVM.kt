package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.SessionManager
import com.example.spera.data.feed.FeedItem
import com.example.spera.data.feed.FeedProvider
import com.example.spera.data.feed.FeedRepository
import com.example.spera.data.feed.ShareResult
import com.example.spera.data.feed.todayIso
import com.example.spera.data.recipes.CreateRecipeResult
import com.example.spera.data.recipes.RecipeProvider
import com.example.spera.data.recipes.RecipeRepository
import com.example.spera.models.Ingredient
import com.example.spera.models.IngredientQuantity
import com.example.spera.models.Recipe
import com.example.spera.viewmodels.states.IngredientDraft
import com.example.spera.viewmodels.states.NewRecipeUiState
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de l'écran « Nouvelle recette » : saisie (nom, description,
 * temps, ingrédients), validation locale, création via [RecipeRepository]
 * puis partage optionnel sur le fil via [FeedRepository]. Référence de
 * patron : [LoginVM] / [TimerVM].
 */
class NewRecipeVM(
    private val recipeRepository: RecipeRepository = RecipeProvider.recipeRepository,
    private val feedRepository: FeedRepository = FeedProvider.feedRepository,
    private val sessionManager: SessionManager = AuthProvider.sessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewRecipeUiState>(NewRecipeUiState.Creation())
    val uiState: StateFlow<NewRecipeUiState> = _uiState.asStateFlow()

    /** État "Creation" courant, ou un état frais si on n'est pas en saisie. */
    private val editingState: NewRecipeUiState.Creation
        get() = _uiState.value as? NewRecipeUiState.Creation ?: NewRecipeUiState.Creation()

    private var nextIngredientId = 2

    fun onNameChange(name: String) {
        _uiState.value = editingState.copy(name = name, error = null)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = editingState.copy(description = description, error = null)
    }

    fun onPrepChange(minutes: String) {
        _uiState.value = editingState.copy(
            prepMinutes = minutes.filter { it.isDigit() }.take(3),
            error = null,
        )
    }

    fun onCookChange(minutes: String) {
        _uiState.value = editingState.copy(
            cookMinutes = minutes.filter { it.isDigit() }.take(3),
            error = null,
        )
    }

    fun onIngredientNameChange(id: Int, name: String) =
        updateIngredient(id) { it.copy(name = name) }

    fun onIngredientQuantityChange(id: Int, quantity: String) =
        updateIngredient(id) { it.copy(quantity = quantity.filter { c -> c.isDigit() }.take(4)) }

    fun onIngredientUnitChange(id: Int, unit: String) =
        updateIngredient(id) { it.copy(unit = unit) }

    fun onAddIngredient() {
        val current = editingState
        _uiState.value = current.copy(
            ingredients = current.ingredients + IngredientDraft(id = nextIngredientId++),
        )
    }

    /** Retire une ligne ; le formulaire en garde toujours au moins une. */
    fun onRemoveIngredient(id: Int) {
        val current = editingState
        if (current.ingredients.size <= 1) return
        _uiState.value = current.copy(
            ingredients = current.ingredients.filterNot { it.id == id },
        )
    }

    fun onShareToggle(share: Boolean) {
        _uiState.value = editingState.copy(shareToFeed = share)
    }

    /** Valide la saisie, crée la recette, puis la partage si demandé. */
    fun submit() {
        val current = editingState
        val name = current.name.trim()
        val description = current.description.trim()
        val prep = current.prepMinutes.toIntOrNull()

        // Validation locale avant appel réseau.
        val validationError = when {
            name.isBlank() -> "Donne un nom à ta recette."
            description.isBlank() -> "Ajoute une description."
            prep == null || prep <= 0 -> "Indique le temps de préparation."
            else -> null
        }
        if (validationError != null || prep == null) {
            _uiState.value = current.copy(error = validationError)
            return
        }

        // Lignes sans nom ignorées ; quantité vide → 1.
        val ingredients = current.ingredients.mapIndexedNotNull { index, draft ->
            val ingredientName = draft.name.trim()
            if (ingredientName.isEmpty()) return@mapIndexedNotNull null
            IngredientQuantity(
                ingredient = Ingredient(
                    id = "ing-new-$index",
                    name = ingredientName,
                    photo = "",
                    allergens = emptyList(),
                    unit = draft.unit.trim(),
                ),
                quantity = draft.quantity.toIntOrNull() ?: 1,
            )
        }

        _uiState.value = NewRecipeUiState.Submitting
        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            if (user == null) {
                _uiState.value = current.copy(error = "Session expirée, reconnecte-toi.")
                return@launch
            }
            val recipe = Recipe(
                id = "rc-new-${Random.nextInt(1_000_000)}",
                name = name,
                date = todayIso(),
                users = user,
                description = description,
                photo = "", // pas de sélection de photo en mock (placeholder visuel)
                ingredients = ingredients,
                timePreparation = prep,
                timeCooking = current.cookMinutes.toIntOrNull() ?: 0,
                likes = emptyList(),
                comments = emptyList(),
            )
            when (val result = recipeRepository.createRecipe(user, recipe)) {
                is CreateRecipeResult.Success -> {
                    val shared = current.shareToFeed &&
                        feedRepository.share(FeedItem.RecipePost(recipe)) is ShareResult.Success
                    _uiState.value = NewRecipeUiState.Created(shared = shared)
                }

                is CreateRecipeResult.Error ->
                    _uiState.value = current.copy(error = result.message)
            }
        }
    }

    /**
     * Repasse en saisie vierge. Appelé après [NewRecipeUiState.Created] : le
     * VM survit à la fermeture de l'écran, sans reset il re-déclencherait la
     * navigation à la prochaine ouverture.
     */
    fun reset() {
        nextIngredientId = 2
        _uiState.value = NewRecipeUiState.Creation()
    }

    private inline fun updateIngredient(id: Int, block: (IngredientDraft) -> IngredientDraft) {
        val current = editingState
        _uiState.value = current.copy(
            ingredients = current.ingredients.map { if (it.id == id) block(it) else it },
            error = null,
        )
    }
}
