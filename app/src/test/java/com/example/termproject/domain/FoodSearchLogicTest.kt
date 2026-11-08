package com.example.termproject.domain

import com.example.termproject.domain.model.FoodItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [음식 검색 및 카테고리 필터링 도메인 로직 로컬 단위 테스트]
 */
class FoodSearchLogicTest {

    private val sampleFoods = listOf(
        FoodItem(1, "D000001", "쌀밥", "밥류", 31.6, 2.6, 0.4, 143.0, "식약처", true),
        FoodItem(2, "D000005", "김치볶음밥", "밥류", 28.0, 4.0, 4.5, 170.0, "식약처", true),
        FoodItem(3, "D000011", "된장찌개", "국·찌개류", 3.2, 2.8, 1.5, 38.0, "식약처", true),
        FoodItem(4, "D000012", "김치찌개", "국·찌개류", 2.8, 3.5, 2.2, 45.0, "식약처", true),
        FoodItem(5, "D000021", "제육볶음", "고기류", 6.5, 14.0, 12.0, 190.0, "식약처", true)
    )

    // 13. 음식 검색 키워드 필터링
    @Test
    fun test13_FoodSearchKeywordFiltering() {
        val query = "김치"
        val filtered = sampleFoods.filter { it.name.contains(query, ignoreCase = true) }

        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.name.contains("김치") })
    }

    @Test
    fun testFoodCategoryFiltering() {
        val category = "밥류"
        val filtered = sampleFoods.filter { it.category == category }

        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.category == "밥류" })
    }
}
