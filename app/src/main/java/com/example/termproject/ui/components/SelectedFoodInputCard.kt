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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.termproject.domain.model.formatWithUnit
import com.example.termproject.ui.theme.CaloriesColor
import com.example.termproject.ui.theme.CarbohydrateColor
import com.example.termproject.ui.theme.ErrorRed
import com.example.termproject.ui.theme.FatColor
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.ProteinColor
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary
import com.example.termproject.viewmodel.SelectedFoodState

/**
 * 선택한 음식의 섭취량(g) 입력 및 영양소 실시간 미리보기 카드 컴포넌트
 */
@Composable
fun SelectedFoodInputCard(
    selectedFoodState: SelectedFoodState,
    onQuantityChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val food = selectedFoodState.foodItem
    val nutrient = selectedFoodState.calculatedNutrient

    AppCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. 헤더: 카테고리 태그, 음식명, 삭제 버튼
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
                            text = food.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = food.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "음식 삭제",
                        tint = ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. 섭취량(g) 입력 텍스트 필드
            AppTextField(
                value = selectedFoodState.quantityGramInput,
                onValueChange = onQuantityChange,
                label = "섭취량(g)",
                placeholder = "예: 150",
                isError = !selectedFoodState.isQuantityValid,
                errorMessage = selectedFoodState.errorMessage,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Text(
                        text = "g",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. 영양소 실시간 미리보기 (또는 미검증 식사 안내)
            if (!food.isVerified) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ErrorRed.copy(alpha = 0.1f))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️ 영양 정보 확인 불가 (공공 DB 미검증 항목)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                }
            } else if (nutrient != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        NutrientBadge(
                            label = "탄",
                            value = nutrient.roundedCarbohydrateG.formatWithUnit("g", 1),
                            color = CarbohydrateColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        NutrientBadge(
                            label = "단",
                            value = nutrient.roundedProteinG.formatWithUnit("g", 1),
                            color = ProteinColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        NutrientBadge(
                            label = "지",
                            value = nutrient.roundedFatG.formatWithUnit("g", 1),
                            color = FatColor
                        )
                    }

                    Text(
                        text = nutrient.roundedCaloriesKcal.formatWithUnit(" kcal", 1),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CaloriesColor
                    )
                }
            }
        }
    }
}
