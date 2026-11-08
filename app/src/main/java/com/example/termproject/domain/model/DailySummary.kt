package com.example.termproject.domain.model

/**
 * 특정 일자의 식사 요약 및 집계 결과 모델
 */
data class DailySummary(
    val dateISO: String, // "YYYY-MM-DD"
    val dateDisplay: String, // "2026년 11월 5일 (목)"
    val mealCount: Int,
    val totalCaloriesKcal: Double,
    val totalCarbohydrateG: Double,
    val totalProteinG: Double,
    val totalFatG: Double,
    val meals: List<MealRecord>
) {
    val roundedTotalCaloriesKcal: Double get() = totalCaloriesKcal.roundToDecimal(1)
    val roundedTotalCarbohydrateG: Double get() = totalCarbohydrateG.roundToDecimal(1)
    val roundedTotalProteinG: Double get() = totalProteinG.roundToDecimal(1)
    val roundedTotalFatG: Double get() = totalFatG.roundToDecimal(1)
}
