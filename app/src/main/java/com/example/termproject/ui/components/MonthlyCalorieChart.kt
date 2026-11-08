package com.example.termproject.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.termproject.domain.model.DailyCaloriePoint
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary

/**
 * 최근 30일간의 날짜별 칼로리 추이 막대 차트 (Compose Custom Canvas)
 * 100% Jetpack Compose 네이티브로 구현되어 외부 라이브러리 의존성 문제없이 고성능 렌더링
 */
@Composable
fun MonthlyCalorieChart(
    dailyPoints: List<DailyCaloriePoint>,
    modifier: Modifier = Modifier
) {
    if (dailyPoints.isEmpty()) return

    var selectedPoint by remember { mutableStateOf<DailyCaloriePoint?>(null) }
    val maxCalories = dailyPoints.maxOfOrNull { it.caloriesKcal }?.coerceAtLeast(1000.0) ?: 2000.0

    AppCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "최근 30일 칼로리 추이",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = selectedPoint?.let { "${it.dateShortDisplay}: ${it.caloriesKcal.formatWithUnit(" kcal", 1)}" }
                        ?: "막대를 터치하여 일별 확인",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedPoint != null) CaloriesColor else TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas 막대 차트
            val activeColor = PrimaryGreen
            val emptyBarColor = Color(0xFFE0E0E0)
            val selectedBarColor = CaloriesColor

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(dailyPoints) {
                            detectTapGestures { tapOffset ->
                                val barWidthTotal = size.width / dailyPoints.size
                                val index = (tapOffset.x / barWidthTotal).toInt().coerceIn(0, dailyPoints.size - 1)
                                selectedPoint = dailyPoints[index]
                            }
                        }
                ) {
                    val totalWidth = size.width
                    val chartHeight = size.height - 24.dp.toPx() // x축 라벨 공간 제외
                    val barCount = dailyPoints.size
                    val totalBarSpace = totalWidth / barCount
                    val barWidth = (totalBarSpace * 0.65f).coerceAtLeast(2.dp.toPx())

                    dailyPoints.forEachIndexed { index, point ->
                        val barHeight = ((point.caloriesKcal / maxCalories) * chartHeight).toFloat().coerceAtLeast(4.dp.toPx())
                        val xOffset = (index * totalBarSpace) + (totalBarSpace - barWidth) / 2f
                        val yOffset = chartHeight - barHeight

                        val isSelected = selectedPoint?.dateISO == point.dateISO
                        val barColor = when {
                            isSelected -> selectedBarColor
                            point.caloriesKcal > 0.0 -> activeColor
                            else -> emptyBarColor
                        }

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(xOffset, yOffset),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // x축 날짜 라벨 (인덱스 0, 7, 14, 21, 29 간격 표시)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labelIndices = listOf(0, 7, 14, 21, 29).filter { it in dailyPoints.indices }
                labelIndices.forEach { idx ->
                    Text(
                        text = dailyPoints[idx].dateShortDisplay,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
