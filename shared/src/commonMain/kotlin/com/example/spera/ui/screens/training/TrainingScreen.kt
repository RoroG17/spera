package com.example.spera.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spera.data.trainings.dayLabel
import com.example.spera.models.Training
import com.example.spera.viewmodels.TrainingsVM
import com.example.spera.viewmodels.states.CalendarMonth
import com.example.spera.viewmodels.states.TrainingsUiState
import kotlin.math.round

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

private val WEEKDAY_LABELS = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")

/**
 * Onglet Entraînement (US12/US14) : bouton de création de séance timer,
 * calendrier mensuel avec les jours de séance marqués, et liste des séances
 * en dessous — taper un jour marqué filtre la liste, taper une séance ouvre
 * son détail. Le header et le footer sont fournis par `MainScaffold`.
 *
 * [refreshSignal] : recharge le calendrier quand la valeur change (retour du
 * timer US14). [onCreateTraining] : ouvre le flux timer.
 */
@Composable
fun TrainingScreen(
    refreshSignal: Int = 0,
    onCreateTraining: () -> Unit = {},
    onOpenTraining: (Training) -> Unit = {},
    viewModel: TrainingsVM = viewModel { TrainingsVM() },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(refreshSignal) {
        if (refreshSignal > 0) viewModel.load()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            when (val s = state) {
                is TrainingsUiState.Loading -> CenteredBox {
                    CircularProgressIndicator(color = Primary)
                }

                is TrainingsUiState.Error -> CenteredBox {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(s.message, color = TextMuted, fontSize = 15.sp)
                        TextButton(onClick = viewModel::load) {
                            Text("Réessayer", color = Primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                is TrainingsUiState.Success -> {
                    MonthCalendar(
                        month = s.month,
                        onPreviousMonth = viewModel::onPreviousMonth,
                        onNextMonth = viewModel::onNextMonth,
                        onDaySelect = viewModel::onDaySelect,
                        modifier = Modifier.padding(top = 14.dp),
                    )

                    val selectedDay = s.month.selectedDay
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    ) {
                        Text(
                            if (selectedDay != null) "Séances du jour" else "Toutes les séances",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        if (selectedDay != null) {
                            TextButton(onClick = { viewModel.onDaySelect(selectedDay) }) {
                                Text("Tout afficher", color = Primary, fontSize = 13.sp)
                            }
                        }
                    }

                    if (s.trainings.isEmpty()) {
                        CenteredBox {
                            Text(
                                "Aucune séance pour l'instant.",
                                color = TextMuted,
                                fontSize = 15.sp,
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            items(items = s.trainings, key = { it.id }) { training ->
                                TrainingRow(training, onClick = { onOpenTraining(training) })
                            }
                            item { Box(modifier = Modifier.padding(bottom = 16.dp)) }
                        }
                    }
                }
            }
        }

        // US14 — création d'une séance timer (même patron que le « + » du fil).
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(56.dp)
                .background(Primary, CircleShape)
                .clickable(onClick = onCreateTraining),
            contentAlignment = Alignment.Center,
        ) {
            Text("+", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Calendrier mensuel : navigation ‹ ›, entêtes Lun→Dim, grille des jours. */
@Composable
private fun MonthCalendar(
    month: CalendarMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NavArrow("‹", onPreviousMonth)
            Text(
                month.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            NavArrow("›", onNextMonth)
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            WEEKDAY_LABELS.forEach { label ->
                Text(
                    label,
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        val rowCount = (month.firstDayOffset + month.dayCount + 6) / 7
        repeat(rowCount) { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                repeat(7) { column ->
                    val day = row * 7 + column - month.firstDayOffset + 1
                    if (day in 1..month.dayCount) {
                        DayCell(day, month, onDaySelect, modifier = Modifier.weight(1f))
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun NavArrow(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, color = TextPrimary, fontSize = 22.sp)
    }
}

/**
 * Un jour de la grille : point accent si séance, cercle primaire plein si
 * sélectionné, contour primaire pour aujourd'hui. Seuls les jours avec
 * séance sont cliquables.
 */
@Composable
private fun DayCell(
    day: Int,
    month: CalendarMonth,
    onDaySelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val marked = day in month.markedDays
    val selected = day == month.selectedDay

    Box(modifier = modifier.height(40.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .then(if (selected) Modifier.background(Primary) else Modifier)
                .then(
                    if (!selected && day == month.todayDay) {
                        Modifier.border(1.5.dp, Primary, CircleShape)
                    } else {
                        Modifier
                    },
                )
                .then(if (marked) Modifier.clickable { onDaySelect(day) } else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$day",
                color = when {
                    selected -> Color.White
                    marked -> TextPrimary
                    else -> TextMuted
                },
                fontSize = 13.sp,
                fontWeight = if (marked || selected) FontWeight.SemiBold else FontWeight.Normal,
            )
            if (marked && !selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 3.dp)
                        .size(4.dp)
                        .background(Accent, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun TrainingRow(training: Training, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(Surface, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            dayLabel(training.date),
            color = Accent,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(56.dp),
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(
                training.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            val meta = metaLine(training)
            if (meta.isNotEmpty()) {
                Text(
                    meta,
                    color = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
        Text("›", color = TextMuted, fontSize = 20.sp)
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** « 8.2 km · 42 min » si distance, sinon « 24 min · 310 kcal ». */
private fun metaLine(training: Training): String {
    val data = training.data ?: return ""
    return if (data.distanceKm != null) {
        "${formatKm(data.distanceKm)} · ${data.durationMinutes} min"
    } else {
        "${data.durationMinutes} min · ${data.caloriesBurned} kcal"
    }
}

/** 8.2 → « 8.2 km », 6.0 → « 6 km » (pas de String.format en commonMain). */
internal fun formatKm(km: Double): String {
    val tenths = round(km * 10).toLong()
    return if (tenths % 10 == 0L) "${tenths / 10} km" else "${tenths / 10}.${tenths % 10} km"
}
