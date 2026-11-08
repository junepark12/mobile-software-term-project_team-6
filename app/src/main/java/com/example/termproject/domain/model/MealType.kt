package com.example.termproject.domain.model

/**
 * 식사 구분 Enum (조식, 중식, 석식, 간식/음료)
 */
enum class MealType(val displayName: String) {
    BREAKFAST("조식"),
    LUNCH("중식"),
    DINNER("석식"),
    SNACK("간식·음료");

    companion object {
        fun fromString(value: String): MealType {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName == value }
                ?: BREAKFAST
        }
    }
}
