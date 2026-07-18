package com.example.spera.data.feed

import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée de [FeedRepository] (US4, sans backend).
 *
 * Renvoie les posts de [FeedMockData] (considérés comme les abonnements de
 * l'utilisateur), triés du plus récent au plus ancien et découpés en pages pour
 * simuler un chargement optimisé / infinite scroll. Les posts partagés via
 * [share] sont insérés à leur place chronologique, en mémoire uniquement.
 */
class MockFeedRepository : FeedRepository {

    private val allPosts: MutableList<FeedItem> =
        FeedMockData.posts.sortedByDescending { it.date }.toMutableList()

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

    override suspend fun share(post: FeedItem): ShareResult {
        delay(400) // simulation d'un appel réseau

        if (allPosts.any { it.id == post.id }) return ShareResult.AlreadyShared
        // Inséré à sa place chronologique : une vieille séance partagée ne
        // passe pas devant les posts plus récents.
        val index = allPosts.indexOfFirst { it.date <= post.date }
        allPosts.add(if (index == -1) allPosts.size else index, post)
        return ShareResult.Success
    }
}
