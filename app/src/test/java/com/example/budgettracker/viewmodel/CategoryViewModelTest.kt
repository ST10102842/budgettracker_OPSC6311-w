package com.example.budgettracker.viewmodel

import com.example.budgettracker.data.model.Category
import com.example.budgettracker.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    private lateinit var viewModel: CategoryViewModel
    private val repository: CategoryRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(repository.allCategories).thenReturn(flowOf(emptyList()))
        viewModel = CategoryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addCategory with valid data calls repository and updates state`() = runTest {
        val name = "Food"
        val emoji = "🍔"
        val color = "#FF0000"
        val budget = 1000.0

        viewModel.addCategory(name, emoji, color, budget)
        advanceUntilIdle()

        verify(repository).insertCategory(argThat {
            this.name == name && iconEmoji == emoji && colorHex == color && monthlyBudget == budget
        })
        assertTrue(viewModel.uiState.value.saveSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `addCategory with blank name sets error message`() = runTest {
        viewModel.addCategory("", "🍔", "#FF0000", 1000.0)
        
        assertEquals("Category name cannot be empty", viewModel.uiState.value.errorMessage)
        verify(repository, never()).insertCategory(any())
    }

    @Test
    fun `updateCategory calls repository and updates state`() = runTest {
        val category = Category(id = 1, name = "Food", iconEmoji = "🍔", colorHex = "#FF0000", monthlyBudget = 1000.0)
        
        viewModel.updateCategory(category)
        advanceUntilIdle()

        verify(repository).updateCategory(category)
        assertTrue(viewModel.uiState.value.saveSuccess)
    }

    @Test
    fun `deleteCategory calls repository`() = runTest {
        val category = Category(id = 1, name = "Food", iconEmoji = "🍔", colorHex = "#FF0000", monthlyBudget = 1000.0)
        
        viewModel.deleteCategory(category)
        advanceUntilIdle()

        verify(repository).deleteCategory(category)
    }

    @Test
    fun `openAddSheet updates uiState`() {
        viewModel.openAddSheet()
        assertTrue(viewModel.uiState.value.showAddSheet)
        assertNull(viewModel.uiState.value.categoryToEdit)
    }

    @Test
    fun `openEditSheet updates uiState with category`() {
        val category = Category(id = 1, name = "Food", iconEmoji = "🍔", colorHex = "#FF0000", monthlyBudget = 1000.0)
        viewModel.openEditSheet(category)
        
        assertTrue(viewModel.uiState.value.showAddSheet)
        assertEquals(category, viewModel.uiState.value.categoryToEdit)
    }

    @Test
    fun `closeSheet updates uiState`() {
        viewModel.openAddSheet()
        viewModel.closeSheet()
        
        assertFalse(viewModel.uiState.value.showAddSheet)
        assertNull(viewModel.uiState.value.categoryToEdit)
    }

    @Test
    fun `clearError removes error message from state`() = runTest {
        viewModel.addCategory("", "🍔", "#FF0000", 1000.0)
        assertNotNull(viewModel.uiState.value.errorMessage)
        
        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `repository failure updates error message in state`() = runTest {
        whenever(repository.insertCategory(any())).thenThrow(RuntimeException("DB Error"))
        
        viewModel.addCategory("Food", "🍔", "#FF0000", 1000.0)
        advanceUntilIdle()
        
        assertEquals("Failed to save: DB Error", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
