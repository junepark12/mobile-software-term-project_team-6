package com.example.termproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.termproject.data.repository.MealRepository
import com.example.termproject.domain.NutrientCalculator
import com.example.termproject.domain.model.MealNutrientSummary
import com.example.termproject.domain.model.MealRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * HomeScreen UI State 정의
 */
data class HomeUiState(
    val todayFormattedDate: String = "",
    val todayDateISO: String = "",
    val todayCalories: Double = 0.0,
    val todayMealCount: Int = 0,
    val todayNutrients: MealNutrientSummary = MealNutrientSummary(0.0, 0.0, 0.0, 0.0, 0.0, emptyList()),
    val recentMeals: List<MealRecord> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * HomeScreen ViewModel
 */
class HomeViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val todayLocalDate: LocalDate = LocalDate.now()
    private val todayISO: String = todayLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    private val todayDisplay: String = todayLocalDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN))

    val uiState: StateFlow<HomeUiState> = mealRepository.getMealsByDate(todayISO)
        .map { todayMeals ->
            val allSingleResults = todayMeals.flatMap { it.items }
            val summary = NutrientCalculator.calculateMealNutrients(allSingleResults)

            HomeUiState(
                todayFormattedDate = todayDisplay,
                todayDateISO = todayISO,
                todayCalories = summary.totalCaloriesKcal,
                todayMealCount = todayMeals.size,
                todayNutrients = summary,
                recentMeals = todayMeals,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(
                todayFormattedDate = todayDisplay,
                todayDateISO = todayISO,
                isLoading = true
            )
        )

    class Factory(private val mealRepository: MealRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(mealRepository) as T
        }
    }
}
