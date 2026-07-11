package com.example.spera.data.auth

import com.example.spera.models.Activity
import com.example.spera.models.User
import kotlinx.serialization.Serializable

/**
 * Instantané sérialisable de la session (US2).
 *
 * On ne persiste que le profil « cœur » de l'utilisateur connecté. Les relations
 * (abonnés, abonnements, recettes, entraînements, favoris) ne sont pas stockées :
 * elles seront rechargées à la demande quand le backend sera branché, et sont
 * restaurées vides au redémarrage — ce qui correspond aux données mockées.
 */
@Serializable
data class StoredUser(
    val id: String,
    val name: String,
    val firstName: String,
    val pseudo: String,
    val mail: String,
    val height: Int,
    val weight: Int,
    val activities: List<StoredActivity>,
)

@Serializable
data class StoredActivity(
    val sport: String,
    val quantity: Int,
    val objective: String?,
)

fun User.toStored(): StoredUser = StoredUser(
    id = id,
    name = name,
    firstName = firstName,
    pseudo = pseudo,
    mail = mail,
    height = height,
    weight = weight,
    activities = activities.map { StoredActivity(it.sport, it.quantity, it.objective) },
)

fun StoredUser.toUser(): User = User(
    id = id,
    name = name,
    firstName = firstName,
    pseudo = pseudo,
    mail = mail,
    height = height,
    weight = weight,
    activities = activities.map { Activity(it.sport, it.quantity, it.objective) },
    followers = emptyList(),
    following = emptyList(),
    favoriteRecipes = emptyList(),
    recipes = emptyList(),
    trainings = emptyList(),
)
