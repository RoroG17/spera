package com.example.spera.data.trainings

/**
 * Câblage manuel léger (en attendant Koin), sur le modèle d'`AuthProvider` :
 * une seule instance de [TrainingRepository] partagée dans l'app.
 */
object TrainingProvider {
    val trainingRepository: TrainingRepository by lazy { MockTrainingRepository() }
}
