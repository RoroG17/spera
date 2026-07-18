package com.example.spera.data.feed

/**
 * Câblage manuel léger (en attendant Koin), sur le modèle d'`AuthProvider` :
 * une seule instance de [FeedRepository] partagée dans l'app.
 */
object FeedProvider {
    val feedRepository: FeedRepository by lazy { MockFeedRepository() }
}
