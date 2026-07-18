package com.example.spera.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spera.data.trainings.dateLabel
import com.example.spera.models.Training

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

/**
 * Détail d'une séance (US12) : pas d'écran dédié en maquette — reprend le
 * patron visuel de `RecipeDetailScreen` (photo, retour, tuiles de mesures).
 */
@Composable
fun TrainingDetailScreen(
    training: Training,
    onBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        // Photo (placeholder) avec retour par-dessus.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Surface),
        ) {
            Text(
                "photo de la séance",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.Center),
            )
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .size(42.dp)
                    .background(Background, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("‹", color = TextPrimary, fontSize = 24.sp)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                training.name,
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "par ${training.users.firstName} ${training.users.name} · ${dateLabel(training.date)}",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp),
            )

            // Mesures de la séance — champ « Data » de data.txt.
            val data = training.data
            if (data != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 18.dp),
                ) {
                    StatTile(
                        value = "${data.durationMinutes} min",
                        label = "durée",
                        color = Accent,
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${data.caloriesBurned}",
                        label = "kcal brûlées",
                        color = Primary,
                        modifier = Modifier.weight(1f),
                    )
                    if (data.distanceKm != null) {
                        StatTile(
                            value = formatKm(data.distanceKm),
                            label = "distance",
                            color = Accent,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Text(
                "Description",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 22.dp),
            )
            Text(
                training.description,
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp),
            )

            // Compteurs sociaux — interactions branchées avec US5.
            Text(
                "❤️ ${training.likes.size} · 💬 ${training.comments.size}",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 18.dp),
            )
        }
    }
}

@Composable
private fun StatTile(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Surface, RoundedCornerShape(14.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
    }
}
