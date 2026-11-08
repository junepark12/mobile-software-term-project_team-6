package com.example.termproject.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.termproject.domain.model.SingleNutrientResult
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.CarbohydrateColor
import com.example.termproject.ui.theme.FatColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.ProteinColor
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary

/**
 * 식사 상세 화면에서 개별 음식/반찬 항목 영양소 분해 카드 컴포넌트
 */
@Composable
fun MealDetailFoodItemCard(
    itemResult: SingleNutrientResult,
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
                    text = itemResult.foodName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "섭취량: ${itemResult.quantityGram.formatWithUnit("g", 1)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    NutrientBadge(
                        label = "탄",
                        value = itemResult.roundedCarbohydrateG.formatWithUnit("g", 1),
                        color = CarbohydrateColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    NutrientBadge(
                        label = "단",
                        value = itemResult.roundedProteinG.formatWithUnit("g", 1),
                        color = ProteinColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    NutrientBadge(
                        label = "지",
                        value = itemResult.roundedFatG.formatWithUnit("g", 1),
                        color = FatColor
                    )
                }

                Text(
                    text = itemResult.roundedCaloriesKcal.formatWithUnit(" kcal", 1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CaloriesColor
                )
            }
        }
    }
}
