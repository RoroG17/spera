package com.example.spera.data.trainings

import com.example.spera.models.Training
import com.example.spera.models.TrainingData
import com.example.spera.models.User

/**
 * Données mockées du calendrier d'entraînement (US12, sans backend).
 * `lea` reprend l'identité du compte de démo (`u-001`, cf. `MockData`).
 * Les dates couvrent plusieurs semaines de juillet 2026 pour exercer le
 * groupement par semaine.
 */
object TrainingMockData {

    private val lea = User(
        id = "u-001",
        name = "Martin",
        firstName = "Léa",
        pseudo = "lea_run",
        mail = "test@elan.fr",
        height = 168,
        weight = 60,
        activities = emptyList(),
        followers = emptyList(),
        following = emptyList(),
        favoriteRecipes = emptyList(),
        recipes = emptyList(),
        trainings = emptyList(),
    )

    private fun training(
        id: String,
        date: String,
        name: String,
        description: String,
        minutes: Int,
        kcal: Int,
        km: Double? = null,
    ): Training = Training(
        id = id,
        date = date,
        name = name,
        description = description,
        photo = "",
        users = lea,
        likes = emptyList(),
        comments = emptyList(),
        data = TrainingData(
            durationMinutes = minutes,
            caloriesBurned = kcal,
            distanceKm = km,
        ),
    )

    /** Séances de Léa, déjà triées de la plus récente à la plus ancienne. */
    val trainings: List<Training> = listOf(
        // Semaine courante (13–19 juillet 2026).
        training("tr-01", "2026-07-17", "Footing", "Sortie tranquille au parc.", 42, 480, 8.2),
        training("tr-02", "2026-07-15", "Fractionné 30/30", "12 répétitions, récup trot.", 24, 310),
        training("tr-03", "2026-07-13", "Renfo jambes", "Squats, fentes et mollets au poids du corps.", 45, 380),
        // Semaine du 6 au 12 juillet.
        training("tr-04", "2026-07-11", "Sortie longue", "Allure endurance, ravito à mi-parcours.", 75, 820, 13.5),
        training("tr-05", "2026-07-08", "Fractionné 10x400m", "Piste, récup 1 min entre chaque.", 38, 450, 6.0),
        training("tr-06", "2026-07-06", "Footing récup", "Très lent, on relâche après le week-end.", 30, 300, 5.4),
        // Semaine du 29 juin au 5 juillet.
        training("tr-07", "2026-07-03", "Côtes 6x200m", "Travail de puissance en côte.", 35, 420, 5.8),
        training("tr-08", "2026-07-01", "Renfo gainage", "Planches, side-planks et hollow hold.", 25, 180),
        training("tr-09", "2026-06-30", "Footing", "Boucle habituelle du canal.", 40, 430, 7.6),
    )
}
