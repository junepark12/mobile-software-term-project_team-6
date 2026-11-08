package com.example.termproject.domain.model

import com.example.termproject.data.local.entity.MealWithItems
import com.example.termproject.domain.NutrientCalculator

/**
 * Domain 영역에서 사용하는 식사 기록 모델
 */
data class MealRecord(
    val id: Long,
    val date: String, // "YYYY-MM-DD"
    val time: String, // "HH:mm"
    val mealType: MealType,
    val imageUri: String?,
    val items: List<SingleNutrientResult>,
    val summary: MealNutrientSummary
) {
    val displayFoodName: String
        get() = when {
            items.isEmpty() -> "식사 항목 없음"
            items.size == 1 -> items.first().foodName
            else -> "${items.first().foodName} 외 ${items.size - 1}건"
        }
}

/**
 * MealWithItems (Entity Relation) -> MealRecord (Domain Model) 변환 함수
 */
fun MealWithItems.toDomainModel(): MealRecord {
    val singleResults = items.map { item ->
        NutrientCalculator.calculateSingleFoodNutrients(
            baseCarbs100g = item.carbohydratePer100g,
            baseProtein100g = item.proteinPer100g,
            baseFat100g = item.fatPer100g,
            baseCalories100g = item.caloriePer100g,
            quantityGram = item.quantityGram,
            foodName = item.foodName
        )
    }
    val summary = NutrientCalculator.calculateMealNutrients(singleResults)
    val type = MealType.fromString(meal.mealType)

    return MealRecord(
        id = meal.mealId,
        date = meal.mealDate,
        time = meal.mealTime,
        mealType = type,
        imageUri = meal.imageUri,
        items = singleResults,
        summary = summary
    )
}
