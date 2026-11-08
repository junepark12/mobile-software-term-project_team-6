package com.example.termproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.termproject.ui.screens.AnalysisScreen
import com.example.termproject.ui.screens.FoodSearchScreen
import com.example.termproject.ui.screens.HomeScreen
import com.example.termproject.ui.screens.InfoScreen
import com.example.termproject.ui.screens.MealDetailScreen
import com.example.termproject.ui.screens.MealInputScreen
import com.example.termproject.ui.screens.MealListScreen

/**
 * 앱 전체 Navigation Graph 설정
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.MealInput.route) {
            MealInputScreen(navController = navController)
        }

        composable(Screen.MealList.route) {
            MealListScreen(navController = navController)
        }

        composable(
            route = Screen.MealDetail.route,
            arguments = listOf(
                navArgument("mealId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getLong("mealId") ?: -1L
            MealDetailScreen(mealId = mealId, navController = navController)
        }

        composable(Screen.Analysis.route) {
            AnalysisScreen(navController = navController)
        }

        composable(Screen.FoodSearch.route) {
            FoodSearchScreen(navController = navController)
        }

        composable(Screen.Info.route) {
            InfoScreen()
        }
    }
}
