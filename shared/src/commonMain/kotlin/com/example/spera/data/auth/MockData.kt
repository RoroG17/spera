package com.example.spera.data.auth

import com.example.spera.models.Activity
import com.example.spera.models.User

/**
 * Données mockées pour tester la connexion sans backend (US2).
 *
 * Chaque compte associe des identifiants (email + mot de passe) à un [User].
 * À remplacer par Supabase Auth quand le backend sera disponible.
 */
data class MockAccount(
    val email: String,
    val password: String,
    val user: User,
)

object MockData {

    private val demoUser = User(
        id = "u-001",
        name = "Martin",
        firstName = "Léa",
        pseudo = "lea_run",
        mail = "test@elan.fr",
        height = 168,
        weight = 60,
        activity = Activity(sport = "Course à pied", quantity = 3, objective = "10 km"),
        followers = emptyList(),
        following = emptyList(),
    )

    private val secondUser = User(
        id = "u-002",
        name = "Dubois",
        firstName = "Tom",
        pseudo = "tom_lift",
        mail = "tom@elan.fr",
        height = 180,
        weight = 78,
        activity = Activity(sport = "Musculation", quantity = 4, objective = "Prise de masse"),
        followers = emptyList(),
        following = emptyList(),
    )

    val accounts: List<MockAccount> = listOf(
        MockAccount(email = "test@elan.fr", password = "password123", user = demoUser),
        MockAccount(email = "tom@elan.fr", password = "elan2026", user = secondUser),
    )
}
