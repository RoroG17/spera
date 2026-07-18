package com.example.spera.data.trainings

import com.example.spera.models.Training
import com.example.spera.models.User

/**
 * Résultat métier du chargement du calendrier d'entraînement (US12).
 * Les erreurs Supabase seront mappées ici quand le backend sera branché.
 */
sealed interface TrainingsResult {
    data class Success(val trainings: List<Training>) : TrainingsResult
    data class Error(val message: String) : TrainingsResult
}

interface TrainingRepository {
    /**
     * Renvoie les séances de [user] (liste `trainings` de `data.txt`), triées
     * de la plus récente à la plus ancienne. Le groupement par semaine
     * s'applique côté ViewModel.
     */
    suspend fun loadTrainings(user: User): TrainingsResult
}
