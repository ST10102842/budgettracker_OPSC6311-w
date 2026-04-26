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