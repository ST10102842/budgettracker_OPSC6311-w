package com.example.budgettracker.navigation

// Each screen in our app has a unique route string — like a URL
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object AddExpense : Screen("add_expense")
    object ExpenseList : Screen("expense_list")
    object Categories : Screen("categories")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}