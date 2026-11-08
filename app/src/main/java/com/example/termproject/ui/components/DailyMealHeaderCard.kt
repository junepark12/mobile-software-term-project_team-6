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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.termproject.domain.model.DailySummary
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.CarbohydrateColor
import com.example.termproject.ui.theme.FatColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.ProteinColor
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary

/**
 * 날짜별 그룹 헤더 카드 (날짜명, 일일 총 칼로리, 탄/단/지 합계 및 끼니 수)
 */
@Composable
fun DailyMealHeaderCard(
    dailySummary: DailySummary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryGreen.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dailySummary.dateDisplay,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "총 ${dailySummary.mealCount}회 식사 기록",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "일일 총 ${dailySummary.roundedTotalCaloriesKcal.formatWithUnit(" kcal", 1)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CaloriesColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 일일 총 탄수화물, 단백질, 지방 합계
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                NutrientBadge(
                    label = "탄",
                    value = dailySummary.roundedTotalCarbohydrateG.formatWithUnit("g", 1),
                    color = CarbohydrateColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "단",
                    value = dailySummary.roundedTotalProteinG.formatWithUnit("g", 1),
                    color = ProteinColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "지",
                    value = dailySummary.roundedTotalFatG.formatWithUnit("g", 1),
                    color = FatColor
                )
            }
        }
    }
}
