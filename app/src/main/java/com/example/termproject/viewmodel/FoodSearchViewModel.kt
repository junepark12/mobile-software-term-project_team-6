package com.example.termproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.termproject.data.repository.FoodRepository
import com.example.termproject.domain.model.FoodItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * 음식 검색 UI State 정의
 */
data class FoodSearchUiState(
    val query: String = "",
    val searchResults: List<FoodItem> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false
)

/**
 * 음식 검색 ViewModel
 */
class FoodSearchViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FoodSearchUiState> = combine(_query, _selectedCategory) { q, cat ->
        q to cat
    }.flatMapLatest { (q, cat) ->
        if (q.isBlank()) {
            if (cat != null) foodRepository.getFoodsByCategory(cat)
            else foodRepository.getAllFoods()
        } else {
            foodRepository.searchFoods(q)
        }
    }.map { list ->
        FoodSearchUiState(
            query = _query.value,
            searchResults = list,
            selectedCategory = _selectedCategory.value,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FoodSearchUiState(isLoading = true)
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    class Factory(private val foodRepository: FoodRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodSearchViewModel(foodRepository) as T
        }
    }
}
