package com.example.spera.ui.screens.newpost

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spera.data.feed.PostType
import com.example.spera.viewmodels.NewPostVM
import com.example.spera.viewmodels.states.NewPostUiState

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)
private val ChipUnselected = Color(0xFFF5F3F7)
private val ChipTextDark = Color(0xFF17141C)

/**
 * Écran « Nouvelle publication » (US6, maquette 6) : choix du type (séance ou
 * recette), nom, description, photo (placeholder en mock) puis publication.
 */
@Composable
fun NewPostScreen(
    onBack: () -> Unit = {},
    onPublished: () -> Unit = {},
    viewModel: NewPostVM = viewModel { NewPostVM() },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is NewPostUiState.Published) {
            onPublished()
            viewModel.reset()
        }
    }

    val editing = state as? NewPostUiState.Editing
    val isSubmitting = state is NewPostUiState.Submitting

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        // Header : retour + titre
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
                "Nouvelle publication",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 14.dp),
            )
        }

        // Choix du type
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TypeChip(
                label = "🏃 Entraînement",
                selected = editing?.type == PostType.Training,
                enabled = !isSubmitting,
                onClick = { viewModel.onTypeSelect(PostType.Training) },
                modifier = Modifier.weight(1f),
            )
            TypeChip(
                label = "🥘 Recette",
                selected = editing?.type == PostType.Recipe,
                enabled = !isSubmitting,
                onClick = { viewModel.onTypeSelect(PostType.Recipe) },
                modifier = Modifier.weight(1f),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
        )

        OutlinedTextField(
            value = editing?.description ?: "",
            onValueChange = viewModel::onDescriptionChange,
            placeholder = { Text("Raconte ta séance ou partage ta recette…", color = TextMuted) },
            enabled = !isSubmitting,
            colors = fieldColors(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .heightIn(min = 150.dp),
        )

        // Photo : placeholder visuel (pas de picker en mock, cf. zone photo du fil).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .height(130.dp)
                .drawBehind {
                    drawRoundRect(
                        color = TextMuted,
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(12.dp.toPx(), 8.dp.toPx()),
                            ),
                        ),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🖼️", fontSize = 26.sp)
                Text(
                    "Ajouter une photo",
                    color = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        if (editing?.error != null) {
            Text(
                editing.error,
                color = Accent,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
        }

        Button(
            onClick = viewModel::publish,
            enabled = !isSubmitting,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 24.dp),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            } else {
                Text(
                    "Publier",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 6.dp),
                )
            }
        }
    }
}

/**
 * Pastille de choix du type. Non sélectionnée : pilule claire (maquette) ;
 * sélectionnée : primaire violet (comme le toggle du prototype).
 */
@Composable
private fun TypeChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                if (selected) Primary else ChipUnselected,
                RoundedCornerShape(50),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (selected) Color.White else ChipTextDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
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
)
