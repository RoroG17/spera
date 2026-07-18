package com.example.spera.data.trainings

import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée de [TrainingRepository] (US12, sans backend).
 * Renvoie les séances de [TrainingMockData] quel que soit l'utilisateur
 * (suffisant tant que l'app est mockée).
 */
class MockTrainingRepository : TrainingRepository {

    override suspend fun loadTrainings(user: User): TrainingsResult {
        delay(700) // simulation d'un appel réseau

        return TrainingsResult.Success(
            trainings = TrainingMockData.trainings.sortedByDescending { it.date },
        )
    }
}
