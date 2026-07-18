package com.example.spera.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spera.viewmodels.NewRecipeVM
import com.example.spera.viewmodels.states.IngredientDraft
import com.example.spera.viewmodels.states.NewRecipeUiState

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)
private val Border = Color(0xFF302B3A)

/**
 * Écran « Nouvelle recette » en plein écran : nom, description, temps,
 * ingrédients, et interrupteur « Partager sur le fil d'actualité ».
 * [onCreated] reçoit `shared` (la recette a aussi été postée sur le fil).
 */
@Composable
fun NewRecipeScreen(
    onBack: () -> Unit = {},
    onCreated: (shared: Boolean) -> Unit = {},
    viewModel: NewRecipeVM = viewModel { NewRecipeVM() },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        val s = state
        if (s is NewRecipeUiState.Created) {
            onCreated(s.shared)
            viewModel.reset()
        }
    }

    val editing = state as? NewRecipeUiState.Creation
    val isSubmitting = state is NewRecipeUiState.Submitting

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        // Header : retour + titre (patron des flux plein écran).
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 14.dp, bottom = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Surface, CircleShape)
                    .clickable(enabled = !isSubmitting, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("‹", color = TextPrimary, fontSize = 24.sp)
            }
            Text(
                "Nouvelle recette",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 14.dp),
            )
        }

        OutlinedTextField(
            value = editing?.name ?: "",
            onValueChange = viewModel::onNameChange,
            label = { Text("Nom") },
            singleLine = true,
            enabled = !isSubmitting,
            colors = fieldColors(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = editing?.description ?: "",
            onValueChange = viewModel::onDescriptionChange,
            placeholder = { Text("Décris ta recette…", color = TextMuted) },
            enabled = !isSubmitting,
            colors = fieldColors(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .heightIn(min = 110.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 14.dp),
        ) {
            OutlinedTextField(
                value = editing?.prepMinutes ?: "",
                onValueChange = viewModel::onPrepChange,
                label = { Text("Préparation (min)") },
                singleLine = true,
                enabled = !isSubmitting,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = editing?.cookMinutes ?: "",
                onValueChange = viewModel::onCookChange,
                label = { Text("Cuisson (min)") },
                singleLine = true,
                enabled = !isSubmitting,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f),
            )
        }

        Text(
            "Ingrédients",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp),
        )
        editing?.ingredients?.forEach { draft ->
            IngredientRow(
                draft = draft,
                removable = editing.ingredients.size > 1,
                enabled = !isSubmitting,
                viewModel = viewModel,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(Surface, RoundedCornerShape(14.dp))
                .clickable(enabled = !isSubmitting, onClick = viewModel::onAddIngredient)
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "＋ Ajouter un ingrédient",
                color = Primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // Partage sur le fil — le fil référence la recette, il ne la duplique pas.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Partager sur le fil d'actualité",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Ta recette apparaîtra dans le fil de tes abonnés.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Switch(
                checked = editing?.shareToFeed ?: false,
                onCheckedChange = viewModel::onShareToggle,
                enabled = !isSubmitting,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Primary,
                    checkedThumbColor = Color.White,
                    uncheckedTrackColor = Border,
                    uncheckedThumbColor = TextMuted,
                ),
            )
        }

        if (editing?.error != null) {
            Text(
                editing.error,
                color = Accent,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .background(Primary, RoundedCornerShape(24.dp))
                .clickable(enabled = !isSubmitting, onClick = viewModel::submit)
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text(
                    "Créer la recette",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/** Ligne d'ingrédient : nom, quantité, unité, suppression. */
@Composable
private fun IngredientRow(
    draft: IngredientDraft,
    removable: Boolean,
    enabled: Boolean,
    viewModel: NewRecipeVM,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
    ) {
        OutlinedTextField(
            value = draft.name,
            onValueChange = { viewModel.onIngredientNameChange(draft.id, it) },
            label = { Text("Ingrédient") },
            singleLine = true,
            enabled = enabled,
            colors = fieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = draft.quantity,
            onValueChange = { viewModel.onIngredientQuantityChange(draft.id, it) },
            label = { Text("Qté") },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = fieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(72.dp),
        )
        OutlinedTextField(
            value = draft.unit,
            onValueChange = { viewModel.onIngredientUnitChange(draft.id, it) },
            label = { Text("Unité") },
            singleLine = true,
            enabled = enabled,
            colors = fieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(84.dp),
        )
        if (removable) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Surface, CircleShape)
                    .clickable(enabled = enabled) { viewModel.onRemoveIngredient(draft.id) },
                contentAlignment = Alignment.Center,
            ) {
                Text("✕", color = TextMuted, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Surface,
    unfocusedContainerColor = Surface,
    disabledContainerColor = Surface,
    focusedBorderColor = Primary,
    unfocusedBorderColor = Border,
    cursorColor = Primary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = Primary,
    unfocusedLabelColor = TextMuted,
)
