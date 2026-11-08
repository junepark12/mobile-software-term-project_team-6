package com.example.termproject.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.termproject.domain.model.MealNutrientSummary
import com.example.termproject.domain.model.MealRecord
import com.example.termproject.domain.model.MealType
import com.example.termproject.domain.model.SingleNutrientResult
import com.example.termproject.ui.screens.MealListScreen
import com.example.termproject.ui.theme.TermprojectTheme
import com.example.termproject.viewmodel.MealListViewModel
import org.junit.Rule
import org.junit.Test

/**
 * [Compose UI Test 2: 식사 목록 조회 화면 UI 테스트]
 */
class MealListUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyMealListEmptyStateUI() {
        // 기록이 없을 때 빈 상태 안내 문구 표시 검증
        val emptyRepository = FakeMealRepository(emptyList())
        val viewModel = MealListViewModel(emptyRepository)

        composeTestRule.setContent {
            TermprojectTheme {
                val navController = rememberNavController()
                MealListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // 목록 화면 제목 및 빈 상태 안내 문구 표시 검증
        composeTestRule.onNodeWithText("식사 기록 목록").assertIsDisplayed()
        composeTestRule.onNodeWithText("등록된 식사 기록이 없습니다.").assertIsDisplayed()
        composeTestRule.onNodeWithText("+ 새 식사 등록").assertIsDisplayed()
    }

    @Test
    fun verifyMealListWithRecordedDataUI() {
        // 저장된 식사 데이터가 있을 때 목록에 식사 정보 표시 검증
        val item = SingleNutrientResult("제육볶음", 150.0, 9.75, 21.0, 18.0, 285.0)
        val sampleMeal = MealRecord(
            id = 10,
            date = "2026-11-05",
            time = "12:30",
            mealType = MealType.LUNCH,
            imageUri = null,
            items = listOf(item),
            summary = MealNutrientSummary(150.0, 9.75, 21.0, 18.0, 285.0, listOf(item))
        )

        val repositoryWithData = FakeMealRepository(listOf(sampleMeal))
        val viewModel = MealListViewModel(repositoryWithData)

        composeTestRule.setContent {
            TermprojectTheme {
                val navController = rememberNavController()
                MealListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // 저장된 날짜 그룹 및 음식명/칼로리 표시 검증
        composeTestRule.onNodeWithText("식사 기록 목록").assertIsDisplayed()
        composeTestRule.onNodeWithText("제육볶음").assertIsDisplayed()
        composeTestRule.onNodeWithText("285.0 kcal").assertIsDisplayed()
    }
}
