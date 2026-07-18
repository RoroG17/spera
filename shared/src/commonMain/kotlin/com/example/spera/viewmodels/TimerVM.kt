package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.SessionManager
import com.example.spera.data.feed.todayIso
import com.example.spera.data.trainings.TrainingProvider
import com.example.spera.data.trainings.TrainingRepository
import com.example.spera.data.trainings.TrainingsResult
import com.example.spera.models.TimerExercise
import com.example.spera.models.Training
import com.example.spera.models.TrainingData
import com.example.spera.viewmodels.states.TimerLimits
import com.example.spera.viewmodels.states.TimerPhase
import com.example.spera.viewmodels.states.TimerUiState
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Durée du décompte de lancement (« 5, 4, 3, 2, 1, GO ! »). */
private const val COUNTDOWN_SECONDS = 5

/**
 * ViewModel du flux timer (US14) : configuration de la séance (Creation),
 * déroulé effort → repos → répétitions → exercice suivant (In Training),
 * puis résumé et enregistrement au calendrier (End Training). Référence de
 * patron : [NewPostVM] pour le flux plein écran, [TrainingsVM] pour la data.
 */
class TimerVM(
    private val trainingRepository: TrainingRepository = TrainingProvider.trainingRepository,
    private val sessionManager: SessionManager = AuthProvider.sessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimerUiState>(newCreation())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // Config figée au lancement (la Creation peut être éditée entre-temps).
    private var sessionName = ""
    private var exercises: List<TimerExercise> = emptyList()

    /** Temps effectif écoulé (les pauses ne comptent pas). */
    private var elapsedSeconds = 0

    private var nextExerciseId = 2

    // ----- Creation -------------------------------------------------------

    fun onSessionNameChange(name: String) = updateCreation { it.copy(sessionName = name) }

    fun onExerciseNameChange(id: Int, name: String) =
        updateExercise(id) { it.copy(name = name) }

    fun onEffortChange(id: Int, seconds: Int) =
        updateExercise(id) { it.copy(effortSeconds = seconds.coerceIn(TimerLimits.effortSeconds)) }

    fun onRestChange(id: Int, seconds: Int) =
        updateExercise(id) { it.copy(restSeconds = seconds.coerceIn(TimerLimits.restSeconds)) }

    fun onRoundsChange(id: Int, rounds: Int) =
        updateExercise(id) { it.copy(rounds = rounds.coerceIn(TimerLimits.rounds)) }

    fun onTransitionDefinedChange(id: Int, defined: Boolean) =
        updateExercise(id) { it.copy(transitionDefined = defined) }

    fun onTransitionChange(id: Int, seconds: Int) =
        updateExercise(id) { it.copy(transitionSeconds = seconds.coerceIn(TimerLimits.transitionSeconds)) }

    fun onAddExercise() = updateCreation {
        it.copy(exercises = it.exercises + defaultExercise(nextExerciseId++))
    }

    /** Retire un exercice ; la séance en garde toujours au moins un. */
    fun onRemoveExercise(id: Int) = updateCreation { state ->
        if (state.exercises.size <= 1) state
        else state.copy(exercises = state.exercises.filterNot { it.id == id })
    }

    // ----- In Training ----------------------------------------------------

    /**
     * Fige la config et ouvre le chrono en attente sur le premier exercice :
     * c'est l'utilisateur qui lance la séance (décompte 5 → GO ! ensuite).
     */
    fun onStart() {
        val creation = _uiState.value as? TimerUiState.Creation ?: return
        sessionName = creation.sessionName.trim().ifEmpty { "Séance timer" }
        exercises = creation.exercises.map { exercise ->
            exercise.copy(name = exercise.name.trim().ifEmpty { "Exercice" })
        }
        elapsedSeconds = 0

        _uiState.value = TimerUiState.InTraining(
            exercise = exercises.first(),
            exerciseIndex = 0,
            exerciseCount = exercises.size,
            phase = TimerPhase.Transition,
            round = 1,
            secondsLeft = 0,
            paused = false,
            awaitingManualStart = true,
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _uiState.value as? TimerUiState.InTraining ?: return@launch
                if (current.paused || current.awaitingManualStart) continue
                // Le décompte de lancement ne compte pas dans la durée.
                if (current.phase != TimerPhase.Countdown) elapsedSeconds++
                if (current.secondsLeft > 1) {
                    _uiState.value = current.copy(secondsLeft = current.secondsLeft - 1)
                } else {
                    val next = advance(current)
                    _uiState.value = next
                    if (next is TimerUiState.EndTraining) return@launch
                }
            }
        }
    }

    fun onPauseToggle() {
        val current = _uiState.value as? TimerUiState.InTraining ?: return
        _uiState.value = current.copy(paused = !current.paused)
    }

    /** Arrête la séance en cours et passe au résumé. */
    fun onStop() {
        if (_uiState.value !is TimerUiState.InTraining) return
        timerJob?.cancel()
        _uiState.value = endState()
    }

    /**
     * Lancement manuel (début de séance ou transition non définie) : démarre
     * le décompte « 5, 4, 3, 2, 1, GO ! » avant l'effort. secondsLeft = 6 :
     * l'UI affiche secondsLeft-1, la dernière seconde étant le « GO ! ».
     */
    fun onStartNextExercise() {
        val current = _uiState.value as? TimerUiState.InTraining ?: return
        if (!current.awaitingManualStart) return
        _uiState.value = current.copy(
            phase = TimerPhase.Countdown,
            secondsLeft = COUNTDOWN_SECONDS + 1,
            awaitingManualStart = false,
        )
    }

    /**
     * Phase suivante : repos → répétition suivante → transition vers
     * l'exercice suivant → fin. La transition dépend de la config de
     * l'exercice qui se termine : non définie → lancement manuel ; définie à
     * 0 s → enchaînement direct ; sinon décompte.
     */
    private fun advance(current: TimerUiState.InTraining): TimerUiState {
        val exercise = current.exercise
        if (current.phase == TimerPhase.Countdown || current.phase == TimerPhase.Transition) {
            return current.copy(phase = TimerPhase.Effort, secondsLeft = exercise.effortSeconds)
        }
        if (current.phase == TimerPhase.Effort && exercise.restSeconds > 0) {
            return current.copy(phase = TimerPhase.Rest, secondsLeft = exercise.restSeconds)
        }
        if (current.round < exercise.rounds) {
            return current.copy(
                phase = TimerPhase.Effort,
                round = current.round + 1,
                secondsLeft = exercise.effortSeconds,
            )
        }
        val nextIndex = current.exerciseIndex + 1
        if (nextIndex < exercises.size) {
            val next = exercises[nextIndex]
            val moved = current.copy(exercise = next, exerciseIndex = nextIndex, round = 1)
            return when {
                !exercise.transitionDefined -> moved.copy(
                    phase = TimerPhase.Transition,
                    secondsLeft = 0,
                    awaitingManualStart = true,
                )

                exercise.transitionSeconds == 0 -> moved.copy(
                    phase = TimerPhase.Effort,
                    secondsLeft = next.effortSeconds,
                )

                else -> moved.copy(
                    phase = TimerPhase.Transition,
                    secondsLeft = exercise.transitionSeconds,
                )
            }
        }
        return endState()
    }

    // ----- End Training ---------------------------------------------------

    /** Convertit la séance en [Training] et l'ajoute au calendrier. */
    fun onSave() {
        val end = _uiState.value as? TimerUiState.EndTraining ?: return
        if (end.saving || end.saved) return
        _uiState.value = end.copy(saving = true, error = null)

        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            if (user == null) {
                _uiState.value = end.copy(saving = false, error = "Session expirée, reconnecte-toi.")
                return@launch
            }
            val training = Training(
                id = "tr-timer-${Random.nextInt(1_000_000)}",
                date = todayIso(),
                name = end.sessionName,
                description = end.exercises.joinToString(" · ") {
                    "${it.name} ${it.rounds}×${it.effortSeconds}s/${it.restSeconds}s"
                },
                photo = "",
                users = user,
                likes = emptyList(),
                comments = emptyList(),
                data = TrainingData(
                    durationMinutes = (end.totalSeconds + 59) / 60,
                    // Estimation grossière (~8 kcal/min) en attendant de vraies mesures.
                    caloriesBurned = end.totalSeconds * 8 / 60,
                ),
            )
            when (val result = trainingRepository.saveTraining(user, training)) {
                is TrainingsResult.Success ->
                    _uiState.value = end.copy(saving = false, saved = true)

                is TrainingsResult.Error ->
                    _uiState.value = end.copy(saving = false, error = result.message)
            }
        }
    }

    /** Réinitialise le flux — appelé quand on quitte l'écran timer. */
    fun reset() {
        timerJob?.cancel()
        timerJob = null
        nextExerciseId = 2
        _uiState.value = newCreation()
    }

    // ----- Helpers --------------------------------------------------------

    private fun newCreation() = TimerUiState.Creation(
        sessionName = "Séance timer",
        exercises = listOf(defaultExercise(1)),
    )

    private fun defaultExercise(id: Int) =
        TimerExercise(id = id, name = "Exercice $id", effortSeconds = 30, restSeconds = 15, rounds = 8)

    private fun endState() = TimerUiState.EndTraining(
        sessionName = sessionName,
        exercises = exercises,
        totalSeconds = elapsedSeconds,
    )

    private inline fun updateCreation(block: (TimerUiState.Creation) -> TimerUiState.Creation) {
        val creation = _uiState.value as? TimerUiState.Creation ?: return
        _uiState.value = block(creation)
    }

    private inline fun updateExercise(id: Int, block: (TimerExercise) -> TimerExercise) =
        updateCreation { creation ->
            creation.copy(
                exercises = creation.exercises.map { if (it.id == id) block(it) else it },
            )
        }
}
