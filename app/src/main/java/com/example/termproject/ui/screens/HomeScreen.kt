package com.example.termproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.components.AppCard
import com.example.termproject.ui.components.AppPrimaryButton
import com.example.termproject.ui.components.MealNutrientSummaryCard
import com.example.termproject.ui.components.MealRecordCard
import com.example.termproject.ui.navigation.Screen
import com.example.termproject.ui.theme.AppBackground
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary
import com.example.termproject.viewmodel.HomeViewModel

/**
 * 메인 홈 화면 (HomeScreen)
 * - 오늘 날짜 & 앱 이름
 * - 오늘 총 섭취 칼로리 및 끼니 수
 * - 탄수화물, 단백질, 지방 영양소 카드 및 비율 바
 * - 오늘 입력한 최근 식사 기록 목록 (비어있는 경우 0/빈 상태 표시)
 * - 식사 추가 버튼 (음식 입력 화면으로 이동)
 */
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = run {
        val context = LocalContext.current
        val database = AppDatabase.getDatabase(context)
        val mealRepository = MealRepositoryImpl(database.mealDao())
        viewModel(factory = HomeViewModel.Factory(mealRepository))
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. 상단: 앱 이름 및 오늘 날짜
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "학생 식당 식단 관리",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.todayFormattedDate,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }

                    // 2. 중앙: 오늘 총 섭취 칼로리 & 끼니 수 카드
                    item {
                        AppCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "오늘 총 섭취 칼로리",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = uiState.todayCalories.formatWithUnit(" kcal", 1),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CaloriesColor
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "오늘의 끼니 수",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${uiState.todayMealCount}회",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                            }
                        }
                    }

                    // 3. 중간: 영양소 카드 (탄/단/지 요약 & 비율 바)
                    item {
                        MealNutrientSummaryCard(summary = uiState.todayNutrients)
                    }

                    // 4. 하단: 최근 식사 기록 헤더
                    item {
                        Text(
                            text = "오늘의 식사 기록",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // 5. 최근 식사 목록 (없으면 빈 상태 카드)
                    if (uiState.recentMeals.isEmpty()) {
                        item {
                            AppCard(
                                backgroundColor = PrimaryGreen.copy(alpha = 0.05f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "오늘 등록된 식사 기록이 없습니다.",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "아래 [+ 식사 추가] 버튼을 눌러 식단을 기록해보세요!",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.recentMeals) { mealRecord ->
                            MealRecordCard(
                                mealRecord = mealRecord,
                                onClick = {
                                    navController.navigate(Screen.MealDetail.createRoute(mealRecord.id))
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // 6. 최하단: 식사 추가 버튼 (음식 입력 화면으로 이동)
                AppPrimaryButton(
                    text = "+ 식사 추가",
                    onClick = {
                        navController.navigate(Screen.MealInput.route)
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}
