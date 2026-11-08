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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.termproject.data.local.AppDatabase
import com.example.termproject.data.repository.MealRepositoryImpl
import com.example.termproject.ui.components.AppCard
import com.example.termproject.ui.components.AppOutlinedButton
import com.example.termproject.ui.components.AppPrimaryButton
import com.example.termproject.ui.components.MealDetailFoodItemCard
import com.example.termproject.ui.components.MealNutrientSummaryCard
import com.example.termproject.ui.theme.AppBackground
import com.example.termproject.ui.theme.ErrorRed
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary
import com.example.termproject.viewmodel.MealDetailViewModel

/**
 * 식사 상세 조회 및 삭제 화면 (MealDetailScreen)
 * - mealId 기반 식사 정보 및 연결된 모든 MealItemEntity 영양 정보 표시
 * - 식사 사진, 일시, 끼니 종류, 개별 음식별 섭취량(g) 및 탄/단/지/칼로리 breakdown
 * - 존재하지 않는 mealId 예외 처리 및 식사 삭제 확인 대화상자 지원
 */
@Composable
fun MealDetailScreen(
    mealId: Long,
    navController: NavController,
    viewModel: MealDetailViewModel = run {
        val context = LocalContext.current
        val database = AppDatabase.getDatabase(context)
        val mealRepository = MealRepositoryImpl(database.mealDao())
        viewModel(factory = MealDetailViewModel.Factory(mealId, mealRepository))
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // 삭제 완료 시 이전 화면으로 복귀
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryGreen
            )
        } else if (uiState.isNotFound || uiState.mealRecord == null) {
            // 존재하지 않는 mealId 예외 처리 화면
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppCard(
                    backgroundColor = ErrorRed.copy(alpha = 0.05f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️ 식사 기록을 찾을 수 없습니다.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "존재하지 않거나 이미 삭제된 식사 기록입니다.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                AppPrimaryButton(
                    text = "목록으로 돌아가기",
                    onClick = { navController.popBackStack() }
                )
            }
        } else {
            val record = uiState.mealRecord!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // 상단 네비게이션 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                    Text(
                        text = "식사 상세 정보",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. 식사 대표 사진 (있을 경우만 표시)
                    if (!record.imageUri.isNullOrEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                AsyncImage(
                                    model = record.imageUri,
                                    contentDescription = record.displayFoodName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    // 2. 식사 기본 정보 (일자, 시간, 끼니 종류)
                    item {
                        AppCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PrimaryGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = record.mealType.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${record.date}  |  ${record.time}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // 3. 식사 전체 영양소 요약 카드
                    item {
                        MealNutrientSummaryCard(summary = record.summary)
                    }

                    // 4. 구성 음식 및 반찬 세부 영양소 목록
                    item {
                        Text(
                            text = "구성 음식 및 반찬 상세 (${record.items.size}개)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(record.items) { itemResult ->
                        MealDetailFoodItemCard(itemResult = itemResult)
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // 하단 식사 삭제 버튼
                AppOutlinedButton(
                    text = "🗑️ 이 식사 기록 삭제하기",
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // 삭제 확인 대화상자 (Confirm Dialog)
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text(text = "식사 기록 삭제") },
                text = { Text(text = "이 식사 기록을 완전히 삭제하시겠습니까?\n삭제된 기록은 복구할 수 없습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = false
                            viewModel.deleteMealRecord()
                        }
                    ) {
                        Text(text = "삭제", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text(text = "취소")
                    }
                }
            )
        }
    }
}
