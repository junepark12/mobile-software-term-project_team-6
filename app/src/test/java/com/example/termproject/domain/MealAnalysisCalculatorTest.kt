package com.example.termproject.domain

import com.example.termproject.domain.model.MealNutrientSummary
import com.example.termproject.domain.model.MealRecord
import com.example.termproject.domain.model.MealType
import com.example.termproject.domain.model.SingleNutrientResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * MealAnalysisCalculator 최근 1개월 식사 분석 도메인 단위 테스트
 */
class MealAnalysisCalculatorTest {

    // 10. 최근 1개월 데이터 필터링 & 11. 끼니별 영양소 합산
    @Test
    fun test10_11_Past30DaysFilteringAndMealTypeStats() {
        val today = LocalDate.of(2026, 11, 5)

        val lunchItem = SingleNutrientResult("쌀밥", 200.0, 63.2, 5.2, 0.8, 286.0)
        val lunchMeal = MealRecord(
            id = 1,
            date = "2026-11-05",
            time = "12:00",
            mealType = MealType.LUNCH,
            imageUri = null,
            items = listOf(lunchItem),
            summary = MealNutrientSummary(200.0, 63.2, 5.2, 0.8, 286.0, listOf(lunchItem))
        )

        val dinnerItem = SingleNutrientResult("제육볶음", 150.0, 9.75, 21.0, 18.0, 285.0)
        val dinnerMeal = MealRecord(
            id = 2,
            date = "2026-11-04",
            time = "18:00",
            mealType = MealType.DINNER,
            imageUri = null,
            items = listOf(dinnerItem),
            summary = MealNutrientSummary(150.0, 9.75, 21.0, 18.0, 285.0, listOf(dinnerItem))
        )

        val result = MealAnalysisCalculator.calculateMonthlyAnalysis(
            listOf(lunchMeal, dinnerMeal),
            today
        )

        assertTrue(result.hasData)
        assertEquals(571.0, result.totalCaloriesKcal, 0.001)
        assertEquals(19.033, result.dailyAverageCaloriesKcal, 0.01)

        // 11. 끼니별 영양소 합산 검증 (조식/중식/석식/간식 4개 항목)
        assertEquals(4, result.mealTypeStats.size)

        val lunchStat = result.mealTypeStats.find { it.mealType == MealType.LUNCH }
        val dinnerStat = result.mealTypeStats.find { it.mealType == MealType.DINNER }
        val breakfastStat = result.mealTypeStats.find { it.mealType == MealType.BREAKFAST }

        assertEquals(1, lunchStat?.mealCount ?: 0)
        assertEquals(286.0, lunchStat?.totalCaloriesKcal ?: 0.0, 0.001)

        assertEquals(1, dinnerStat?.mealCount ?: 0)
        assertEquals(285.0, dinnerStat?.totalCaloriesKcal ?: 0.0, 0.001)

        // 기록 없는 조식의 경우 0.0 kcal / 0회
        assertEquals(0, breakfastStat?.mealCount ?: -1)
        assertEquals(0.0, breakfastStat?.totalCaloriesKcal ?: -1.0, 0.001)
    }

    // 15. 날짜 경계 조건 테스트 (30일 범위 경계)
    @Test
    fun test15_30DaysDateBoundaryFiltering() {
        val today = LocalDate.of(2026, 11, 5)
        // 30일 경계: 2026-10-07 ~ 2026-11-05
        val boundaryMealIn = MealRecord(
            id = 1,
            date = "2026-10-07",
            time = "12:00",
            mealType = MealType.LUNCH,
            imageUri = null,
            items = listOf(SingleNutrientResult("경계내음식", 100.0, 10.0, 10.0, 10.0, 100.0)),
            summary = MealNutrientSummary(100.0, 10.0, 10.0, 10.0, 100.0, emptyList())
        )

        val boundaryMealOut = MealRecord(
            id = 2,
            date = "2026-10-06", // 31일 전 -> 경계 밖
            time = "12:00",
            mealType = MealType.LUNCH,
            imageUri = null,
            items = listOf(SingleNutrientResult("경계밖음식", 100.0, 10.0, 10.0, 10.0, 100.0)),
            summary = MealNutrientSummary(100.0, 10.0, 10.0, 10.0, 100.0, emptyList())
        )

        val result = MealAnalysisCalculator.calculateMonthlyAnalysis(
            listOf(boundaryMealIn, boundaryMealOut),
            today
        )

        assertEquals(100.0, result.totalCaloriesKcal, 0.001) // boundaryMealIn만 포함
    }
}
