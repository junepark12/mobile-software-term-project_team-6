package com.example.termproject.domain.model

/**
 * 특정 일자의 일일 칼로리 포인트 모델 (차트 렌더링용)
 */
data class DailyCaloriePoint(
    val dateISO: String, // "YYYY-MM-DD"
    val dateShortDisplay: String, // "MM/dd"
    val caloriesKcal: Double
) {
    val roundedCaloriesKcal: Double get() = caloriesKcal.roundToDecimal(1)
}

/**
 * 식사 유형별(조식, 중식, 석식, 간식·음료) 영양 분석 수치 모델
 */
data class MealTypeNutrition(
    val mealType: MealType,
    val mealCount: Int,
    val totalCaloriesKcal: Double,
    val totalCarbsG: Double,
    val totalProteinG: Double,
    val totalFatG: Double
) {
    val roundedTotalCaloriesKcal: Double get() = totalCaloriesKcal.roundToDecimal(1)
    val roundedTotalCarbsG: Double get() = totalCarbsG.roundToDecimal(1)
    val roundedTotalProteinG: Double get() = totalProteinG.roundToDecimal(1)
    val roundedTotalFatG: Double get() = totalFatG.roundToDecimal(1)
}

/**
 * 최근 1개월(30일) 식사 종합 분석 결과 모델
 */
data class MonthlyAnalysisResult(
    val startDateISO: String,
    val endDateISO: String,
    val totalCaloriesKcal: Double,
    val dailyAverageCaloriesKcal: Double,
    val activeDaysCount: Int,
    val totalSummary: MealNutrientSummary,
    val dailyPoints: List<DailyCaloriePoint>,
    val mealTypeStats: List<MealTypeNutrition>
) {
    val roundedTotalCaloriesKcal: Double get() = totalCaloriesKcal.roundToDecimal(1)
    val roundedDailyAverageCaloriesKcal: Double get() = dailyAverageCaloriesKcal.roundToDecimal(1)
    val hasData: Boolean get() = totalCaloriesKcal > 0.0
}
