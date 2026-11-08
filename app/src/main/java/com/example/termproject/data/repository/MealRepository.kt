package com.example.termproject.data.repository

import com.example.termproject.data.local.dao.MealDao
import com.example.termproject.data.local.entity.MealEntity
import com.example.termproject.data.local.entity.MealItemEntity
import com.example.termproject.domain.model.MealRecord
import com.example.termproject.domain.model.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 식사 기록 C.R.U.D 및 조회 Repository 인터페이스 및 구현체
 */
interface MealRepository {
    fun getAllMeals(): Flow<List<MealRecord>>
    fun getMealsByDate(date: String): Flow<List<MealRecord>>
    fun getMealsBetweenDates(startDate: String, endDate: String): Flow<List<MealRecord>>
    suspend fun getMealById(mealId: Long): MealRecord?
    suspend fun insertMealWithItems(meal: MealEntity, items: List<MealItemEntity>): Long
    suspend fun deleteMealById(mealId: Long)
}

class MealRepositoryImpl(
    private val mealDao: MealDao
) : MealRepository {

    override fun getAllMeals(): Flow<List<MealRecord>> {
        return mealDao.getAllMealsWithItems().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getMealsByDate(date: String): Flow<List<MealRecord>> {
        return mealDao.getMealsWithItemsByDate(date).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getMealsBetweenDates(startDate: String, endDate: String): Flow<List<MealRecord>> {
        return mealDao.getMealsWithItemsBetweenDates(startDate, endDate).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getMealById(mealId: Long): MealRecord? {
        return mealDao.getMealWithItemsById(mealId)?.toDomainModel()
    }

    /**
     * Room @Transaction을 호출하여 단일 원자적 트랜잭션으로 부분 저장 방지
     */
    override suspend fun insertMealWithItems(meal: MealEntity, items: List<MealItemEntity>): Long {
        return mealDao.insertMealWithItemsTransaction(meal, items)
    }

    override suspend fun deleteMealById(mealId: Long) {
        mealDao.deleteMealById(mealId)
    }
}
