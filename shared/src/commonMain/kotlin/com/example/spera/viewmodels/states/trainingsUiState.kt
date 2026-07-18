package com.example.spera.viewmodels.states

import com.example.spera.models.Training

/**
 * Grille du mois affiché par le calendrier d'entraînement. Tout est
 * pré-calculé côté ViewModel : l'UI ne fait que dessiner.
 */
data class CalendarMonth(
    /** « Juillet 2026 ». */
    val title: String,
    /** Décalage du 1er du mois dans la semaine : 0 = lundi … 6 = dimanche. */
    val firstDayOffset: Int,
    /** Nombre de jours du mois (28–31). */
    val dayCount: Int,
    /** Jours du mois ayant au moins une séance. */
    val markedDays: Set<Int>,
    /** Jour courant, si le mois affiché est le mois en cours. */
    val todayDay: Int?,
    /** Jour sélectionné (filtre de la liste), null si aucun. */
    val selectedDay: Int?,
)

/**
 * État UI de l'onglet Entraînement (US12), aligné sur le doc "UI State" →
 * Entraînement (Success / Loading / Error ; les états Creation / In Training /
 * End Training viendront avec US13-US14).
 */
sealed interface TrainingsUiState {

    /** Chargement du calendrier. */
    data object Loading : TrainingsUiState

    /** Échec du chargement (aucune séance affichable). */
    data class Error(val message: String) : TrainingsUiState

    /**
     * Calendrier chargé : mois affiché + séances de la liste (toutes, de la
     * plus récente à la plus ancienne, ou celles du jour sélectionné).
     */
    data class Success(
        val month: CalendarMonth,
        val trainings: List<Training>,
    ) : TrainingsUiState
}
