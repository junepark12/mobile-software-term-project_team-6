package com.example.termproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.termproject.data.local.AppDatabase
import com.example.termproject.data.repository.FoodRepositoryImpl
import com.example.termproject.domain.model.FoodItem
import com.example.termproject.ui.components.AppCard
import com.example.termproject.ui.components.AppTextField
import com.example.termproject.ui.components.FoodSearchResultCard
import com.example.termproject.ui.theme.AppBackground
import com.example.termproject.ui.theme.PrimaryGreen
import com.example.termproject.ui.theme.TextPrimary
import com.example.termproject.ui.theme.TextSecondary
import com.example.termproject.viewmodel.FoodSearchViewModel

private val categories = listOf(
    "밥류", "국·찌개류", "고기류", "생선류", "채소류",
    "반찬류", "면류", "과일류", "간식류", "음료류"
)

/**
 * 음식 검색 및 선택 화면 (FoodSearchScreen)
 */
@Composable
fun FoodSearchScreen(
    navController: NavController,
    onSelectFood: ((FoodItem) -> Unit)? = null,
    viewModel: FoodSearchViewModel = run {
        val context = LocalContext.current
        val database = AppDatabase.getDatabase(context)
        val foodRepository = FoodRepositoryImpl(context, database.foodDao())
        viewModel(factory = FoodSearchViewModel.Factory(foodRepository))
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "음식 검색",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 1. 검색창
        AppTextField(
            value = uiState.query,
            onValueChange = { viewModel.onQueryChange(it) },
            label = "음식명 검색",
            placeholder = "예: 쌀밥, 제육볶음, 된장찌개",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색 아이콘"
                )
            },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "지우기"
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. 카테고리 필터 칩 스크롤
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { category ->
                val isSelected = uiState.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onCategorySelect(category) },
                    label = { Text(text = category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. 검색 중 로딩 상태
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (uiState.searchResults.isEmpty()) {
            // 4. 검색 결과가 없는 경우 안내 표시
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AppCard(
                    backgroundColor = PrimaryGreen.copy(alpha = 0.05f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "검색된 음식 정보가 없습니다.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "다른 키워드나 카테고리를 선택해보세요.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            // 5. 검색 결과 리스트
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.searchResults) { foodItem ->
                    FoodSearchResultCard(
                        foodItem = foodItem,
                        onSelectFood = { item ->
                            if (onSelectFood != null) {
                                onSelectFood(item)
                            } else {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedFoodCode", item.foodCode)
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
        }
    }
}
