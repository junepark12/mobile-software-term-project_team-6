package com.example.termproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 100g당 식품 영양성분 정보 테이블 (식품의약품안전처 공공데이터 기반)
 */
@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val foodId: Long = 0,
    val foodCode: String? = null,
    val foodName: String,
    val category: String,
    val carbohydratePer100g: Double,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val caloriePer100g: Double,
    val dataSource: String = "식품의약품안전처 공공데이터",
    val isVerified: Boolean = true
)
