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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spera.models.TimerExercise
import com.example.spera.viewmodels.TimerVM
import com.example.spera.viewmodels.states.TimerLimits
import com.example.spera.viewmodels.states.TimerPhase
import com.example.spera.viewmodels.states.TimerUiState
import kotlin.math.roundToInt

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)
private val Border = Color(0xFF302B3A)

/**
 * Flux timer effort/repos (US14, maquette 13) en plein écran, même patron
 * que `NewPostScreen` : configuration de la séance (un ou plusieurs
 * exercices, sliders + saisie manuelle), décompte avec pause, puis résumé
 * avec enregistrement au calendrier.
 */
@Composable
fun TimerScreen(
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: TimerVM = viewModel { TimerVM() },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        val s = state
        if (s is TimerUiState.EndTraining && s.saved) {
            onSaved()
            viewModel.reset()
        }
    }

    // Quitter le flux remet le VM à zéro (il survit à l'écran, cf. NewPostVM).
    val exit = {
        viewModel.reset()
        onBack()
    }

    when (val s = state) {
        is TimerUiState.Creation -> CreationContent(s, viewModel, onBack = exit)
        is TimerUiState.InTraining -> InTrainingContent(s, viewModel)
        is TimerUiState.EndTraining -> EndTrainingContent(s, viewModel, onDismiss = exit)
    }
}

// ----- Creation -----------------------------------------------------------

@Composable
private fun CreationContent(
    state: TimerUiState.Creation,
    viewModel: TimerVM,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        FlowHeader(title = "Timer effort / repos", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = state.sessionName,
                onValueChange = viewModel::onSessionNameChange,
                label = { Text("Nom de la séance") },
                singleLine = true,
                colors = fieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            state.exercises.forEachIndexed { index, exercise ->
                ExerciseEditor(
                    exercise = exercise,
                    removable = state.exercises.size > 1,
                    last = index == state.exercises.lastIndex,
                    viewModel = viewModel,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(Surface, RoundedCornerShape(14.dp))
                    .clickable(onClick = viewModel::onAddExercise)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "＋ Ajouter un exercice",
                    color = Primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        PrimaryButton(
            label = "Lancer la séance",
            onClick = viewModel::onStart,
            modifier = Modifier.padding(vertical = 12.dp),
        )
    }
}

/**
 * Carte d'édition d'un exercice : nom, effort, repos, répétitions, et — sauf
 * pour le dernier — le temps avant l'exercice suivant (case à cocher :
 * non défini = lancement manuel, 0 s = enchaînement direct).
 */
@Composable
private fun ExerciseEditor(
    exercise: TimerExercise,
    removable: Boolean,
    last: Boolean,
    viewModel: TimerVM,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = exercise.name,
                onValueChange = { viewModel.onExerciseNameChange(exercise.id, it) },
                label = { Text("Exercice") },
                singleLine = true,
                colors = fieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
            )
            if (removable) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(36.dp)
                        .background(Background, CircleShape)
                        .clickable { viewModel.onRemoveExercise(exercise.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✕", color = TextMuted, fontSize = 15.sp)
                }
            }
        }

        LabeledSlider(
            label = "Effort",
            value = exercise.effortSeconds,
            range = TimerLimits.effortSeconds,
            unit = "s",
            onChange = { viewModel.onEffortChange(exercise.id, it) },
            step = 5,
        )
        LabeledSlider(
            label = "Repos",
            value = exercise.restSeconds,
            range = TimerLimits.restSeconds,
            unit = "s",
            onChange = { viewModel.onRestChange(exercise.id, it) },
            step = 5,
        )
        LabeledSlider(
            label = "Répétitions",
            value = exercise.rounds,
            range = TimerLimits.rounds,
            unit = "",
            onChange = { viewModel.onRoundsChange(exercise.id, it) },
        )

        if (!last) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Checkbox(
                    checked = exercise.transitionDefined,
                    onCheckedChange = { viewModel.onTransitionDefinedChange(exercise.id, it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Primary,
                        uncheckedColor = TextMuted,
                        checkmarkColor = Color.White,
                    ),
                )
                Text(
                    "Définir le temps avant l'exercice suivant",
                    color = TextPrimary,
                    fontSize = 14.sp,
                )
            }
            if (exercise.transitionDefined) {
                LabeledSlider(
                    label = "Transition",
                    value = exercise.transitionSeconds,
                    range = TimerLimits.transitionSeconds,
                    unit = "s",
                    onChange = { viewModel.onTransitionChange(exercise.id, it) },
                    step = 5,
                )
            }
            Text(
                when {
                    !exercise.transitionDefined ->
                        "Tu lanceras l'exercice suivant à la main."

                    exercise.transitionSeconds == 0 ->
                        "À 0 s, l'exercice suivant s'enchaîne directement."

                    else ->
                        "Pause de ${exercise.transitionSeconds} s avant l'exercice suivant."
                },
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

/**
 * Slider rose (maquette 13) + valeur éditable à la main. [step] : cran du
 * slider (la saisie manuelle reste libre).
 */
@Composable
private fun LabeledSlider(
    label: String,
    value: Int,
    range: IntRange,
    unit: String,
    onChange: (Int) -> Unit,
    step: Int = 1,
) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                color = TextPrimary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
            )
            ValueField(value = value, range = range, onChange = onChange)
            if (unit.isNotEmpty()) {
                Text(
                    unit,
                    color = Accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.roundToInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first) / step - 1,
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
                inactiveTrackColor = Border,
                // Crans invisibles : on garde l'accroche sans pointiller la piste.
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            ),
        )
    }
}

/**
 * Valeur du slider, éditable au clavier. Le texte local n'écrase la saisie
 * que quand la valeur bouge ailleurs (slider) ou à la perte de focus, pour
 * laisser taper un nombre hors bornes en cours de frappe (ex. « 1 » → « 15 »).
 */
@Composable
private fun ValueField(value: Int, range: IntRange, onChange: (Int) -> Unit) {
    var text by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(value) {
        if (text.toIntOrNull() != value) text = value.toString()
    }

    BasicTextField(
        value = text,
        onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }.take(3)
            text = digits
            digits.toIntOrNull()?.let { if (it in range) onChange(it) }
        },
        textStyle = TextStyle(
            color = Accent,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(Accent),
        singleLine = true,
        modifier = Modifier
            .width(52.dp)
            .background(Background, RoundedCornerShape(8.dp))
            .onFocusChanged { if (!it.isFocused && text.toIntOrNull() != value) text = value.toString() }
            .padding(horizontal = 8.dp, vertical = 5.dp),
    )
}

// ----- In Training --------------------------------------------------------

@Composable
private fun InTrainingContent(state: TimerUiState.InTraining, viewModel: TimerVM) {
    val effort = state.phase == TimerPhase.Effort
    val counting = state.phase == TimerPhase.Countdown
    // Décompte de lancement : secondsLeft-1, la dernière seconde = « GO ! ».
    // Transition manuelle : le cercle affiche l'effort à venir en attendant.
    val circleText = when {
        counting -> if (state.secondsLeft <= 1) "GO !" else "${state.secondsLeft - 1}"
        state.awaitingManualStart -> formatSeconds(state.exercise.effortSeconds)
        else -> formatSeconds(state.secondsLeft)
    }
    val phaseLabel = when {
        counting -> null
        state.awaitingManualStart -> "PRÊT ?"
        effort -> "EFFORT"
        state.phase == TimerPhase.Rest -> "REPOS"
        else -> "TRANSITION"
    }
    val phaseColor = when {
        state.awaitingManualStart -> TextMuted
        effort -> Accent
        else -> Primary
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        // Le retour arrête la séance : on retombe sur le résumé, rien n'est perdu.
        FlowHeader(title = "Timer effort / repos", onBack = viewModel::onStop)

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                state.exercise.name,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Exercice ${state.exerciseIndex + 1}/${state.exerciseCount} · " +
                    "Répétition ${state.round}/${state.exercise.rounds}",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp),
            )

            Box(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(240.dp)
                    .border(6.dp, if (counting) Accent else Primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        circleText,
                        color = if (counting) Accent else TextPrimary,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    if (phaseLabel != null) {
                        Text(
                            phaseLabel,
                            color = phaseColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                        )
                    }
                }
            }

            if (state.paused) {
                Text(
                    "En pause",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 18.dp),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 12.dp),
        ) {
            if (state.awaitingManualStart) {
                ActionButton(
                    label = if (state.exerciseIndex == 0) "Lancer la séance" else "Lancer l'exercice",
                    background = Primary,
                    labelColor = Color.White,
                    onClick = viewModel::onStartNextExercise,
                    modifier = Modifier.weight(1f),
                )
            } else {
                ActionButton(
                    label = if (state.paused) "Reprendre" else "Pause",
                    background = Surface,
                    labelColor = TextPrimary,
                    onClick = viewModel::onPauseToggle,
                    modifier = Modifier.weight(1f),
                )
            }
            ActionButton(
                label = "Terminer",
                background = Accent,
                labelColor = Color.White,
                onClick = viewModel::onStop,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ----- End Training -------------------------------------------------------

@Composable
private fun EndTrainingContent(
    state: TimerUiState.EndTraining,
    viewModel: TimerVM,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        FlowHeader(title = "Séance terminée", onBack = onDismiss)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("💪", fontSize = 44.sp, modifier = Modifier.padding(top = 24.dp))
            Text(
                state.sessionName,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                "Durée : ${formatTotal(state.totalSeconds)}",
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 6.dp),
            )

            state.exercises.forEach { exercise ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Surface, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Text(
                        exercise.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "${exercise.rounds} × ${exercise.effortSeconds}s / ${exercise.restSeconds}s",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (state.error != null) {
                Text(
                    state.error,
                    color = Accent,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }

        PrimaryButton(
            label = "Enregistrer la séance",
            onClick = viewModel::onSave,
            loading = state.saving,
            modifier = Modifier.padding(top = 12.dp),
        )
        TextButton(
            onClick = onDismiss,
            enabled = !state.saving,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
        ) {
            Text("Ignorer", color = TextMuted, fontSize = 14.sp)
        }
    }
}

// ----- Communs ------------------------------------------------------------

/** Header plein écran : retour + titre (patron `NewPostScreen`). */
@Composable
private fun FlowHeader(title: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 14.dp, bottom = 20.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Surface, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Text("‹", color = TextPrimary, fontSize = 24.sp)
        }
        Text(
            title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 14.dp),
        )
    }
}

@Composable
private fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary, RoundedCornerShape(24.dp))
            .clickable(enabled = !loading, onClick = onClick)
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
            )
        } else {
            Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    background: Color,
    labelColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = labelColor, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
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

/** 95 → « 1:35 », 30 → « 30s » (affichage du cercle, cf. maquette). */
private fun formatSeconds(seconds: Int): String =
    if (seconds >= 60) {
        val s = seconds % 60
        "${seconds / 60}:${if (s < 10) "0$s" else "$s"}"
    } else {
        "${seconds}s"
    }

/** « 12 min 30 », « 45 s »… durée totale du résumé. */
private fun formatTotal(seconds: Int): String {
    if (seconds < 60) return "$seconds s"
    val s = seconds % 60
    return if (s == 0) "${seconds / 60} min" else "${seconds / 60} min $s"
}
