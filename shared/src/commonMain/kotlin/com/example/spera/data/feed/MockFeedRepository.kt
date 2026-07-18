package com.example.spera.data.feed

import com.example.spera.models.Recipe
import com.example.spera.models.Training
import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée de [FeedRepository] (US4/US6, sans backend).
 *
 * Renvoie les posts de [FeedMockData] (considérés comme les abonnements de
 * l'utilisateur), triés du plus récent au plus ancien et découpés en pages pour
 * simuler un chargement optimisé / infinite scroll. Les posts créés via
 * [createPost] sont ajoutés en tête du fil, en mémoire uniquement.
 */
class MockFeedRepository : FeedRepository {

    private val allPosts: MutableList<FeedItem> =
        FeedMockData.posts.sortedByDescending { it.date }.toMutableList()

    /** Compteur d'ids des posts créés pendant la session. */
    private var createdCount = 0

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

    override suspend fun createPost(
        author: User,
        type: PostType,
        name: String,
        description: String,
        photo: String,
    ): CreatePostResult {
        delay(700) // simulation d'un appel réseau

        createdCount += 1
        val id = "u-$createdCount"
        val date = todayIso()
        val post = when (type) {
            PostType.Training -> FeedItem.TrainingPost(
                Training(
                    id = id,
                    date = date,
                    name = name,
                    description = description,
                    photo = photo,
                    users = author,
                    likes = emptyList(),
                    comments = emptyList(),
                ),
            )

            PostType.Recipe -> FeedItem.RecipePost(
                Recipe(
                    id = id,
                    name = name,
                    date = date,
                    users = author,
                    description = description,
                    photo = photo,
                    // Détail (ingrédients, temps) hors périmètre US6 — voir US9.
                    ingredients = emptyList(),
                    timePreparation = 0,
                    timeCooking = 0,
                    likes = emptyList(),
                    comments = emptyList(),
                ),
            )
        }
        // Daté du jour : plus récent que toutes les données mockées, donc en tête.
        allPosts.add(0, post)
        return CreatePostResult.Success(post)
    }
}
