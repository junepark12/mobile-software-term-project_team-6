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
import com.example.termproject.domain.model.FoodItem
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.CarbohydrateColor
import com.example.termproject.ui.theme.FatColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.ProteinColor
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary

/**
 * 음식 검색 결과 카드 컴포넌트
 * 100g당 탄수화물, 단백질, 지방 및 칼로리와 데이터 출처 표시
 */
@Composable
fun FoodSearchResultCard(
    foodItem: FoodItem,
    onSelectFood: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        onClick = { onSelectFood(foodItem) }
    ) {
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
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = foodItem.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = foodItem.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = foodItem.caloriePer100g.formatWithUnit(" kcal/100g", 1),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CaloriesColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 100g 당 영양성분 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                NutrientBadge(
                    label = "탄",
                    value = foodItem.carbohydratePer100g.formatWithUnit("g", 1),
                    color = CarbohydrateColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "단",
                    value = foodItem.proteinPer100g.formatWithUnit("g", 1),
                    color = ProteinColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                NutrientBadge(
                    label = "지",
                    value = foodItem.fatPer100g.formatWithUnit("g", 1),
                    color = FatColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "출처: ${foodItem.dataSource}",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}
