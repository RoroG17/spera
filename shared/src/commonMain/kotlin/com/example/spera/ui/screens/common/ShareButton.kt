package com.example.spera.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spera.viewmodels.states.ShareUiState

// Palette auth de référence (CLAUDE.md)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)

/**
 * Bouton « Partager sur le fil » des écrans détail (recette / séance) :
 * spinner pendant l'appel, « Partagé sur le fil ✓ » une fois sur le fil,
 * message d'erreur en dessous sinon. [shared] est calculé par l'appelant
 * (id du post courant, cf. [ShareUiState.Shared]).
 */
@Composable
fun ShareButton(
    state: ShareUiState,
    shared: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (shared) Surface else Primary, RoundedCornerShape(14.dp))
                .clickable(enabled = !shared && state !is ShareUiState.Sharing, onClick = onClick)
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state is ShareUiState.Sharing -> CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp),
                )

                shared -> Text(
                    "Partagé sur le fil ✓",
                    color = Primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                else -> Text(
                    "Partager sur le fil",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        if (state is ShareUiState.Error) {
            Text(
                state.message,
                color = Accent,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
