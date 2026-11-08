package com.example.termproject.domain

import com.example.termproject.domain.model.DailySummary
import com.example.termproject.domain.model.MealRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 날짜별 식사 기록 집계 및 그룹화 순수 도메인 계산기
 * - UI 프레임워크와 완전히 분리되어 순수 단위 테스트 가능
 */
object DailySummaryCalculator {

    /**
     * 전체 식사 기록 목록을 날짜별(YYYY-MM-DD)로 그룹화하고 총칼로리, 탄, 단, 지, 끼니 수 집계
     */
    fun calculateDailySummaries(meals: List<MealRecord>): List<DailySummary> {
        if (meals.isEmpty()) return emptyList()

        return meals.groupBy { it.date }
            .map { (dateISO, mealsInDate) ->
                calculateSummaryForDate(dateISO, mealsInDate)
            }
            .sortedByDescending { it.dateISO }
    }

    /**
     * 특정 날짜의 식사 기록에 대한 일일 집계 계산
     */
    fun calculateSummaryForDate(dateISO: String, mealsForDate: List<MealRecord>): DailySummary {
        val dateDisplay = try {
            val localDate = LocalDate.parse(dateISO)
            localDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN))
        } catch (_: Exception) {
            dateISO
        }

        val allSingleResults = mealsForDate.flatMap { it.items }
        val totalSummary = NutrientCalculator.calculateMealNutrients(allSingleResults)

        return DailySummary(
            dateISO = dateISO,
            dateDisplay = dateDisplay,
            mealCount = mealsForDate.size,
            totalCaloriesKcal = totalSummary.totalCaloriesKcal,
            totalCarbohydrateG = totalSummary.totalCarbohydrateG,
            totalProteinG = totalSummary.totalProteinG,
            totalFatG = totalSummary.totalFatG,
            meals = mealsForDate.sortedByDescending { it.time }
        )
    }
}
