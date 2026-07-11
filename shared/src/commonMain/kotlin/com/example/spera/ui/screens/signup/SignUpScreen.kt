package com.example.spera.ui.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spera.models.Activity
import com.example.spera.viewmodels.SignUpVM
import com.example.spera.viewmodels.states.SignUpForm
import com.example.spera.viewmodels.states.SignUpUiState

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: SignUpVM = viewModel { SignUpVM() },
) {
    val state by viewModel.uiState.collectAsState()

    val form = (state as? SignUpUiState.Editing)?.form ?: SignUpForm()
    val error = (state as? SignUpUiState.Editing)?.error
    val isLoading = state is SignUpUiState.Loading

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 40.dp),
    ) {
        TextButton(
            onClick = onNavigateBack,
            enabled = !isLoading,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        ) {
            Text("‹ Retour", color = TextMuted, fontSize = 15.sp)
        }

        Text(
            text = "Créer un compte",
            color = TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Rejoins Élan et suis ta progression.",
            color = TextMuted,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )

        SectionLabel("Identité")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuthField(
                value = form.firstName,
                onValueChange = viewModel::onFirstNameChange,
                label = "Prénom",
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
            )
            AuthField(
                value = form.name,
                onValueChange = viewModel::onNameChange,
                label = "Nom",
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
            )
        }
        AuthField(
            value = form.pseudo,
            onValueChange = viewModel::onPseudoChange,
            label = "Pseudo",
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )

        SectionLabel("Identifiants", top = 20.dp)
        AuthField(
            value = form.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            enabled = !isLoading,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth(),
        )
        AuthField(
            value = form.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Mot de passe",
            enabled = !isLoading,
            keyboardType = KeyboardType.Password,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                    Text(if (passwordVisible) "Masquer" else "Voir", color = Primary, fontSize = 13.sp)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )
        AuthField(
            value = form.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Confirmer le mot de passe",
            enabled = !isLoading,
            keyboardType = KeyboardType.Password,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )
        Text(
            text = "8 caractères min., avec au moins une lettre et un chiffre.",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp),
        )

        SectionLabel("Mensurations", top = 20.dp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuthField(
                value = form.height,
                onValueChange = viewModel::onHeightChange,
                label = "Taille (cm)",
                enabled = !isLoading,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            AuthField(
                value = form.weight,
                onValueChange = viewModel::onWeightChange,
                label = "Poids (kg)",
                enabled = !isLoading,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
        }

        SectionLabel("Activités sportives", top = 20.dp)

        // Activités déjà ajoutées.
        form.activities.forEachIndexed { index, activity ->
            ActivityRow(
                activity = activity,
                enabled = !isLoading,
                onRemove = { viewModel.removeActivity(index) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            )
        }

        // Saisie d'une nouvelle activité.
        AuthField(
            value = form.sportDraft,
            onValueChange = viewModel::onSportDraftChange,
            label = "Sport",
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AuthField(
                value = form.quantityDraft,
                onValueChange = viewModel::onQuantityDraftChange,
                label = "Séances / sem.",
                enabled = !isLoading,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            AuthField(
                value = form.objectiveDraft,
                onValueChange = viewModel::onObjectiveDraftChange,
                label = "Objectif (option.)",
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
            )
        }
        TextButton(
            onClick = viewModel::addActivity,
            enabled = !isLoading,
            modifier = Modifier.padding(top = 4.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
        ) {
            Text("+ Ajouter cette activité", color = Primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        if (error != null) {
            Text(
                text = error,
                color = Accent,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
        }

        Button(
            onClick = viewModel::register,
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.padding(vertical = 2.dp))
            } else {
                Text("Créer mon compte", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 6.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Déjà un compte ?", color = TextMuted, fontSize = 14.sp)
            TextButton(onClick = onNavigateToLogin, enabled = !isLoading) {
                Text("Se connecter", color = Primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ActivityRow(
    activity: Activity,
    enabled: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(Surface, RoundedCornerShape(12.dp))
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(activity.sport, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            val detail = buildString {
                append("${activity.quantity} séance(s)/sem.")
                activity.objective?.let { append(" • $it") }
            }
            Text(detail, color = TextMuted, fontSize = 13.sp)
        }
        TextButton(onClick = onRemove, enabled = enabled) {
            Text("Retirer", color = Accent, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SectionLabel(text: String, top: androidx.compose.ui.unit.Dp = 0.dp) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = top, bottom = 10.dp),
    )
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
            disabledContainerColor = Surface,
            focusedBorderColor = Primary,
            unfocusedBorderColor = Color(0xFF302B3A),
            cursorColor = Primary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = Primary,
            unfocusedLabelColor = TextMuted,
        ),
        modifier = modifier,
    )
}
