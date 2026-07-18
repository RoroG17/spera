package com.example.spera.data.feed

import com.example.spera.models.Recipe
import com.example.spera.models.Training
import com.example.spera.models.User

/**
 * Élément du fil d'actualité (US4).
 *
 * Un post n'est pas une entité propre dans le modèle : c'est soit une [Recipe],
 * soit un [Training]. Cette sealed interface les unifie pour un affichage
 * homogène (auteur / date / contenu) sans modifier les modèles de `data.txt`.
 */
sealed interface FeedItem {
    val id: String
    val author: User
    val date: String

    data class RecipePost(val recipe: Recipe) : FeedItem {
        override val id: String get() = "recipe-${recipe.id}"
        override val author: User get() = recipe.users
        override val date: String get() = recipe.date
    }

    data class TrainingPost(val training: Training) : FeedItem {
        override val id: String get() = "training-${training.id}"
        override val author: User get() = training.users
        override val date: String get() = training.date
    }
}
