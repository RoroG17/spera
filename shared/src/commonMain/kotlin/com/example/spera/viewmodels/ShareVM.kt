package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.feed.FeedItem
import com.example.spera.data.feed.FeedProvider
import com.example.spera.data.feed.FeedRepository
import com.example.spera.data.feed.ShareResult
import com.example.spera.viewmodels.states.ShareUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel du partage d'un post existant (recette ou séance) sur le fil,
 * utilisé par les écrans détail. Instancier avec une clé `viewModel(key = …)`
 * distincte par écran pour ne pas partager l'état entre recette et séance.
 */
class ShareVM(
    private val feedRepository: FeedRepository = FeedProvider.feedRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Idle)
    val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    fun share(post: FeedItem) {
        val current = _uiState.value
        if (current is ShareUiState.Sharing) return
        if (current is ShareUiState.Shared && current.postId == post.id) return

        _uiState.value = ShareUiState.Sharing
        viewModelScope.launch {
            _uiState.value = when (val result = feedRepository.share(post)) {
                // Déjà présent : même issue pour l'utilisateur, le post est sur le fil.
                is ShareResult.Success, is ShareResult.AlreadyShared ->
                    ShareUiState.Shared(post.id)

                is ShareResult.Error -> ShareUiState.Error(result.message)
            }
        }
    }

    /** Oublie l'état du post précédent — appelé à l'ouverture d'un détail. */
    fun reset() {
        _uiState.value = ShareUiState.Idle
    }
}
