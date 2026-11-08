package com.example.termproject.domain

import com.example.termproject.domain.model.FoodItem
import com.example.termproject.domain.model.MealNutrientSummary
import com.example.termproject.domain.model.SingleNutrientResult

/**
 * 섭취량(g) 기반 영양소 및 칼로리 비량 계산 순수 코틀린 계산기
 * 프레임워크나 UI 종속성이 없어 로컬 단위 테스트가 매우 용이함
 */
object NutrientCalculator {

    /**
     * 100g 당 영양소와 실제 섭취량(g)을 기반으로 개별 음식 영양소 계산
     * 공식: 실제 섭취 영양소 = (100g 기준 영양소 × 실제 섭취량) / 100
     */
    fun calculateSingleFoodNutrients(
        baseCarbs100g: Double,
        baseProtein100g: Double,
        baseFat100g: Double,
        baseCalories100g: Double,
        quantityGram: Double,
        foodName: String = ""
    ): SingleNutrientResult {
        require(quantityGram > 0.0) { "섭취량(g)은 0g보다 커야 합니다. (입력값: ${quantityGram}g)" }
        require(baseCarbs100g >= 0.0) { "탄수화물 수치는 0 이상이어야 합니다." }
        require(baseProtein100g >= 0.0) { "단백질 수치는 0 이상이어야 합니다." }
        require(baseFat100g >= 0.0) { "지방 수치는 0 이상이어야 합니다." }
        require(baseCalories100g >= 0.0) { "칼로리 수치는 0 이상이어야 합니다." }

        val carbs = (baseCarbs100g * quantityGram) / 100.0
        val protein = (baseProtein100g * quantityGram) / 100.0
        val fat = (baseFat100g * quantityGram) / 100.0
        val calories = (baseCalories100g * quantityGram) / 100.0

        return SingleNutrientResult(
            foodName = foodName,
            quantityGram = quantityGram,
            carbohydrateG = carbs,
            proteinG = protein,
            fatG = fat,
            caloriesKcal = calories
        )
    }

    /**
     * FoodItem과 섭취량(g)을 받아 개별 음식 영양소 계산
     */
    fun calculateForFoodItem(foodItem: FoodItem, quantityGram: Double): SingleNutrientResult {
        return calculateSingleFoodNutrients(
            baseCarbs100g = foodItem.carbohydratePer100g,
            baseProtein100g = foodItem.proteinPer100g,
            baseFat100g = foodItem.fatPer100g,
            baseCalories100g = foodItem.caloriePer100g,
            quantityGram = quantityGram,
            foodName = foodItem.name
        )
    }

    /**
     * 한 끼 식사에 포함된 여러 개별 음식 영양소 결과(SingleNutrientResult 리스트)의 총합 계산
     */
    fun calculateMealNutrients(items: List<SingleNutrientResult>): MealNutrientSummary {
        if (items.isEmpty()) {
            return MealNutrientSummary(
                totalQuantityGram = 0.0,
                totalCarbohydrateG = 0.0,
                totalProteinG = 0.0,
                totalFatG = 0.0,
                totalCaloriesKcal = 0.0,
                itemResults = emptyList()
            )
        }

        val totalQuantity = items.sumOf { it.quantityGram }
        val totalCarbs = items.sumOf { it.carbohydrateG }
        val totalProtein = items.sumOf { it.proteinG }
        val totalFat = items.sumOf { it.fatG }
        val totalCalories = items.sumOf { it.caloriesKcal }

        return MealNutrientSummary(
            totalQuantityGram = totalQuantity,
            totalCarbohydrateG = totalCarbs,
            totalProteinG = totalProtein,
            totalFatG = totalFat,
            totalCaloriesKcal = totalCalories,
            itemResults = items
        )
    }
}
