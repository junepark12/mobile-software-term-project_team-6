package com.example.termproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 사용자가 등록한 식사 헤더 정보 테이블
 */
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val mealId: Long = 0,
    val mealDate: String, // "YYYY-MM-DD"
    val mealTime: String, // "HH:mm"
    val mealType: String, // "BREAKFAST", "LUNCH", "DINNER", "SNACK"
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
