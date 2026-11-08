package com.example.termproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.termproject.data.local.AppDatabase
import com.example.termproject.data.repository.MealRepositoryImpl
import com.example.termproject.ui.components.AppCard
import com.example.termproject.ui.components.AppPrimaryButton
import com.example.termproject.ui.components.DailyMealHeaderCard
import com.example.termproject.ui.components.MealRecordCard
import com.example.termproject.ui.navigation.Screen
import com.example.termproject.ui.theme.AppBackground
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary
import com.example.termproject.viewmodel.MealListViewModel

/**
 * 식사 목록 및 날짜별 히스토리 조회 화면 (MealListScreen)
 * - 최신 날짜순 정렬 및 날짜별 그룹화 (일일 총열량, 탄/단/지 및 끼니 수 표시)
 * - 클릭 시 mealId를 전달하여 MealDetailScreen 이동
 * - Room Flow 연동을 통해 데이터 추가/삭제 시 자동 반영
 */
@Composable
fun MealListScreen(
    navController: NavController,
    viewModel: MealListViewModel = run {
        val context = LocalContext.current
        val database = AppDatabase.getDatabase(context)
        val mealRepository = MealRepositoryImpl(database.mealDao())
        viewModel(factory = MealListViewModel.Factory(mealRepository))
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryGreen
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. 헤더 타이틀
                    item {
                        Text(
                            text = "식사 기록 목록",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // 2. 식사 기록이 없는 경우 빈 상태 표시
                    if (uiState.dailySummaries.isEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                            AppCard(
                                backgroundColor = PrimaryGreen.copy(alpha = 0.05f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "등록된 식사 기록이 없습니다.",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "아래 [+ 새 식사 등록] 버튼을 눌러\n오늘의 첫 식단을 기록해보세요!",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    } else {
                        // 3. 날짜별 그룹화된 식사 목록 리스트
                        uiState.dailySummaries.forEach { summary ->
                            // 날짜별 그룹 헤더
                            item(key = "header_${summary.dateISO}") {
                                DailyMealHeaderCard(dailySummary = summary)
                            }

                            // 해당 날짜의 식사 기록들
                            items(
                                items = summary.meals,
                                key = { meal -> "meal_${meal.id}" }
                            ) { mealRecord ->
                                MealRecordCard(
                                    mealRecord = mealRecord,
                                    onClick = {
                                        navController.navigate(Screen.MealDetail.createRoute(mealRecord.id))
                                    }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // 최하단: 새 식사 등록 버튼
                AppPrimaryButton(
                    text = "+ 새 식사 등록",
                    onClick = {
                        navController.navigate(Screen.MealInput.route)
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}
