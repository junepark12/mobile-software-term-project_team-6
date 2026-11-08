package com.example.termproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 앱 내 모든 화면의 Route 및 하단 탭 아이템 정의
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // 하단 Navigation Bar 주요 탭
    object Home : Screen("home", "홈", Icons.Default.Home)
    object MealInput : Screen("meal_input", "식사 입력", Icons.Default.AddCircle)
    object MealList : Screen("meal_list", "식사 조회", Icons.Default.List)
    object Analysis : Screen("analysis", "식사 분석", Icons.Default.Analytics)
    object Info : Screen("info", "출처/정보", Icons.Default.Info)

    // 서브 화면 (인자 전달 및 검색)
    object MealDetail : Screen("meal_detail/{mealId}", "식사 상세") {
        fun createRoute(mealId: Long): String = "meal_detail/$mealId"
    }
    object FoodSearch : Screen("food_search", "음식 검색")

    companion object {
        val bottomNavItems = listOf(Home, MealInput, MealList, Analysis, Info)
    }
}
