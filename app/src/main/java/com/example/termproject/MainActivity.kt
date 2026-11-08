package com.example.termproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.termproject.data.local.AppDatabase
import com.example.termproject.data.repository.FoodRepositoryImpl
import com.example.termproject.ui.screens.MainScreen
import com.example.termproject.ui.theme.TermprojectTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 100개 공공 식품 DB 최초 초기화 실행 (이미 존재할 경우 자동 스킵)
        val database = AppDatabase.getDatabase(this)
        val foodRepository = FoodRepositoryImpl(this, database.foodDao())
        lifecycleScope.launch {
            foodRepository.initializeFoodDatabase()
        }

        setContent {
            TermprojectTheme {
                MainScreen()
            }
        }
    }
}
