package com.example.termproject.domain.model

import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * 개별 음식의 섭취량에 따른 계산된 영양소 결과 모델
 */
data class SingleNutrientResult(
    val foodName: String,
    val quantityGram: Double,
    val carbohydrateG: Double,
    val proteinG: Double,
    val fatG: Double,
    val caloriesKcal: Double
) {
    val roundedCarbohydrateG: Double get() = carbohydrateG.roundToDecimal(1)
    val roundedProteinG: Double get() = proteinG.roundToDecimal(1)
    val roundedFatG: Double get() = fatG.roundToDecimal(1)
    val roundedCaloriesKcal: Double get() = caloriesKcal.roundToDecimal(1)
}

/**
 * 한 끼 식사 전체의 영양소 및 칼로리 합산 결과 모델
 */
data class MealNutrientSummary(
    val totalQuantityGram: Double,
    val totalCarbohydrateG: Double,
    val totalProteinG: Double,
    val totalFatG: Double,
    val totalCaloriesKcal: Double,
    val itemResults: List<SingleNutrientResult>
) {
    val roundedTotalQuantityGram: Double get() = totalQuantityGram.roundToDecimal(1)
    val roundedTotalCarbohydrateG: Double get() = totalCarbohydrateG.roundToDecimal(1)
    val roundedTotalProteinG: Double get() = totalProteinG.roundToDecimal(1)
    val roundedTotalFatG: Double get() = totalFatG.roundToDecimal(1)
    val roundedTotalCaloriesKcal: Double get() = totalCaloriesKcal.roundToDecimal(1)
}

/**
 * 소수점 자리수 반올림 확장 함수
 */
fun Double.roundToDecimal(decimals: Int = 1): Double {
    require(decimals >= 0) { "decimals는 0 이상이어야 합니다." }
    val factor = 10.0.pow(decimals.toDouble())
    return (this * factor).roundToInt() / factor
}

/**
 * UI 표시용 단위 포맷팅 확장 함수 (예: "45.0g", "307.5kcal")
 */
fun Double.formatWithUnit(unit: String, decimals: Int = 1): String {
    return String.format(Locale.getDefault(), "%.${decimals}f%s", this.roundToDecimal(decimals), unit)
}
