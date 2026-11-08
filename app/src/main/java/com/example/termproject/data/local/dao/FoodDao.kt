package com.example.termproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.termproject.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

/**
 * 표준 식품 및 영양 정보 DB 접근 DAO
 */
@Dao
interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY foodName ASC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE isVerified = 1 ORDER BY foodName ASC")
    fun getVerifiedFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE foodName LIKE '%' || :query || '%' ORDER BY foodName ASC")
    fun searchFoods(query: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE category = :category ORDER BY foodName ASC")
    fun getFoodsByCategory(category: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE foodId = :foodId")
    suspend fun getFoodById(foodId: Long): FoodEntity?

    @Query("SELECT * FROM foods WHERE foodCode = :foodCode")
    suspend fun getFoodByCode(foodCode: String): FoodEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoods(foods: List<FoodEntity>)

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun getFoodCount(): Int

    @Query("SELECT COUNT(*) FROM foods WHERE isVerified = 1")
    suspend fun getVerifiedFoodCount(): Int
}
