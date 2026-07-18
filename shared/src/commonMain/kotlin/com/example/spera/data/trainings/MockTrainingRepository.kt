package com.example.spera.data.trainings

import com.example.spera.models.Training
import com.example.spera.models.User
import kotlinx.coroutines.delay

/**
 * Implémentation mockée de [TrainingRepository] (US12/US14, sans backend).
 * Sert les séances de [TrainingMockData] quel que soit l'utilisateur, dans
 * une liste mutable en mémoire pour que les séances créées depuis le timer
 * apparaissent dans le calendrier (perdues au redémarrage, comme le reste
 * du mock).
 */
class MockTrainingRepository : TrainingRepository {

    private val trainings = TrainingMockData.trainings.toMutableList()

    override suspend fun loadTrainings(user: User): TrainingsResult {
        delay(700) // simulation d'un appel réseau

        return TrainingsResult.Success(trainings = trainings.sortedByDescending { it.date })
    }

    override suspend fun saveTraining(user: User, training: Training): TrainingsResult {
        delay(400) // simulation d'un appel réseau

        trainings += training
        return TrainingsResult.Success(trainings = trainings.sortedByDescending { it.date })
    }
}
