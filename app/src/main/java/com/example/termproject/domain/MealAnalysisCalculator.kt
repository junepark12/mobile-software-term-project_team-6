package com.example.termproject.domain

import com.example.termproject.domain.model.DailyCaloriePoint
import com.example.termproject.domain.model.MealRecord
import com.example.termproject.domain.model.MealType
import com.example.termproject.domain.model.MealTypeNutrition
import com.example.termproject.domain.model.MonthlyAnalysisResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 최근 1개월(30일) 실제 날짜 범위 기반 식사 및 영양 분석 도메인 계산기
 * - 프레임워크 독립적으로 순수 코틀린 단위 테스트 가능
 */
object MealAnalysisCalculator {

    /**
     * 오늘(todayDate) 기준 과거 30일 동안의 식사 분석 결과를 계산
     */
    fun calculateMonthlyAnalysis(
        meals: List<MealRecord>,
        todayDate: LocalDate = LocalDate.now()
    ): MonthlyAnalysisResult {
        val endDateISO = todayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val startDate = todayDate.minusDays(29)
        val startDateISO = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // 1. 기간 내 식사만 필터링 (실제 30일 날짜 범위 기준)
        val filteredMeals = meals.filter { it.date in startDateISO..endDateISO }

        // 2. 날짜별 식사 Map 생성
        val mealsByDate = filteredMeals.groupBy { it.date }

        // 3. 30일 간의 날짜 포인트 생성 (기록이 없는 날짜는 0.0 kcal)
        val dailyPoints = mutableListOf<DailyCaloriePoint>()
        var currentDate = startDate
        val shortDateFormatter = DateTimeFormatter.ofPattern("MM/dd", Locale.KOREAN)

        while (!currentDate.isAfter(todayDate)) {
            val dateISO = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val dateShort = currentDate.format(shortDateFormatter)
            val mealsOnDate = mealsByDate[dateISO] ?: emptyList()
            val caloriesOnDate = mealsOnDate.sumOf { it.summary.totalCaloriesKcal }

            dailyPoints.add(
                DailyCaloriePoint(
                    dateISO = dateISO,
                    dateShortDisplay = dateShort,
                    caloriesKcal = caloriesOnDate
                )
            )
            currentDate = currentDate.plusDays(1)
        }

        // 4. 기간 내 총 영양소 합계 및 하루 평균 칼로리 (30일 기준)
        val allSingleResults = filteredMeals.flatMap { it.items }
        val totalSummary = NutrientCalculator.calculateMealNutrients(allSingleResults)
        val activeDaysCount = mealsByDate.size
        val dailyAverageCalories = totalSummary.totalCaloriesKcal / 30.0

        // 5. 식사 구분별(조식, 중식, 석식, 간식·음료) 영양소 분석
        val mealsByType = filteredMeals.groupBy { it.mealType }
        val mealTypeStats = MealType.entries.map { type ->
            val mealsInType = mealsByType[type] ?: emptyList()
            val typeSingleResults = mealsInType.flatMap { it.items }
            val summary = NutrientCalculator.calculateMealNutrients(typeSingleResults)

            MealTypeNutrition(
                mealType = type,
                mealCount = mealsInType.size,
                totalCaloriesKcal = summary.totalCaloriesKcal,
                totalCarbsG = summary.totalCarbohydrateG,
                totalProteinG = summary.totalProteinG,
                totalFatG = summary.totalFatG
            )
        }

        return MonthlyAnalysisResult(
            startDateISO = startDateISO,
            endDateISO = endDateISO,
            totalCaloriesKcal = totalSummary.totalCaloriesKcal,
            dailyAverageCaloriesKcal = dailyAverageCalories,
            activeDaysCount = activeDaysCount,
            totalSummary = totalSummary,
            dailyPoints = dailyPoints,
            mealTypeStats = mealTypeStats
        )
    }
}
