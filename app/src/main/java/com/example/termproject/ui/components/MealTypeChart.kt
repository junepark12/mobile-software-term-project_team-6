package com.example.termproject.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.termproject.domain.model.MealType
import com.example.termproject.domain.model.MealTypeNutrition
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary

val BreakfastColor = Color(0xFFFFB74D) // Amber
val LunchColor = Color(0xFF2E7D32)     // Green
val DinnerColor = Color(0xFF00695C)    // Teal
val SnackColor = Color(0xFF8E24AA)     // Purple

fun getMealTypeColor(type: MealType): Color {
    return when (type) {
        MealType.BREAKFAST -> BreakfastColor
        MealType.LUNCH -> LunchColor
        MealType.DINNER -> DinnerColor
        MealType.SNACK -> SnackColor
    }
}

/**
 * 끼니 종류별 칼로리 비율 도넛 차트 (Compose Canvas Donut Chart)
 */
@Composable
fun MealTypeDonutChart(
    mealTypeStats: List<MealTypeNutrition>,
    modifier: Modifier = Modifier
) {
    val totalCalories = mealTypeStats.sumOf { it.totalCaloriesKcal }.coerceAtLeast(1.0)

    AppCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "끼니별 칼로리 비율 (조식·중식·석식·간식)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. 도넛 차트
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val strokeWidth = 24.dp.toPx()
                        var startAngle = -90f

                        if (totalCalories <= 1.0) {
                            drawArc(
                                color = Color(0xFFE0E0E0),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth)
                            )
                        } else {
                            mealTypeStats.forEach { stat ->
                                val sweepAngle = ((stat.totalCaloriesKcal / totalCalories) * 360f).toFloat()
                                if (sweepAngle > 0f) {
                                    drawArc(
                                        color = getMealTypeColor(stat.mealType),
                                        startAngle = startAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                    )
                                    startAngle += sweepAngle
                                }
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "총 칼로리",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = totalCalories.coerceAtLeast(0.0).formatWithUnit("kcal", 0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CaloriesColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 2. 범례 (Legend)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mealTypeStats.forEach { stat ->
                        val color = getMealTypeColor(stat.mealType)
                        val percentage = if (totalCalories > 1.0) (stat.totalCaloriesKcal / totalCalories) * 100.0 else 0.0

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stat.mealType.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "${percentage.formatWithUnit("%", 0)} (${stat.roundedTotalCaloriesKcal.formatWithUnit("kcal", 0)})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stat.totalCaloriesKcal > 0) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
