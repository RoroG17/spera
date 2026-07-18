package com.example.spera.viewmodels.states

import com.example.spera.models.TimerExercise

/** Bornes de configuration du timer (sliders + saisie manuelle). */
object TimerLimits {
    val effortSeconds = 5..180
    val restSeconds = 0..120
    val transitionSeconds = 0..120
    val rounds = 1..20
}

/**
 * Phase courante du timer : temps actif, récupération, transition entre deux
 * exercices, ou décompte « 5, 4, 3, 2, 1, GO ! » après un lancement manuel.
 */
enum class TimerPhase { Effort, Rest, Transition, Countdown }

/**
 * État UI du flux timer (US14), aligné sur le doc "UI State" → Entraînement :
 * Creation / In Training / End Training (Success / Loading / Error sont
 * portés par le calendrier, cf. [TrainingsUiState]).
 */
sealed interface TimerUiState {

    /** Configuration de la séance : nom + liste d'exercices à régler. */
    data class Creation(
        val sessionName: String,
        val exercises: List<TimerExercise>,
    ) : TimerUiState

    /** Séance en cours : décompte de la phase courante. */
    data class InTraining(
        val exercise: TimerExercise,
        val exerciseIndex: Int,
        val exerciseCount: Int,
        val phase: TimerPhase,
        /** Répétition courante, 1..rounds de l'exercice. */
        val round: Int,
        val secondsLeft: Int,
        val paused: Boolean,
        /** Transition manuelle : le décompte attend le lancement de l'exercice. */
        val awaitingManualStart: Boolean = false,
    ) : TimerUiState

    /** Séance terminée (ou arrêtée) : résumé + enregistrement au calendrier. */
    data class EndTraining(
        val sessionName: String,
        val exercises: List<TimerExercise>,
        /** Temps effectivement écoulé (pauses exclues). */
        val totalSeconds: Int,
        val saving: Boolean = false,
        val saved: Boolean = false,
        val error: String? = null,
    ) : TimerUiState
}
