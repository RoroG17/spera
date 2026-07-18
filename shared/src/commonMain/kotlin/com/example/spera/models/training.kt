package com.example.spera.models

/**
 * Mesures d'une séance — champ « Data : Durée, Calorie brûlée, … » de
 * `data.txt`. [distanceKm] est null pour les séances sans distance (renfo,
 * timer…).
 */
data class TrainingData(
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val distanceKm: Double? = null,
)

data class Training (
    val id: String,
    val date: String,
    val name: String,
    val description: String,
    val photo: String,
    val users: User,
    val likes: List<User>,
    val comments: List<Comment<Training>>,
    // Null par défaut : les posts du fil (US4/US6) n'ont pas encore de mesures.
    val data: TrainingData? = null,
) {
}