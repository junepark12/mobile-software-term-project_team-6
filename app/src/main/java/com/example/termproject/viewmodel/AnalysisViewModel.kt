package com.example.termproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.termproject.data.repository.MealRepository
import com.example.termproject.domain.MealAnalysisCalculator
import com.example.termproject.domain.model.MonthlyAnalysisResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/**
 * AnalysisScreen UI State 정의
 */
data class AnalysisUiState(
    val monthlyAnalysis: MonthlyAnalysisResult? = null,
    val isLoading: Boolean = false
)

/**
 * AnalysisScreen ViewModel
 */
class AnalysisViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    val uiState: StateFlow<AnalysisUiState> = mealRepository.getAllMeals()
        .map { allMeals ->
            val result = MealAnalysisCalculator.calculateMonthlyAnalysis(allMeals, LocalDate.now())
            AnalysisUiState(
                monthlyAnalysis = result,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalysisUiState(isLoading = true)
        )

    class Factory(private val mealRepository: MealRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AnalysisViewModel(mealRepository) as T
        }
    }
}
