package com.example.termproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.termproject.ui.components.AppCard
import com.example.termproject.ui.theme.AppBackground
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary

/**
 * 앱 정보 및 식품 데이터 출처 표기 화면 (InfoScreen)
 */
@Composable
fun InfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 헤더 타이틀
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "앱 정보 및 공공데이터 출처",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2026 모바일 소프트웨어 학기말 프로젝트",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 2. 공공데이터 출처 명시 카드
            item {
                AppCard(
                    backgroundColor = PrimaryGreen.copy(alpha = 0.05f)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "🏛️ 공공데이터 출처 명시",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "본 앱에서 제공하는 약 100여 개의 음식 영양성분 정보는 대한민국 공공데이터포털(data.go.kr) 및 식품의약품안전처의 오픈 API 데이터를 정제하여 활용하였습니다.",
                            fontSize = 14.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "제공 기관", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "식품의약품안전처", fontSize = 13.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "데이터베이스명", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "식품 영양성분 DB (I2790 / C003)", fontSize = 13.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "기준 중량 및 열량", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "100g 당 kcal / g", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // 3. 앱 주요 기능 및 스택 요약 카드
            item {
                AppCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "📱 주요 기능 및 개발 정보",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "• 100개 공공 식품 DB 기반 실시간 영양소 계산\n" +
                                    "• 식사 사진, 일시, 끼니별(조식/중식/석식/간식) 등록\n" +
                                    "• 최근 1개월(30일) 칼로리 추이 막대 그래프 및 도넛 차트\n" +
                                    "• Room Database 오프라인 로컬 저장 지원\n" +
                                    "• 독립적인 도메인 계산기 기반 단위 테스트 17개 통과",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 4. 기술 스택 카드
            item {
                AppCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "🛠️ 기술 스택 (Tech Stack)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "• Language: Kotlin 2.2.10\n" +
                                    "• UI Framework: Jetpack Compose (Material3)\n" +
                                    "• Architecture: MVVM & Repository Pattern\n" +
                                    "• Database: Room DB (KSP)\n" +
                                    "• Navigation: Navigation Compose\n" +
                                    "• Image Loading: Coil Compose\n" +
                                    "• Testing: JUnit4, AndroidX Compose UI Testing",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
