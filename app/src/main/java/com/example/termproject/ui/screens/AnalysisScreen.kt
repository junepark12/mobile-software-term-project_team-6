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
import com.example.termproject.ui.components.MealNutrientSummaryCard
import com.example.termproject.ui.components.MealTypeDonutChart
import com.example.termproject.ui.components.MealTypeStatCard
import com.example.termproject.ui.components.MonthlyCalorieChart
import com.example.termproject.ui.theme.AppBackground
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary
import com.example.termproject.viewmodel.AnalysisViewModel

/**
 * 최근 1개월 식사 종합 분석 화면 (AnalysisScreen)
 * 1. 상단: 최근 1개월 분석 제목 및 날짜 범위
 * 2. 칼로리 분석: 총 섭취 칼로리, 하루 평균(30일 기준), 날짜별 추이 막대 그래프 (0 kcal 처리)
 * 3. 영양소 분석: 1개월 총 탄수화물·단백질·지방 섭취량 및 비율 프로그레스 바
 * 4. 끼니별 분석: 조식·중식·석식·간식/음료 비율 도넛 차트 및 상세 카드
 */
@Composable
fun AnalysisScreen(
    navController: NavController,
    viewModel: AnalysisViewModel = run {
        val context = LocalContext.current
        val database = AppDatabase.getDatabase(context)
        val mealRepository = MealRepositoryImpl(database.mealDao())
        viewModel(factory = AnalysisViewModel.Factory(mealRepository))
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // 로딩 상태 구분
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryGreen
            )
        } else {
            val analysis = uiState.monthlyAnalysis

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
                    // 1. 상단: 식사 분석 제목 및 최근 1개월 기간
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "최근 1개월 식사 분석",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (analysis != null) {
                                Text(
                                    text = "분석 기간: ${analysis.startDateISO} ~ ${analysis.endDateISO} (30일간)",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // 데이터 없음 / 기록 없음 안내 상태 구분
                    if (analysis == null || !analysis.hasData) {
                        item {
                            Spacer(modifier = Modifier.height(30.dp))
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
                                        text = "최근 1개월 동안 등록된 식사 기록이 없습니다.",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "식사를 기록하여 월간 칼로리 추이와\n탄·단·지 영양소 및 끼니별 통계를 확인해보세요!",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    } else {
                        // 2. 칼로리 분석: 총 섭취 칼로리, 하루 평균 칼로리 카드
                        item {
                            AppCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "1개월 총 섭취 칼로리",
                                            fontSize = 13.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = analysis.roundedTotalCaloriesKcal.formatWithUnit(" kcal", 1),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CaloriesColor
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "하루 평균 칼로리 (30일)",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = analysis.roundedDailyAverageCaloriesKcal.formatWithUnit(" kcal", 1),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "기록일수 ${analysis.activeDaysCount}일 / 30일",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // 2-2. 칼로리 분석: 날짜별 칼로리 추이 막대 그래프 (기록 없는 날은 0 kcal 표시)
                        item {
                            MonthlyCalorieChart(dailyPoints = analysis.dailyPoints)
                        }

                        // 3. 영양소 분석: 탄수화물·단백질·지방 1개월 총량 및 비율 카드
                        item {
                            Text(
                                text = "1개월 총 영양소 분석",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        item {
                            MealNutrientSummaryCard(summary = analysis.totalSummary)
                        }

                        // 4. 끼니별 분석: 조식·중식·석식·간식/음료 비율 도넛 차트
                        item {
                            Text(
                                text = "끼니별 분석 (조식·중식·석식·간식/음료)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        item {
                            MealTypeDonutChart(mealTypeStats = analysis.mealTypeStats)
                        }

                        // 4-2. 끼니별 분석 상세 카드 목록
                        items(analysis.mealTypeStats) { stat ->
                            MealTypeStatCard(stat = stat)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
