package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.SessionManager
import com.example.spera.data.feed.FeedProvider
import com.example.spera.data.feed.FeedRepository
import com.example.spera.data.feed.FeedResult
import com.example.spera.viewmodels.states.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel du fil d'actualité (US4). Charge les posts des abonnements page par
 * page ([load] pour la première, [loadMore] pour l'infinite scroll).
 * Référence de patron : [LoginVM].
 */
class HomeVM(
    private val feedRepository: FeedRepository = FeedProvider.feedRepository,
    private val sessionManager: SessionManager = AuthProvider.sessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var nextPage = 0

    /** Garde-fou contre les chargements concurrents (double appel d'infinite scroll). */
    private var isFetching = false

    init {
        load()
    }

    /** (Re)charge la première page du fil. */
    fun load() {
        if (isFetching) return
        isFetching = true
        nextPage = 0
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            if (user == null) {
                _uiState.value = HomeUiState.Error("Session expirée, reconnecte-toi.")
                isFetching = false
                return@launch
            }
            when (val result = feedRepository.loadFeed(user, nextPage, PAGE_SIZE)) {
                is FeedResult.Success -> {
                    nextPage = 1
                    _uiState.value = HomeUiState.Success(
                        posts = result.posts,
                        isLoadingMore = false,
                        canLoadMore = result.hasMore,
                    )
                }

                is FeedResult.Error ->
                    _uiState.value = HomeUiState.Error(result.message)
            }
            isFetching = false
        }
    }

    /** Charge la page suivante et l'ajoute au fil (infinite scroll). */
    fun loadMore() {
        val current = _uiState.value
        if (current !is HomeUiState.Success || !current.canLoadMore || isFetching) return

        isFetching = true
        _uiState.value = current.copy(isLoadingMore = true)

        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            if (user == null) {
                _uiState.value = current.copy(isLoadingMore = false)
                isFetching = false
                return@launch
            }
            when (val result = feedRepository.loadFeed(user, nextPage, PAGE_SIZE)) {
                is FeedResult.Success -> {
                    nextPage += 1
                    _uiState.value = current.copy(
                        posts = current.posts + result.posts,
                        isLoadingMore = false,
                        canLoadMore = result.hasMore,
                    )
                }

                is FeedResult.Error ->
                    // On conserve les posts déjà chargés ; on arrête juste le spinner.
                    _uiState.value = current.copy(isLoadingMore = false)
            }
            isFetching = false
        }
    }

    private companion object {
        const val PAGE_SIZE = 6
    }
}
