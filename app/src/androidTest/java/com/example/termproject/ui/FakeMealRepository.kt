package com.example.termproject.ui

import com.example.termproject.data.local.entity.MealEntity
import com.example.termproject.data.local.entity.MealItemEntity
import com.example.termproject.data.repository.MealRepository
import com.example.termproject.domain.model.MealRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * UI 테스트용 FakeMealRepository (독립적인 테스트 환경 보장)
 */
class FakeMealRepository(
    initialMeals: List<MealRecord> = emptyList()
) : MealRepository {

    private val _meals = MutableStateFlow(initialMeals)

    override fun getAllMeals(): Flow<List<MealRecord>> = _meals

    override fun getMealsByDate(date: String): Flow<List<MealRecord>> =
        _meals.map { list -> list.filter { it.date == date } }

    override fun getMealsBetweenDates(startDate: String, endDate: String): Flow<List<MealRecord>> =
        _meals.map { list -> list.filter { it.date in startDate..endDate } }

    override suspend fun getMealById(mealId: Long): MealRecord? =
        _meals.value.find { it.id == mealId }

    override suspend fun insertMealWithItems(meal: MealEntity, items: List<MealItemEntity>): Long {
        val newId = System.currentTimeMillis()
        return newId
    }

    override suspend fun deleteMealById(mealId: Long) {
        _meals.update { list -> list.filterNot { it.id == mealId } }
    }
}
