package com.example.termproject.data.repository

import android.content.Context
import com.example.termproject.data.local.dao.FoodDao
import com.example.termproject.data.local.entity.FoodEntity
import com.example.termproject.domain.model.FoodItem
import com.example.termproject.domain.model.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray

/**
 * 식품 데이터 접근을 위한 Repository 인터페이스 및 구현체
 */
interface FoodRepository {
    fun getAllFoods(): Flow<List<FoodItem>>
    fun getVerifiedFoods(): Flow<List<FoodItem>>
    fun searchFoods(query: String): Flow<List<FoodItem>>
    fun getFoodsByCategory(category: String): Flow<List<FoodItem>>
    suspend fun getFoodById(foodId: Long): FoodItem?
    suspend fun initializeFoodDatabase(): Int
    suspend fun getFoodCount(): Int
    suspend fun getVerifiedFoodCount(): Int
}

class FoodRepositoryImpl(
    private val context: Context,
    private val foodDao: FoodDao
) : FoodRepository {

    override fun getAllFoods(): Flow<List<FoodItem>> {
        return foodDao.getAllFoods().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getVerifiedFoods(): Flow<List<FoodItem>> {
        return foodDao.getVerifiedFoods().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun searchFoods(query: String): Flow<List<FoodItem>> {
        return foodDao.searchFoods(query).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getFoodsByCategory(category: String): Flow<List<FoodItem>> {
        return foodDao.getFoodsByCategory(category).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getFoodById(foodId: Long): FoodItem? {
        return foodDao.getFoodById(foodId)?.toDomainModel()
    }

    override suspend fun getFoodCount(): Int {
        return foodDao.getFoodCount()
    }

    override suspend fun getVerifiedFoodCount(): Int {
        return foodDao.getVerifiedFoodCount()
    }

    /**
     * assets/foods.json에서 100개 표준 데이터를 읽어와 Room DB에 초기화
     * 중복 등록 방지 (DB에 이미 100개 이상의 데이터가 존재하면 건너뜀)
     */
    override suspend fun initializeFoodDatabase(): Int = withContext(Dispatchers.IO) {
        val currentCount = foodDao.getFoodCount()
        if (currentCount >= 100) {
            return@withContext currentCount
        }

        val jsonString = context.assets.open("foods.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val foodList = mutableListOf<FoodEntity>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val code = if (jsonObject.has("foodCode")) jsonObject.getString("foodCode") else null
            val source = if (jsonObject.has("dataSource")) jsonObject.getString("dataSource") else "식품의약품안전처 공공데이터"
            val verified = if (jsonObject.has("isVerified")) jsonObject.getBoolean("isVerified") else true

            foodList.add(
                FoodEntity(
                    foodCode = code,
                    foodName = jsonObject.getString("foodName"),
                    category = jsonObject.getString("category"),
                    carbohydratePer100g = jsonObject.getDouble("carbohydratePer100g"),
                    proteinPer100g = jsonObject.getDouble("proteinPer100g"),
                    fatPer100g = jsonObject.getDouble("fatPer100g"),
                    caloriePer100g = jsonObject.getDouble("caloriePer100g"),
                    dataSource = source,
                    isVerified = verified
                )
            )
        }

        foodDao.insertFoods(foodList)
        return@withContext foodDao.getFoodCount()
    }
}
