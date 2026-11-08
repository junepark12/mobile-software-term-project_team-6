package com.example.termproject.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.termproject.data.local.entity.MealEntity
import com.example.termproject.data.local.entity.MealItemEntity
import com.example.termproject.data.local.entity.MealWithItems
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 식사 기록(MealEntity & MealItemEntity) C.R.U.D 및 쿼리 접근 DAO
 */
@Dao
abstract class MealDao {
    @Transaction
    @Query("SELECT * FROM meals ORDER BY mealDate DESC, mealTime DESC")
    abstract fun getAllMealsWithItems(): Flow<List<MealWithItems>>

    @Transaction
    @Query("SELECT * FROM meals WHERE mealDate = :date ORDER BY mealTime ASC")
    abstract fun getMealsWithItemsByDate(date: String): Flow<List<MealWithItems>>

    @Transaction
    @Query("SELECT * FROM meals WHERE mealDate BETWEEN :startDate AND :endDate ORDER BY mealDate DESC, mealTime DESC")
    abstract fun getMealsWithItemsBetweenDates(startDate: String, endDate: String): Flow<List<MealWithItems>>

    @Transaction
    @Query("SELECT * FROM meals WHERE mealId = :mealId")
    abstract suspend fun getMealWithItemsById(mealId: Long): MealWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMeal(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMealItems(items: List<MealItemEntity>)

    @Delete
    abstract suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE mealId = :mealId")
    abstract suspend fun deleteMealById(mealId: Long)

    @Query("DELETE FROM meal_items WHERE mealId = :mealId")
    abstract suspend fun deleteMealItemsByMealId(mealId: Long)

    /**
     * 식사 및 세부 음식 항목을 단일 원자적 트랜잭션(@Transaction)으로 추가/수정하여 부분 저장 방지
     */
    @Transaction
    open suspend fun insertMealWithItemsTransaction(meal: MealEntity, items: List<MealItemEntity>): Long {
        if (meal.mealId > 0) {
            deleteMealItemsByMealId(meal.mealId)
            insertMeal(meal)
            val itemsWithMealId = items.map { it.copy(mealId = meal.mealId) }
            insertMealItems(itemsWithMealId)
            return meal.mealId
        } else {
            val mealId = insertMeal(meal)
            val itemsWithMealId = items.map { it.copy(mealId = mealId) }
            insertMealItems(itemsWithMealId)
            return mealId
        }
    }
}
