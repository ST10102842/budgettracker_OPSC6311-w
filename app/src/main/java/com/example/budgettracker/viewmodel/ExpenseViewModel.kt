package com.example.budgettracker.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.model.Category
import com.example.budgettracker.data.model.Expense
import com.example.budgettracker.data.repository.CategoryRepository
import com.example.budgettracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate


class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // --- UI State ---

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    // All expenses — observed from database
    val allExpenses: StateFlow<List<Expense>> = expenseRepository
        .allExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All categories — observed from database
    val allCategories: StateFlow<List<Category>> = categoryRepository
        .allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Monthly total spending
    private val _monthlyTotal = MutableStateFlow(0.0)
    val monthlyTotal: StateFlow<Double> = _monthlyTotal.asStateFlow()

    // Filter states
    private val _selectedDateRange = MutableStateFlow<Pair<LocalDate, LocalDate>?>(null)
    val selectedDateRange: StateFlow<Pair<LocalDate, LocalDate>?> = _selectedDateRange.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    // Filtered expenses
    val filteredExpenses: StateFlow<List<Expense>> = combine(
        allExpenses,
        _selectedDateRange,
        _selectedCategoryId
    ) { expenses, dateRange, categoryId ->
        var filtered = expenses

        // Apply date range filter
        if (dateRange != null) {
            filtered = filtered.filter { expense ->
                !expense.date.isBefore(dateRange.first) && !expense.date.isAfter(dateRange.second)
            }
        }

        // Apply category filter
        if (categoryId != null) {
            filtered = filtered.filter { it.categoryId == categoryId }
        }

        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadMonthlyTotal()
    }

    private fun loadMonthlyTotal() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val startOfMonth = now.withDayOfMonth(1)
            expenseRepository
                .getTotalSpendingInRange(startOfMonth, now)
                .collect { total ->
                    _monthlyTotal.value = total ?: 0.0
                }
        }
    }

    // --- Actions ---

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                expenseRepository.insertExpense(expense)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Failed to save expense: ${e.message}")
                }
            }
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpenseById(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete expense.") }
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.insertCategory(category)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to save category.") }
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // --- Filter Actions ---

    fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        _selectedDateRange.value = Pair(startDate, endDate)
    }

    fun clearDateRange() {
        _selectedDateRange.value = null
    }

    fun setCategoryFilter(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun clearCategoryFilter() {
        _selectedCategoryId.value = null
    }

    fun clearAllFilters() {
        _selectedDateRange.value = null
        _selectedCategoryId.value = null
    }
}

// Holds the current state of the UI (loading, errors, success signals)
data class ExpenseUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

// Factory to create the ViewModel with dependencies
class ExpenseViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(expenseRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}