package com.example.termproject.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 특정 식사(MealEntity)에 포함된 개별 음식/반찬 항목 테이블 (1:N 관계)
 * 섭취 당시의 100g당 기준 영양소 수치 포함 (기록의 보존성 확보)
 */
@Entity(
    tableName = "meal_items",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["mealId"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["foodId"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["foodId"])
    ]
)
data class MealItemEntity(
    @PrimaryKey(autoGenerate = true)
    val mealItemId: Long = 0,
    val mealId: Long = 0,
    val foodId: Long? = null,
    val foodName: String,
    val quantityGram: Double,
    val carbohydratePer100g: Double = 0.0,
    val proteinPer100g: Double = 0.0,
    val fatPer100g: Double = 0.0,
    val caloriePer100g: Double = 0.0
)
