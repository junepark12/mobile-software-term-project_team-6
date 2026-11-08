package com.example.termproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.termproject.domain.model.MealNutrientSummary
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.CarbohydrateColor
import com.example.termproject.ui.theme.FatColor
import com.example.termproject.ui.theme.ProteinColor
import com.example.termproject.ui.theme.TextPrimary

/**
 * 개별 영양소 태그/배지 표시 컴포넌트
 */
@Composable
fun NutrientBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

/**
 * 식사 영양소 요약 카드 (총 칼로리 + 탄/단/지 프로그레스 바)
 */
@Composable
fun MealNutrientSummaryCard(
    summary: MealNutrientSummary,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "총 섭취 열량",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = summary.roundedTotalCaloriesKcal.formatWithUnit(" kcal", 1),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CaloriesColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 탄/단/지 요약 태그
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientBadge(
                    label = "탄수화물",
                    value = summary.roundedTotalCarbohydrateG.formatWithUnit("g", 1),
                    color = CarbohydrateColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "단백질",
                    value = summary.roundedTotalProteinG.formatWithUnit("g", 1),
                    color = ProteinColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "지방",
                    value = summary.roundedTotalFatG.formatWithUnit("g", 1),
                    color = FatColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 영양소 비율 바 시각화
            NutrientRatioBar(
                carbsG = summary.totalCarbohydrateG,
                proteinG = summary.totalProteinG,
                fatG = summary.totalFatG
            )
        }
    }
}

/**
 * 탄수화물/단백질/지방 비율 시각화 프로그레스 바
 */
@Composable
fun NutrientRatioBar(
    carbsG: Double,
    proteinG: Double,
    fatG: Double,
    modifier: Modifier = Modifier
) {
    val totalG = (carbsG + proteinG + fatG).coerceAtLeast(1.0)
    val carbsRatio = (carbsG / totalG).toFloat()
    val proteinRatio = (proteinG / totalG).toFloat()
    val fatRatio = (fatG / totalG).toFloat()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            if (carbsRatio > 0f) {
                Box(
                    modifier = Modifier
                        .weight(carbsRatio)
                        .height(10.dp)
                        .background(CarbohydrateColor)
                )
            }
            if (proteinRatio > 0f) {
                Box(
                    modifier = Modifier
                        .weight(proteinRatio)
                        .height(10.dp)
                        .background(ProteinColor)
                )
            }
            if (fatRatio > 0f) {
                Box(
                    modifier = Modifier
                        .weight(fatRatio)
                        .height(10.dp)
                        .background(FatColor)
                )
            }
        }
    }
}
