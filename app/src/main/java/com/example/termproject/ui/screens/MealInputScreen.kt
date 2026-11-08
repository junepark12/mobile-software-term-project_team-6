package com.example.termproject.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.termproject.data.local.AppDatabase
import com.example.termproject.data.repository.MealRepositoryImpl
import com.example.termproject.domain.model.MealType
import com.example.termproject.ui.components.AppCard
import com.example.termproject.ui.components.AppOutlinedButton
import com.example.termproject.ui.components.AppPrimaryButton
import com.example.termproject.ui.components.AppTextField
import com.example.termproject.ui.components.MealNutrientSummaryCard
import com.example.termproject.ui.components.SelectedFoodInputCard
import com.example.termproject.ui.theme.AppBackground
import com.example.termproject.ui.theme.ErrorRed
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary
import com.example.termproject.viewmodel.MealInputViewModel
import java.util.Calendar
import java.util.Locale

/**
 * 식사 입력 및 영양소 실시간 미리보기 화면 (MealInputScreen)
 * - Android Photo Picker / GetContent 호환 사진 등록 & 지속적 URI 권한 유지
 * - DatePickerDialog 및 TimePickerDialog를 통한 일시 선택
 * - 조식/중식/석식/간식·음료 식사 구분 선택
 * - 섭취량(g) 조절 및 영양소 실시간 계산
 */
@Composable
fun MealInputScreen(
    navController: NavController,
    viewModel: MealInputViewModel = run {
        val context = LocalContext.current
        val database = AppDatabase.getDatabase(context)
        val mealRepository = MealRepositoryImpl(database.mealDao())
        viewModel(factory = MealInputViewModel.Factory(mealRepository))
    }
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showFoodSearchDialog by remember { mutableStateOf(false) }

    // DatePickerDialog 피커 팝업
    val openDatePicker = {
        val calendar = Calendar.getInstance()
        val dateParts = uiState.selectedDate.split("-")
        val initialYear = if (dateParts.size == 3) dateParts[0].toIntOrNull() ?: calendar.get(Calendar.YEAR) else calendar.get(Calendar.YEAR)
        val initialMonth = if (dateParts.size == 3) (dateParts[1].toIntOrNull() ?: (calendar.get(Calendar.MONTH) + 1)) - 1 else calendar.get(Calendar.MONTH)
        val initialDay = if (dateParts.size == 3) dateParts[2].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH) else calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                viewModel.onDateChange(formattedDate)
            },
            initialYear,
            initialMonth,
            initialDay
        ).show()
    }

    // TimePickerDialog 피커 팝업
    val openTimePicker = {
        val calendar = Calendar.getInstance()
        val timeParts = uiState.selectedTime.split(":")
        val initialHour = if (timeParts.size == 2) timeParts[0].toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY) else calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = if (timeParts.size == 2) timeParts[1].toIntOrNull() ?: calendar.get(Calendar.MINUTE) else calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                viewModel.onTimeChange(formattedTime)
            },
            initialHour,
            initialMinute,
            true
        ).show()
    }

    // Android Photo Picker (Android 13+ / API 33+) 런처
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoUriChange(context, it.toString()) }
    }

    // 구버전 Android 호환 갤러리 런처
    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoUriChange(context, it.toString()) }
    }

    val openPhotoPicker = {
        if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            legacyGalleryLauncher.launch("image/*")
        }
    }

    // 저장 성공 시 이전 화면으로 복귀
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    Text(
                        text = "식사 기록 등록",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // 2. 날짜 및 시간 선택 카드 (DatePickerDialog / TimePickerDialog 연동)
                item {
                    AppCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "식사 날짜 및 시간",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { openDatePicker() }
                                ) {
                                    AppTextField(
                                        value = uiState.selectedDate,
                                        onValueChange = {},
                                        label = "날짜 선택",
                                        readOnly = true,
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "날짜 선택",
                                                tint = PrimaryGreen,
                                                modifier = Modifier.clickable { openDatePicker() }
                                            )
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { openTimePicker() }
                                ) {
                                    AppTextField(
                                        value = uiState.selectedTime,
                                        onValueChange = {},
                                        label = "시간 선택",
                                        readOnly = true,
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = "시간 선택",
                                                tint = PrimaryGreen,
                                                modifier = Modifier.clickable { openTimePicker() }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. 식사 구분 선택 (조식 / 중식 / 석식 / 간식·음료)
                item {
                    AppCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "식사 구분",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MealType.entries.forEach { mealType ->
                                    val isSelected = uiState.selectedMealType == mealType
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.onMealTypeChange(mealType) },
                                        label = { Text(text = mealType.displayName, fontSize = 13.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryGreen,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. 식사 사진 등록 (Android Photo Picker & URI 지속 권한 지원)
                item {
                    AppCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "식사 사진 (선택)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (!uiState.photoUri.isNullOrEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = uiState.photoUri,
                                        contentDescription = "식사 사진",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AppOutlinedButton(
                                        text = "사진 변경",
                                        onClick = { openPhotoPicker() },
                                        modifier = Modifier.weight(1f)
                                    )
                                    AppOutlinedButton(
                                        text = "사진 삭제",
                                        onClick = { viewModel.onPhotoUriChange(context, null) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                AppOutlinedButton(
                                    text = "📷 사진 선택하기 (Photo Picker / 갤러리)",
                                    onClick = { openPhotoPicker() }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "사진을 첨부하지 않을 경우 식사 대표 카테고리 템플릿 이미지가 사용됩니다.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 5. 선택된 음식 및 섭취량 입력 헤더 + 음식 추가 버튼
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "음식 및 섭취량(g) 목록",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        AppOutlinedButton(
                            text = "+ 음식 추가",
                            onClick = { showFoodSearchDialog = true },
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }

                // 6. 선택된 음식 항목 리스트 (섭취량 g 조절 및 영양소 실시간 미리보기)
                if (uiState.selectedFoods.isEmpty()) {
                    item {
                        AppCard(backgroundColor = PrimaryGreen.copy(alpha = 0.05f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "선택된 음식이 없습니다.",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "[+ 음식 추가] 버튼을 눌러 음식과 반찬을 선택해보세요!",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(uiState.selectedFoods) { index, foodState ->
                        SelectedFoodInputCard(
                            selectedFoodState = foodState,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateFoodQuantity(index, newQuantity)
                            },
                            onDelete = {
                                viewModel.removeFoodItem(index)
                            }
                        )
                    }
                }

                // 7. 실시간 전체 식사 영양소 요약 카드
                if (uiState.selectedFoods.isNotEmpty()) {
                    item {
                        Text(
                            text = "식사 전체 영양소 미리보기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MealNutrientSummaryCard(summary = uiState.totalMealSummary)
                    }
                }

                // 에러 메시지 표시
                if (uiState.generalErrorMessage != null) {
                    item {
                        Text(
                            text = uiState.generalErrorMessage!!,
                            color = ErrorRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // 8. 최하단 식사 저장 버튼
            AppPrimaryButton(
                text = if (uiState.isSaving) "저장 중..." else "식사 저장하기",
                onClick = { viewModel.saveMealRecord() },
                enabled = !uiState.isSaving,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // 음식 검색 대화상자 (Dialog)
        if (showFoodSearchDialog) {
            Dialog(
                onDismissRequest = { showFoodSearchDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    FoodSearchScreen(
                        navController = navController,
                        onSelectFood = { selectedFood ->
                            viewModel.addFoodItem(selectedFood)
                            showFoodSearchDialog = false
                        }
                    )
                }
            }
        }
    }
}
