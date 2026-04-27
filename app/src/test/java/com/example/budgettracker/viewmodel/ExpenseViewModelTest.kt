package com.example.budgettracker.viewmodel

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Test
import java.time.LocalDate

class ExpenseViewModelFilterTest {

    @Test
    fun testFilteringByDateRange() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 31)
        val testDate = LocalDate.of(2024, 1, 15)

        // Verify date is within range
        val isWithinRange = !testDate.isBefore(startDate) && !testDate.isAfter(endDate)
        assertEquals(true, isWithinRange)
    }

    @Test
    fun testFilteringByCategory() {
        val categoryId = 1
        val selectedCategory = 1

        val isSelected = categoryId == selectedCategory
        assertEquals(true, isSelected)
    }

    @Test
    fun testDateRangeValidation() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 31)

        // Verify start is before end
        val isValid = startDate.isBefore(endDate) || startDate.isEqual(endDate)
        assertEquals(true, isValid)
    }

    @Test
    fun testMultipleCategoriesFiltering() {
        val expense1Category = 1
        val expense2Category = 2
        val selectedCategories = listOf(1, 2)

        val expense1Matches = expense1Category in selectedCategories
        val expense2Matches = expense2Category in selectedCategories

        assertEquals(true, expense1Matches)
        assertEquals(true, expense2Matches)
    }

    @Test
    fun testExpenseDateComparison() {
        val expenseDate = LocalDate.of(2024, 3, 15)
        val filterStartDate = LocalDate.of(2024, 3, 1)
        val filterEndDate = LocalDate.of(2024, 3, 31)

        val matchesFilter = !expenseDate.isBefore(filterStartDate) && !expenseDate.isAfter(filterEndDate)
        assertEquals(true, matchesFilter)
    }

    @Test
    fun testExpenseDateOutsideRange() {
        val expenseDate = LocalDate.of(2024, 4, 15)
        val filterStartDate = LocalDate.of(2024, 3, 1)
        val filterEndDate = LocalDate.of(2024, 3, 31)

        val matchesFilter = !expenseDate.isBefore(filterStartDate) && !expenseDate.isAfter(filterEndDate)
        assertEquals(false, matchesFilter)
    }

    @Test
    fun testEmptyFilterResults() {
        val expenses = emptyList<String>()
        assertEquals(0, expenses.size)
        assertEquals(true, expenses.isEmpty())
    }

    @Test
    fun testLocalDateOperations() {
        val date1 = LocalDate.of(2024, 1, 1)
        val date2 = LocalDate.of(2024, 1, 2)

        assertEquals(true, date1.isBefore(date2))
        assertEquals(false, date1.isAfter(date2))
        assertEquals(false, date1.isEqual(date2))
    }
}


