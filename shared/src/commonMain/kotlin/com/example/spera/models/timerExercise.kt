package com.example.spera.models

/**
 * Un exercice du timer effort/repos (US14) : configuration locale d'une
 * séance à créer — pas d'entité dédiée dans `data.txt`, la séance terminée
 * est convertie en [Training] classique à l'enregistrement.
 */
data class TimerExercise(
    /** Clé stable pour la liste d'édition (pas persistée). */
    val id: Int,
    val name: String,
    val effortSeconds: Int,
    val restSeconds: Int,
    val rounds: Int,
    /**
     * Temps avant l'exercice suivant (ignoré pour le dernier) : non défini →
     * lancement manuel ; défini à 0 s → enchaînement direct ; sinon décompte.
     */
    val transitionDefined: Boolean = false,
    val transitionSeconds: Int = 0,
)
