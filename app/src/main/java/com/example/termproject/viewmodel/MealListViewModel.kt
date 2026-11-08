package com.example.termproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.termproject.data.repository.MealRepository
import com.example.termproject.domain.DailySummaryCalculator
import com.example.termproject.domain.model.DailySummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * MealListScreen UI State 정의
 */
data class MealListUiState(
    val dailySummaries: List<DailySummary> = emptyList(),
    val totalMealCount: Int = 0,
    val isLoading: Boolean = false
)

/**
 * MealListScreen ViewModel
 */
class MealListViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    val uiState: StateFlow<MealListUiState> = mealRepository.getAllMeals()
        .map { allMeals ->
            val summaries = DailySummaryCalculator.calculateDailySummaries(allMeals)

            MealListUiState(
                dailySummaries = summaries,
                totalMealCount = allMeals.size,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MealListUiState(isLoading = true)
        )

    class Factory(private val mealRepository: MealRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MealListViewModel(mealRepository) as T
        }
    }
}
