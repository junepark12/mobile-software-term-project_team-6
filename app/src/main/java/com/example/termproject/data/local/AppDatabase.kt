package com.example.termproject.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.termproject.data.local.dao.FoodDao
import com.example.termproject.data.local.dao.MealDao
import com.example.termproject.data.local.entity.FoodEntity
import com.example.termproject.data.local.entity.MealEntity
import com.example.termproject.data.local.entity.MealItemEntity

/**
 * 학식 및 식사 관리 데이터베이스 (Room Database)
 */
@Database(
    entities = [FoodEntity::class, MealEntity::class, MealItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cafeteria_meal_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
