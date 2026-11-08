package com.example.termproject.domain.model

import com.example.termproject.data.local.entity.FoodEntity

/**
 * UI 및 Domain 영역에서 사용하는 식품 데이터 모델
 */
data class FoodItem(
    val id: Long,
    val foodCode: String?,
    val name: String,
    val category: String,
    val carbohydratePer100g: Double,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val caloriePer100g: Double,
    val dataSource: String,
    val isVerified: Boolean = true
)

/**
 * Entity -> Domain Model 변환 확장 함수
 */
fun FoodEntity.toDomainModel(): FoodItem = FoodItem(
    id = foodId,
    foodCode = foodCode,
    name = foodName,
    category = category,
    carbohydratePer100g = carbohydratePer100g,
    proteinPer100g = proteinPer100g,
    fatPer100g = fatPer100g,
    caloriePer100g = caloriePer100g,
    dataSource = dataSource,
    isVerified = isVerified
)
