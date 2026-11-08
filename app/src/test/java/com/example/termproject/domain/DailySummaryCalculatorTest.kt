package com.example.termproject.domain

import com.example.termproject.domain.model.MealNutrientSummary
import com.example.termproject.domain.model.MealRecord
import com.example.termproject.domain.model.MealType
import com.example.termproject.domain.model.SingleNutrientResult
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * DailySummaryCalculator 날짜별 요약 및 영양소 집계 도메인 단위 테스트
 */
class DailySummaryCalculatorTest {

    // 8. 날짜별 총칼로리 및 9. 날짜별 끼니 수
    @Test
    fun test08_09_DailyTotalCaloriesAndMealCount() {
        val item1 = SingleNutrientResult("쌀밥", 200.0, 63.2, 5.2, 0.8, 286.0)
        val item2 = SingleNutrientResult("제육볶음", 150.0, 9.75, 21.0, 18.0, 285.0)

        val meal1 = MealRecord(
            id = 1,
            date = "2026-11-05",
            time = "12:30",
            mealType = MealType.LUNCH,
            imageUri = null,
            items = listOf(item1, item2),
            summary = MealNutrientSummary(350.0, 72.95, 26.2, 18.8, 571.0, listOf(item1, item2))
        )

        val item3 = SingleNutrientResult("된장찌개", 200.0, 6.4, 5.6, 3.0, 76.0)
        val meal2 = MealRecord(
            id = 2,
            date = "2026-11-05",
            time = "18:30",
            mealType = MealType.DINNER,
            imageUri = null,
            items = listOf(item3),
            summary = MealNutrientSummary(200.0, 6.4, 5.6, 3.0, 76.0, listOf(item3))
        )

        val summaries = DailySummaryCalculator.calculateDailySummaries(listOf(meal1, meal2))

        assertEquals(1, summaries.size)
        val todaySummary = summaries.first()
        assertEquals("2026-11-05", todaySummary.dateISO)

        // 9. 날짜별 끼니 수 검증 (2회)
        assertEquals(2, todaySummary.mealCount)

        // 8. 날짜별 총 칼로리 검증 (286.0 + 285.0 + 76.0 = 647.0 kcal)
        assertEquals(647.0, todaySummary.totalCaloriesKcal, 0.001)
        assertEquals(79.35, todaySummary.totalCarbohydrateG, 0.001)
        assertEquals(31.8, todaySummary.totalProteinG, 0.001)
        assertEquals(21.8, todaySummary.totalFatG, 0.001)
    }

    @Test
    fun testSortingDescendingByDate() {
        val meal1 = MealRecord(
            id = 1,
            date = "2026-11-01",
            time = "12:00",
            mealType = MealType.LUNCH,
            imageUri = null,
            items = emptyList(),
            summary = MealNutrientSummary(0.0, 0.0, 0.0, 0.0, 0.0, emptyList())
        )

        val meal2 = MealRecord(
            id = 2,
            date = "2026-11-05",
            time = "12:00",
            mealType = MealType.LUNCH,
            imageUri = null,
            items = emptyList(),
            summary = MealNutrientSummary(0.0, 0.0, 0.0, 0.0, 0.0, emptyList())
        )

        val summaries = DailySummaryCalculator.calculateDailySummaries(listOf(meal1, meal2))

        assertEquals(2, summaries.size)
        assertEquals("2026-11-05", summaries[0].dateISO)
        assertEquals("2026-11-01", summaries[1].dateISO)
    }

    // 12. 데이터 없음 처리
    @Test
    fun test12_EmptyMealDataHandling() {
        val summaries = DailySummaryCalculator.calculateDailySummaries(emptyList())
        assertEquals(0, summaries.size)
    }
}
