package com.example.termproject.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 식사 헤더(MealEntity)와 1:N으로 연결된 세부 음식 리스트(List<MealItemEntity>)를 조인한 릴레이션 모델
 */
data class MealWithItems(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "mealId"
    )
    val items: List<MealItemEntity>
)
