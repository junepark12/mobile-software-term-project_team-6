package com.example.termproject.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.termproject.data.local.entity.MealEntity
import com.example.termproject.data.local.entity.MealItemEntity
import com.example.termproject.data.repository.MealRepository
import com.example.termproject.domain.NutrientCalculator
import com.example.termproject.domain.model.FoodItem
import com.example.termproject.domain.model.MealNutrientSummary
import com.example.termproject.domain.model.MealType
import com.example.termproject.domain.model.SingleNutrientResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 선택된 음식 항목의 입력 상태 및 실시간 계산 결과
 */
data class SelectedFoodState(
    val foodItem: FoodItem,
    val quantityGramInput: String = "100",
    val quantityGramDouble: Double = 100.0,
    val isQuantityValid: Boolean = true,
    val errorMessage: String? = null,
    val calculatedNutrient: SingleNutrientResult? = null
)

/**
 * 식사 입력 화면 전체 UI State
 */
data class MealInputUiState(
    val selectedDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val selectedTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
    val selectedMealType: MealType = MealType.BREAKFAST,
    val photoUri: String? = null,
    val selectedFoods: List<SelectedFoodState> = emptyList(),
    val totalMealSummary: MealNutrientSummary = MealNutrientSummary(0.0, 0.0, 0.0, 0.0, 0.0, emptyList()),
    val isSaving: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val generalErrorMessage: String? = null
)

/**
 * MealInputScreen ViewModel
 */
class MealInputViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealInputUiState())
    val uiState: StateFlow<MealInputUiState> = _uiState.asStateFlow()

    fun onDateChange(newDate: String) {
        _uiState.update { it.copy(selectedDate = newDate) }
    }

    fun onTimeChange(newTime: String) {
        _uiState.update { it.copy(selectedTime = newTime) }
    }

    fun onMealTypeChange(mealType: MealType) {
        _uiState.update { it.copy(selectedMealType = mealType) }
    }

    /**
     * 식사 사진 URI 변경 및 지속적 읽기 권한(Persistable URI Permission) 획득
     */
    fun onPhotoUriChange(context: Context, uriString: String?) {
        if (!uriString.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(uriString)
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            } catch (_: Exception) {
                // 특정 URI가 Persistable 권한을 지원하지 않는 경우 예외 방지
            }
        }
        _uiState.update { it.copy(photoUri = uriString) }
    }

    /**
     * 검색 화면에서 음식 선택 시 항목 추가
     */
    fun addFoodItem(foodItem: FoodItem) {
        val initialQuantity = 100.0
        val initialNutrient = if (foodItem.isVerified) {
            NutrientCalculator.calculateForFoodItem(foodItem, initialQuantity)
        } else null

        val newFoodState = SelectedFoodState(
            foodItem = foodItem,
            quantityGramInput = "100",
            quantityGramDouble = initialQuantity,
            isQuantityValid = true,
            errorMessage = null,
            calculatedNutrient = initialNutrient
        )

        val updatedList = _uiState.value.selectedFoods + newFoodState
        _uiState.update {
            it.copy(
                selectedFoods = updatedList,
                totalMealSummary = calculateTotalSummary(updatedList)
            )
        }
    }

    /**
     * 음식 항목 삭제
     */
    fun removeFoodItem(index: Int) {
        val currentList = _uiState.value.selectedFoods.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _uiState.update {
                it.copy(
                    selectedFoods = currentList,
                    totalMealSummary = calculateTotalSummary(currentList)
                )
            }
        }
    }

    /**
     * 섭취량(g) 변경 시 즉시 영양소 실시간 재계산
     */
    fun updateFoodQuantity(index: Int, quantityInput: String) {
        val currentList = _uiState.value.selectedFoods.toMutableList()
        if (index !in currentList.indices) return

        val targetState = currentList[index]
        val parsedGram = quantityInput.toDoubleOrNull()

        val updatedState = when {
            quantityInput.isBlank() -> {
                targetState.copy(
                    quantityGramInput = quantityInput,
                    isQuantityValid = false,
                    errorMessage = "섭취량(g)을 입력해주세요.",
                    calculatedNutrient = null
                )
            }
            parsedGram == null || parsedGram <= 0.0 -> {
                targetState.copy(
                    quantityGramInput = quantityInput,
                    isQuantityValid = false,
                    errorMessage = "0g보다 큰 숫자를 입력해주세요.",
                    calculatedNutrient = null
                )
            }
            !targetState.foodItem.isVerified -> {
                targetState.copy(
                    quantityGramInput = quantityInput,
                    quantityGramDouble = parsedGram,
                    isQuantityValid = true,
                    errorMessage = "영양 정보 확인 불가",
                    calculatedNutrient = null
                )
            }
            else -> {
                val recalculated = NutrientCalculator.calculateForFoodItem(targetState.foodItem, parsedGram)
                targetState.copy(
                    quantityGramInput = quantityInput,
                    quantityGramDouble = parsedGram,
                    isQuantityValid = true,
                    errorMessage = null,
                    calculatedNutrient = recalculated
                )
            }
        }

        currentList[index] = updatedState
        _uiState.update {
            it.copy(
                selectedFoods = currentList,
                totalMealSummary = calculateTotalSummary(currentList)
            )
        }
    }

    /**
     * 전체 식사의 실시간 영양소 총합 계산
     */
    private fun calculateTotalSummary(foodsList: List<SelectedFoodState>): MealNutrientSummary {
        val validResults = foodsList.mapNotNull { it.calculatedNutrient }
        return NutrientCalculator.calculateMealNutrients(validResults)
    }

    /**
     * Room DB에 식사 및 음식 항목 기록 저장
     */
    fun saveMealRecord() {
        val state = _uiState.value

        if (state.selectedFoods.isEmpty()) {
            _uiState.update { it.copy(generalErrorMessage = "최소 1개 이상의 음식을 선택해주세요.") }
            return
        }

        val hasInvalidQuantity = state.selectedFoods.any { !it.isQuantityValid }
        if (hasInvalidQuantity) {
            _uiState.update { it.copy(generalErrorMessage = "섭취량(g)을 올바르게 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, generalErrorMessage = null) }

            val mealEntity = MealEntity(
                mealDate = state.selectedDate,
                mealTime = state.selectedTime,
                mealType = state.selectedMealType.name,
                imageUri = state.photoUri,
                createdAt = System.currentTimeMillis()
            )

            val itemEntities = state.selectedFoods.map { foodState ->
                MealItemEntity(
                    foodId = foodState.foodItem.id,
                    foodName = foodState.foodItem.name,
                    quantityGram = foodState.quantityGramDouble,
                    carbohydratePer100g = foodState.foodItem.carbohydratePer100g,
                    proteinPer100g = foodState.foodItem.proteinPer100g,
                    fatPer100g = foodState.foodItem.fatPer100g,
                    caloriePer100g = foodState.foodItem.caloriePer100g
                )
            }

            mealRepository.insertMealWithItems(mealEntity, itemEntities)
            _uiState.update { it.copy(isSaving = false, isSaveSuccess = true) }
        }
    }

    class Factory(private val mealRepository: MealRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MealInputViewModel(mealRepository) as T
        }
    }
}
