package com.example.budgettracker.viewmodel

import com.example.budgettracker.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GamificationViewModelTest {

    private lateinit var viewModel: GamificationViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GamificationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `evaluateBadges unlocks first_expense badge when there is at least one expense`() = runTest {
        val expenses = listOf(
            Expense(amount = 10.0, description = "Test", date = LocalDate.now(), categoryId = 1)
        )

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        val badges = viewModel.badges.value
        val firstBadge = badges.find { it.id == "first_expense" }
        assertTrue("First expense badge should be unlocked", firstBadge?.isUnlocked ?: false)
    }

    @Test
    fun `evaluateBadges unlocks ten_expenses badge when there are 10 expenses`() = runTest {
        val expenses = List(10) {
            Expense(amount = 1.0, description = "Test", date = LocalDate.now(), categoryId = 1)
        }

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        val badges = viewModel.badges.value
        assertTrue("Ten expenses badge should be unlocked", badges.find { it.id == "ten_expenses" }?.isUnlocked ?: false)
    }

    @Test
    fun `evaluateBadges unlocks thirty_expenses badge when there are 30 expenses`() = runTest {
        val expenses = List(30) {
            Expense(amount = 1.0, description = "Test", date = LocalDate.now(), categoryId = 1)
        }

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        val badges = viewModel.badges.value
        assertTrue("Thirty expenses badge should be unlocked", badges.find { it.id == "thirty_expenses" }?.isUnlocked ?: false)
    }

    @Test
    fun `evaluateBadges unlocks hundred_rands badge when total amount is 100 or more`() = runTest {
        val expenses = listOf(
            Expense(amount = 100.0, description = "Test", date = LocalDate.now(), categoryId = 1)
        )

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        val badges = viewModel.badges.value
        assertTrue("Hundred rands badge should be unlocked", badges.find { it.id == "hundred_rands" }?.isUnlocked ?: false)
    }

    @Test
    fun `evaluateBadges unlocks five_categories badge when there are 5 unique categories`() = runTest {
        val expenses = (1..5).map {
            Expense(amount = 1.0, description = "Test", date = LocalDate.now(), categoryId = it)
        }

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        val badges = viewModel.badges.value
        assertTrue("Five categories badge should be unlocked", badges.find { it.id == "five_categories" }?.isUnlocked ?: false)
    }

    @Test
    fun `evaluateBadges calculates correct streak for consecutive days`() = runTest {
        val today = LocalDate.now()
        val expenses = listOf(
            Expense(amount = 1.0, description = "Day 1", date = today, categoryId = 1),
            Expense(amount = 1.0, description = "Day 2", date = today.minusDays(1), categoryId = 1),
            Expense(amount = 1.0, description = "Day 3", date = today.minusDays(2), categoryId = 1)
        )

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        assertEquals(3, viewModel.streakDays.value)
    }

    @Test
    fun `evaluateBadges unlocks seven_streak badge when streak is 7 or more`() = runTest {
        val today = LocalDate.now()
        val expenses = (0..6).map {
            Expense(amount = 1.0, description = "Day $it", date = today.minusDays(it.toLong()), categoryId = 1)
        }

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        val badges = viewModel.badges.value
        assertEquals(7, viewModel.streakDays.value)
        assertTrue("Seven streak badge should be unlocked", badges.find { it.id == "seven_streak" }?.isUnlocked ?: false)
    }

    @Test
    fun `streak is zero when no expenses are present`() = runTest {
        viewModel.evaluateBadges(emptyList())
        advanceUntilIdle()

        assertEquals(0, viewModel.streakDays.value)
    }

    @Test
    fun `streak breaks when there is a gap in days`() = runTest {
        val today = LocalDate.now()
        val expenses = listOf(
            Expense(amount = 1.0, description = "Today", date = today, categoryId = 1),
            Expense(amount = 1.0, description = "Gap day", date = today.minusDays(2), categoryId = 1)
        )

        viewModel.evaluateBadges(expenses)
        advanceUntilIdle()

        assertEquals(1, viewModel.streakDays.value)
    }
}
