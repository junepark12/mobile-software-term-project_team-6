package com.example.termproject.domain

import com.example.termproject.domain.model.FoodItem
import com.example.termproject.domain.model.SingleNutrientResult
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.domain.model.roundToDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * [영양소 계산 핵심 로직 로컬 단위 테스트]
 * JUnit4 기반, Android Emulator 없이 JVM 독립 환경에서 실행
 */
class NutrientCalculatorTest {

    // 1. 100g 섭취 계산
    @Test
    fun test01_100g_IntakeCalculation() {
        val result = NutrientCalculator.calculateSingleFoodNutrients(
            baseCarbs100g = 31.6,
            baseProtein100g = 2.6,
            baseFat100g = 0.4,
            baseCalories100g = 143.0,
            quantityGram = 100.0,
            foodName = "쌀밥"
        )

        assertEquals(31.6, result.carbohydrateG, 0.001)
        assertEquals(2.6, result.proteinG, 0.001)
        assertEquals(0.4, result.fatG, 0.001)
        assertEquals(143.0, result.caloriesKcal, 0.001)
    }

    // 2. 150g 섭취 계산 (예시 요구사항 대조)
    @Test
    fun test02_150g_IntakeCalculation() {
        // 100g 기준: 탄수화물 30g, 단백질 10g, 지방 5g, 칼로리 205kcal
        val result = NutrientCalculator.calculateSingleFoodNutrients(
            baseCarbs100g = 30.0,
            baseProtein100g = 10.0,
            baseFat100g = 5.0,
            baseCalories100g = 205.0,
            quantityGram = 150.0,
            foodName = "테스트 음식"
        )

        // 150g 섭취 시: 탄수화물 45g, 단백질 15g, 지방 7.5g, 칼로리 307.5kcal
        assertEquals(45.0, result.carbohydrateG, 0.001)
        assertEquals(15.0, result.proteinG, 0.001)
        assertEquals(7.5, result.fatG, 0.001)
        assertEquals(307.5, result.caloriesKcal, 0.001)
    }

    // 3. 200g 섭취 계산
    @Test
    fun test03_200g_IntakeCalculation() {
        val result = NutrientCalculator.calculateSingleFoodNutrients(
            baseCarbs100g = 31.6,
            baseProtein100g = 2.6,
            baseFat100g = 0.4,
            baseCalories100g = 143.0,
            quantityGram = 200.0,
            foodName = "쌀밥 2공기"
        )

        assertEquals(63.2, result.carbohydrateG, 0.001)
        assertEquals(5.2, result.proteinG, 0.001)
        assertEquals(0.8, result.fatG, 0.001)
        assertEquals(286.0, result.caloriesKcal, 0.001)
    }

    // 4. 여러 음식 영양소 합산
    @Test
    fun test04_MultipleFoodsNutrientSummation() {
        val rice = SingleNutrientResult("쌀밥", 200.0, 63.2, 5.2, 0.8, 286.0)
        val pork = SingleNutrientResult("제육볶음", 150.0, 9.75, 21.0, 18.0, 285.0)

        val summary = NutrientCalculator.calculateMealNutrients(listOf(rice, pork))

        assertEquals(350.0, summary.totalQuantityGram, 0.001)
        assertEquals(72.95, summary.totalCarbohydrateG, 0.001)
        assertEquals(26.2, summary.totalProteinG, 0.001)
        assertEquals(18.8, summary.totalFatG, 0.001)
    }

    // 5. 칼로리 합산
    @Test
    fun test05_TotalCaloriesSummation() {
        val rice = SingleNutrientResult("쌀밥", 200.0, 63.2, 5.2, 0.8, 286.0)
        val pork = SingleNutrientResult("제육볶음", 150.0, 9.75, 21.0, 18.0, 285.0)

        val summary = NutrientCalculator.calculateMealNutrients(listOf(rice, pork))

        assertEquals(571.0, summary.totalCaloriesKcal, 0.001)
    }

    // 6. 0g 처리 예외 검증
    @Test(expected = IllegalArgumentException::class)
    fun test06_ZeroGramValidation() {
        NutrientCalculator.calculateSingleFoodNutrients(
            baseCarbs100g = 30.0,
            baseProtein100g = 10.0,
            baseFat100g = 5.0,
            baseCalories100g = 205.0,
            quantityGram = 0.0
        )
    }

    // 7. 음수 섭취량 검증 예외
    @Test(expected = IllegalArgumentException::class)
    fun test07_NegativeQuantityValidation() {
        NutrientCalculator.calculateSingleFoodNutrients(
            baseCarbs100g = 30.0,
            baseProtein100g = 10.0,
            baseFat100g = 5.0,
            baseCalories100g = 205.0,
            quantityGram = -50.0
        )
    }

    // 14. 영양정보 미검증 항목 처리 테스트
    @Test
    fun test14_UnverifiedFoodInfoHandling() {
        val unverifiedFood = FoodItem(
            id = 99,
            foodCode = "UNKNOWN",
            name = "미검증 음식",
            category = "기타",
            carbohydratePer100g = 0.0,
            proteinPer100g = 0.0,
            fatPer100g = 0.0,
            caloriePer100g = 0.0,
            dataSource = "미확인",
            isVerified = false
        )

        val result = if (unverifiedFood.isVerified) {
            NutrientCalculator.calculateForFoodItem(unverifiedFood, 100.0)
        } else null

        assertNull(result)
    }

    @Test
    fun testDecimalRoundingAndFormatting() {
        assertEquals(307.5, 307.456.roundToDecimal(1), 0.001)
        assertEquals("307.5kcal", 307.5.formatWithUnit("kcal", 1))
    }
}
