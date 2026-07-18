package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.SessionManager
import com.example.spera.data.feed.CreatePostResult
import com.example.spera.data.feed.FeedProvider
import com.example.spera.data.feed.FeedRepository
import com.example.spera.data.feed.PostType
import com.example.spera.viewmodels.states.NewPostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de l'écran « Nouvelle publication » (US6) : saisie d'un post
 * (séance ou recette), validation locale puis publication via [FeedRepository].
 * Référence de patron : [LoginVM].
 */
class NewPostVM(
    private val feedRepository: FeedRepository = FeedProvider.feedRepository,
    private val sessionManager: SessionManager = AuthProvider.sessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewPostUiState>(NewPostUiState.Editing())
    val uiState: StateFlow<NewPostUiState> = _uiState.asStateFlow()

    /** État "Editing" courant, ou un état frais si on n'est pas en saisie. */
    private val editingState: NewPostUiState.Editing
        get() = _uiState.value as? NewPostUiState.Editing ?: NewPostUiState.Editing()

    fun onTypeSelect(type: PostType) {
        _uiState.value = editingState.copy(type = type, error = null)
    }

    fun onNameChange(name: String) {
        _uiState.value = editingState.copy(name = name, error = null)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = editingState.copy(description = description, error = null)
    }

    fun publish() {
        val current = editingState
        val type = current.type
        val name = current.name.trim()
        val description = current.description.trim()

        // Validation locale avant appel réseau.
        val validationError = when {
            type == null -> "Choisis un type : Entraînement ou Recette."
            name.isBlank() -> "Donne un nom à ta publication."
            description.isBlank() -> "Ajoute une description."
            else -> null
        }
        if (validationError != null || type == null) {
            _uiState.value = current.copy(error = validationError)
            return
        }

        _uiState.value = NewPostUiState.Submitting
        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            if (user == null) {
                _uiState.value = current.copy(error = "Session expirée, reconnecte-toi.")
                return@launch
            }
            val result = feedRepository.createPost(
                author = user,
                type = type,
                name = name,
                description = description,
                photo = "", // pas de sélection de photo en mock (placeholder visuel)
            )
            when (result) {
                is CreatePostResult.Success ->
                    _uiState.value = NewPostUiState.Published(result.post)

                is CreatePostResult.Error ->
                    _uiState.value = current.copy(error = result.message)
            }
        }
    }

    /**
     * Repasse en saisie vierge. Appelé après [NewPostUiState.Published] : le VM
     * survit à la fermeture de l'écran, sans reset il re-déclencherait la
     * navigation à la prochaine ouverture.
     */
    fun reset() {
        _uiState.value = NewPostUiState.Editing()
    }
}
