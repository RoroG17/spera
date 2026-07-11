package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.AuthRepository
import com.example.spera.data.auth.SignUpResult
import com.example.spera.models.Activity
import com.example.spera.models.User
import com.example.spera.viewmodels.states.SignUpForm
import com.example.spera.viewmodels.states.SignUpUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SignUpVM(
    private val authRepository: AuthRepository = AuthProvider.authRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Editing())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val editingState: SignUpUiState.Editing
        get() = _uiState.value as? SignUpUiState.Editing ?: SignUpUiState.Editing()

    private fun updateForm(transform: (SignUpForm) -> SignUpForm) {
        val current = editingState
        _uiState.value = current.copy(form = transform(current.form), error = null)
    }

    fun onFirstNameChange(v: String) = updateForm { it.copy(firstName = v) }
    fun onNameChange(v: String) = updateForm { it.copy(name = v) }
    fun onPseudoChange(v: String) = updateForm { it.copy(pseudo = v) }
    fun onEmailChange(v: String) = updateForm { it.copy(email = v) }
    fun onPasswordChange(v: String) = updateForm { it.copy(password = v) }
    fun onConfirmPasswordChange(v: String) = updateForm { it.copy(confirmPassword = v) }
    fun onHeightChange(v: String) = updateForm { it.copy(height = v.filter { c -> c.isDigit() }) }
    fun onWeightChange(v: String) = updateForm { it.copy(weight = v.filter { c -> c.isDigit() }) }

    // --- Activités (plusieurs possibles) ---
    fun onSportDraftChange(v: String) = updateForm { it.copy(sportDraft = v) }
    fun onQuantityDraftChange(v: String) = updateForm { it.copy(quantityDraft = v.filter { c -> c.isDigit() }) }
    fun onObjectiveDraftChange(v: String) = updateForm { it.copy(objectiveDraft = v) }

    /** Ajoute l'activité en cours de saisie à la liste, ou signale une erreur. */
    fun addActivity() {
        val current = editingState
        val form = current.form
        val activity = draftToActivity(form)
        if (activity == null) {
            _uiState.value = current.copy(error = "Renseigne un sport et un nombre de séances valide.")
            return
        }
        _uiState.value = current.copy(
            form = form.copy(
                activities = form.activities + activity,
                sportDraft = "",
                quantityDraft = "",
                objectiveDraft = "",
            ),
            error = null,
        )
    }

    fun removeActivity(index: Int) {
        val current = editingState
        val form = current.form
        if (index !in form.activities.indices) return
        _uiState.value = current.copy(
            form = form.copy(activities = form.activities.filterIndexed { i, _ -> i != index }),
            error = null,
        )
    }

    /** Construit une Activity depuis les champs draft, ou null si invalide/vide. */
    private fun draftToActivity(form: SignUpForm): Activity? {
        val sport = form.sportDraft.trim()
        val quantity = form.quantityDraft.toIntOrNull()
        if (sport.isBlank() || quantity == null) return null
        return Activity(
            sport = sport,
            quantity = quantity,
            objective = form.objectiveDraft.trim().ifBlank { null },
        )
    }

    fun register() {
        val current = editingState
        var form = current.form

        // Inclut une activité en cours de saisie non encore ajoutée.
        val pendingDraft = draftToActivity(form)
        if (pendingDraft != null) {
            form = form.copy(
                activities = form.activities + pendingDraft,
                sportDraft = "",
                quantityDraft = "",
                objectiveDraft = "",
            )
        }

        val error = validate(form)
        if (error != null) {
            _uiState.value = current.copy(form = form, error = error)
            return
        }

        val user = User(
            id = "u-${Random.nextInt(100_000, 999_999)}",
            name = form.name.trim(),
            firstName = form.firstName.trim(),
            pseudo = form.pseudo.trim(),
            mail = form.email.trim().lowercase(),
            height = form.height.toInt(),
            weight = form.weight.toInt(),
            activities = form.activities,
            followers = emptyList(),
            following = emptyList(),
            favoriteRecipes = emptyList(),
            recipes = emptyList(),
            trainings = emptyList(),
        )

        _uiState.value = SignUpUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signUp(form.email.trim(), form.password, user)) {
                is SignUpResult.Success ->
                    _uiState.value = SignUpUiState.Success(result.user)

                SignUpResult.EmailAlreadyUsed ->
                    _uiState.value = current.copy(form = form, error = "Cet email est déjà utilisé.")

                is SignUpResult.Error ->
                    _uiState.value = current.copy(form = form, error = result.message)
            }
        }
    }

    /** Validation locale des champs. Retourne le 1er message d'erreur, ou null. */
    private fun validate(form: SignUpForm): String? {
        if (form.firstName.isBlank() || form.name.isBlank() || form.pseudo.isBlank()) {
            return "Renseigne ton prénom, ton nom et ton pseudo."
        }
        if (!EMAIL_REGEX.matches(form.email.trim())) {
            return "Adresse email invalide."
        }
        if (!isSecurePassword(form.password)) {
            return "Mot de passe : 8 caractères minimum, avec au moins une lettre et un chiffre."
        }
        if (form.password != form.confirmPassword) {
            return "Les mots de passe ne correspondent pas."
        }
        val height = form.height.toIntOrNull()
        if (height == null || height !in 50..272) {
            return "Taille invalide (en cm)."
        }
        val weight = form.weight.toIntOrNull()
        if (weight == null || weight !in 20..350) {
            return "Poids invalide (en kg)."
        }
        if (form.activities.isEmpty()) {
            return "Ajoute au moins une activité sportive."
        }
        return null
    }

    private fun isSecurePassword(password: String): Boolean =
        password.length >= 8 && password.any { it.isLetter() } && password.any { it.isDigit() }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
