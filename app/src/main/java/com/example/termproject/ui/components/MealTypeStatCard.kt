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
import com.example.termproject.domain.model.MealTypeNutrition
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.CarbohydrateColor
import com.example.termproject.ui.theme.FatColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.ProteinColor
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary

/**
 * 식사 구분별(조식·중식·석식·간식/음료) 영양 분석 통계 카드
 */
@Composable
fun MealTypeStatCard(
    stat: MealTypeNutrition,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stat.mealType.displayName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "총 ${stat.mealCount}회 기록",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = stat.roundedTotalCaloriesKcal.formatWithUnit(" kcal", 1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CaloriesColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                NutrientBadge(
                    label = "탄",
                    value = stat.roundedTotalCarbsG.formatWithUnit("g", 1),
                    color = CarbohydrateColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "단",
                    value = stat.roundedTotalProteinG.formatWithUnit("g", 1),
                    color = ProteinColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "지",
                    value = stat.roundedTotalFatG.formatWithUnit("g", 1),
                    color = FatColor
                )
            }
        }
    }
}
