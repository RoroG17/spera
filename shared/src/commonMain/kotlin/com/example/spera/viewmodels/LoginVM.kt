package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.AuthRepository
import com.example.spera.viewmodels.states.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginVM(
    private val authRepository: AuthRepository = AuthProvider.authRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Editing())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /** État "Editing" courant, ou un état frais si on n'est pas en saisie. */
    private val editingState: LoginUiState.Editing
        get() = _uiState.value as? LoginUiState.Editing ?: LoginUiState.Editing()

    fun onEmailChange(email: String) {
        _uiState.value = editingState.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = editingState.copy(password = password, error = null)
    }

    fun connect() {
        val current = editingState
        val email = current.email.trim()
        val password = current.password

        // Validation locale avant appel réseau.
        val validationError = when {
            email.isBlank() || password.isBlank() -> "Veuillez renseigner l'email et le mot de passe."
            !isValidEmail(email) -> "Adresse email invalide."
            else -> null
        }
        if (validationError != null) {
            _uiState.value = current.copy(error = validationError)
            return
        }

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signIn(email, password)) {
                is com.example.spera.data.auth.SignInResult.Success ->
                    _uiState.value = LoginUiState.Success(result.user)

                com.example.spera.data.auth.SignInResult.InvalidCredentials ->
                    _uiState.value = current.copy(error = "Identifiants incorrects.")

                is com.example.spera.data.auth.SignInResult.Error ->
                    _uiState.value = current.copy(error = result.message)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean =
        EMAIL_REGEX.matches(email)

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
