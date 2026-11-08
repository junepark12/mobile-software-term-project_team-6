package com.example.termproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.termproject.data.repository.MealRepository
import com.example.termproject.domain.model.MealRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MealDetailScreen UI State 정의
 */
data class MealDetailUiState(
    val mealRecord: MealRecord? = null,
    val isLoading: Boolean = true,
    val isNotFound: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null
)

/**
 * MealDetailScreen ViewModel
 */
class MealDetailViewModel(
    private val mealId: Long,
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealDetailUiState())
    val uiState: StateFlow<MealDetailUiState> = _uiState.asStateFlow()

    init {
        loadMealDetail()
    }

    fun loadMealDetail() {
        if (mealId <= 0) {
            _uiState.update { it.copy(isLoading = false, isNotFound = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val record = mealRepository.getMealById(mealId)
            if (record != null) {
                _uiState.update {
                    it.copy(
                        mealRecord = record,
                        isLoading = false,
                        isNotFound = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        mealRecord = null,
                        isLoading = false,
                        isNotFound = true
                    )
                }
            }
        }
    }

    fun deleteMealRecord() {
        if (mealId <= 0) return
        viewModelScope.launch {
            mealRepository.deleteMealById(mealId)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }

    class Factory(
        private val mealId: Long,
        private val mealRepository: MealRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MealDetailViewModel(mealId, mealRepository) as T
        }
    }
}
