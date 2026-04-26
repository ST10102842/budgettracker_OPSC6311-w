package com.example.budgettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.model.Badge
import com.example.budgettracker.data.model.Expense
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GamificationViewModel : ViewModel() {

    private val _badges = MutableStateFlow<List<Badge>>(emptyList())
    val badges: StateFlow<List<Badge>> = _badges.asStateFlow()

    private val _streakDays = MutableStateFlow(0)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val allPossibleBadges = listOf(
        Badge("first_expense",  "First Step",     "Logged your first expense",         "🎯"),
        Badge("ten_expenses",   "Getting Started","Logged 10 expenses",                "📊"),
        Badge("hundred_rands",  "Century",        "Tracked over R100 in expenses",     "💯"),
        Badge("five_categories","Diversified",    "Used 5 different categories",       "🌈"),
        Badge("seven_streak",   "Week Warrior",   "Logged expenses 7 days in a row",   "🔥"),
        Badge("under_budget",   "Budget Hero",    "Stayed under budget for a month",   "🏆"),
        Badge("thirty_expenses","Committed",      "Logged 30 expenses",                "💪"),
        Badge("saver",          "Saver",          "Reduced spending month over month", "💰")
    )

    fun evaluateBadges(expenses: List<Expense>) {
        viewModelScope.launch {
            val unlockedIds = mutableSetOf<String>()

            if (expenses.isNotEmpty()) unlockedIds.add("first_expense")
            if (expenses.size >= 10) unlockedIds.add("ten_expenses")
            if (expenses.size >= 30) unlockedIds.add("thirty_expenses")
            if (expenses.sumOf { it.amount } >= 100) unlockedIds.add("hundred_rands")

            val uniqueCategories = expenses.map { it.categoryId }.toSet()
            if (uniqueCategories.size >= 5) unlockedIds.add("five_categories")

            // Calculate streak
            val streak = calculateStreak(expenses)
            _streakDays.value = streak
            if (streak >= 7) unlockedIds.add("seven_streak")

            _badges.value = allPossibleBadges.map { badge ->
                badge.copy(isUnlocked = badge.id in unlockedIds)
            }
        }
    }

    private fun calculateStreak(expenses: List<Expense>): Int {
        if (expenses.isEmpty()) return 0
        val uniqueDays = expenses.map { it.date }.toSortedSet(reverseOrder())
        var streak = 0
        var currentDate = java.time.LocalDate.now()
        for (date in uniqueDays) {
            if (date == currentDate || date == currentDate.minusDays(1)) {
                streak++
                currentDate = date
            } else break
        }
        return streak
    }
}