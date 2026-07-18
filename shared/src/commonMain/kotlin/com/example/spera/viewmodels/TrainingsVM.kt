package com.example.spera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spera.data.auth.AuthProvider
import com.example.spera.data.auth.SessionManager
import com.example.spera.data.feed.todayIso
import com.example.spera.data.trainings.CivilDate
import com.example.spera.data.trainings.TrainingProvider
import com.example.spera.data.trainings.TrainingRepository
import com.example.spera.data.trainings.TrainingsResult
import com.example.spera.data.trainings.dayOfWeekIso
import com.example.spera.data.trainings.daysInMonth
import com.example.spera.data.trainings.monthTitle
import com.example.spera.data.trainings.parseIso
import com.example.spera.data.trainings.toEpochDays
import com.example.spera.models.Training
import com.example.spera.viewmodels.states.CalendarMonth
import com.example.spera.viewmodels.states.TrainingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel du calendrier d'entraînement (US12) : charge les séances de
 * l'utilisateur, marque leurs jours sur le mois affiché et filtre la liste
 * sur le jour sélectionné. Référence de patron : [LoginVM] / [HomeVM].
 */
class TrainingsVM(
    private val trainingRepository: TrainingRepository = TrainingProvider.trainingRepository,
    private val sessionManager: SessionManager = AuthProvider.sessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrainingsUiState>(TrainingsUiState.Loading)
    val uiState: StateFlow<TrainingsUiState> = _uiState.asStateFlow()

    private val today: CivilDate? = parseIso(todayIso())

    private var trainings: List<Training> = emptyList()
    private var year: Int = today?.year ?: 1970
    private var month: Int = today?.month ?: 1
    private var selectedDay: Int? = null

    init {
        load()
    }

    /** (Re)charge le calendrier. */
    fun load() {
        _uiState.value = TrainingsUiState.Loading

        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            if (user == null) {
                _uiState.value = TrainingsUiState.Error("Session expirée, reconnecte-toi.")
                return@launch
            }
            when (val result = trainingRepository.loadTrainings(user)) {
                is TrainingsResult.Success -> {
                    trainings = result.trainings
                    selectedDay = null
                    publish()
                }

                is TrainingsResult.Error ->
                    _uiState.value = TrainingsUiState.Error(result.message)
            }
        }
    }

    fun onPreviousMonth() = shiftMonth(-1)

    fun onNextMonth() = shiftMonth(+1)

    /** Filtre la liste sur [day] ; re-taper le même jour retire le filtre. */
    fun onDaySelect(day: Int) {
        if (_uiState.value !is TrainingsUiState.Success) return
        selectedDay = if (selectedDay == day) null else day
        publish()
    }

    private fun shiftMonth(delta: Int) {
        if (_uiState.value !is TrainingsUiState.Success) return
        val zeroBased = year * 12 + (month - 1) + delta
        year = zeroBased.floorDiv(12)
        month = zeroBased.mod(12) + 1
        selectedDay = null
        publish()
    }

    private fun publish() {
        val dates = trainings.associate { it.id to parseIso(it.date) }
        val markedDays = dates.values
            .filterNotNull()
            .filter { it.year == year && it.month == month }
            .map { it.day }
            .toSet()

        _uiState.value = TrainingsUiState.Success(
            month = CalendarMonth(
                title = monthTitle(year, month),
                firstDayOffset = dayOfWeekIso(CivilDate(year, month, 1).toEpochDays()) - 1,
                dayCount = daysInMonth(year, month),
                markedDays = markedDays,
                todayDay = today?.takeIf { it.year == year && it.month == month }?.day,
                selectedDay = selectedDay,
            ),
            trainings = when (val day = selectedDay) {
                null -> trainings
                else -> trainings.filter { dates[it.id] == CivilDate(year, month, day) }
            },
        )
    }
}
