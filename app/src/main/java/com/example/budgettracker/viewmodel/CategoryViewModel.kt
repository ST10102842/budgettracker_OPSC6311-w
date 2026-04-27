package com.example.budgettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.model.Category
import com.example.budgettracker.data.repository.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // ── State ──────────────────────────────────────────────
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    // Live list of all categories from Room — updates automatically when DB changes
    val allCategories: StateFlow<List<Category>> = categoryRepository
        .allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ── Actions ────────────────────────────────────────────

    fun addCategory(name: String, emoji: String, colorHex: String, monthlyBudget: Double) {
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Category name cannot be empty") }
            return
        }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                categoryRepository.insertCategory(
                    Category(
                        name = name.trim(),
                        iconEmoji = emoji,
                        colorHex = colorHex,
                        monthlyBudget = monthlyBudget
                    )
                )
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Failed to save: ${e.message}")
                }
            }
        }
    }

    fun updateCategory(category: Category) {
        if (category.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Category name cannot be empty") }
            return
        }
        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(category)
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update: ${e.message}") }
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete: ${e.message}") }
            }
        }
    }

    fun openAddSheet()  { _uiState.update { it.copy(showAddSheet = true, categoryToEdit = null) } }
    fun openEditSheet(category: Category) {
        _uiState.update { it.copy(showAddSheet = true, categoryToEdit = category) }
    }
    fun closeSheet()    { _uiState.update { it.copy(showAddSheet = false, categoryToEdit = null) } }
    fun clearError()    { _uiState.update { it.copy(errorMessage = null) } }
    fun clearSuccess()  { _uiState.update { it.copy(saveSuccess = false) } }
}

// ── UI State Data Class ────────────────────────────────────
data class CategoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
    val showAddSheet: Boolean = false,
    val categoryToEdit: Category? = null   // null = adding new, non-null = editing existing
)

// ── Factory ───────────────────────────────────────────────
class CategoryViewModelFactory(
    private val repository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}