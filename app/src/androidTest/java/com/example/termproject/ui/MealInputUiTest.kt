package com.example.termproject.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.termproject.ui.screens.MealInputScreen
import com.example.termproject.ui.theme.TermprojectTheme
import com.example.termproject.viewmodel.MealInputViewModel
import org.junit.Rule
import org.junit.Test

/**
 * [Compose UI Test 1: 음식 및 식사 입력 화면 UI 테스트]
 */
class MealInputUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyMealInputScreenUIElements() {
        val fakeMealRepository = FakeMealRepository()
        val viewModel = MealInputViewModel(fakeMealRepository)

        // 화면 렌더링
        composeTestRule.setContent {
            TermprojectTheme {
                val navController = rememberNavController()
                MealInputScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // 1. 화면 헤더 표시 검증
        composeTestRule.onNodeWithText("식사 기록 등록").assertIsDisplayed()

        // 2. 식사 날짜 및 시간 영역 표시 검증
        composeTestRule.onNodeWithText("식사 날짜 및 시간").assertIsDisplayed()

        // 3. 식사 구분 선택 영역 및 칩 표시 검증 (조식, 중식, 석식, 간식)
        composeTestRule.onNodeWithText("식사 구분").assertIsDisplayed()
        composeTestRule.onNodeWithText("조식").assertIsDisplayed()
        composeTestRule.onNodeWithText("중식").assertIsDisplayed()

        // 4. 음식 추가 버튼 및 최하단 식사 저장하기 버튼 표시 검증
        composeTestRule.onNodeWithText("+ 음식 추가").assertIsDisplayed()
        composeTestRule.onNodeWithText("식사 저장하기").assertIsDisplayed()
    }
}
