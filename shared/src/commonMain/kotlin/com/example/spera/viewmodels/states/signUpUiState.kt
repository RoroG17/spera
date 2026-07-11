package com.example.spera.viewmodels.states

import com.example.spera.models.Activity
import com.example.spera.models.User

/**
 * Champs du formulaire de création de compte (US1).
 * Tout est en String pour coller à la saisie ; la conversion/validation se fait
 * dans le ViewModel.
 *
 * [activities] contient les activités déjà ajoutées ; les champs `*Draft`
 * correspondent à l'activité en cours de saisie (avant le bouton « Ajouter »).
 */
data class SignUpForm(
    val firstName: String = "",
    val name: String = "",
    val pseudo: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val height: String = "",
    val weight: String = "",
    val activities: List<Activity> = emptyList(),
    val sportDraft: String = "",
    val quantityDraft: String = "",
    val objectiveDraft: String = "",
)

/**
 * État UI de l'écran de création de compte (US1).
 */
sealed interface SignUpUiState {
    data class Editing(
        val form: SignUpForm = SignUpForm(),
        val error: String? = null,
    ) : SignUpUiState

    data object Loading : SignUpUiState

    data class Success(val user: User) : SignUpUiState
}
