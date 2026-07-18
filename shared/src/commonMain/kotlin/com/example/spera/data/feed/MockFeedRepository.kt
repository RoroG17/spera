package com.example.spera.data.feed

import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée de [FeedRepository] (US4, sans backend).
 *
 * Renvoie les posts de [FeedMockData] (considérés comme les abonnements de
 * l'utilisateur), triés du plus récent au plus ancien et découpés en pages pour
 * simuler un chargement optimisé / infinite scroll.
 */
class MockFeedRepository : FeedRepository {

    private val allPosts: List<FeedItem> =
        FeedMockData.posts.sortedByDescending { it.date }

    override suspend fun loadFeed(user: User, page: Int, pageSize: Int): FeedResult {
        delay(700) // simulation d'un appel réseau

        val from = page * pageSize
        if (from >= allPosts.size) {
            return FeedResult.Success(posts = emptyList(), hasMore = false)
        }
        val to = minOf(from + pageSize, allPosts.size)
        return FeedResult.Success(
            posts = allPosts.subList(from, to).toList(),
            hasMore = to < allPosts.size,
        )
    }
}
