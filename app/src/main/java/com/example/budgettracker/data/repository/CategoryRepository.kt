package com.example.budgettracker.data.repository

import com.example.budgettracker.data.local.dao.CategoryDao
import com.example.budgettracker.data.local.entity.CategoryEntity
import com.example.budgettracker.data.model.Category
import com.example.budgettracker.data.model.toCategory
import com.example.budgettracker.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: Flow<List<Category>> = categoryDao
        .getAllCategories()
        .map { entities -> entities.map { it.toCategory() } }

    suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)?.toCategory()
    }

    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    suspend fun getCategoryCount(): Int {
        return categoryDao.getCategoryCount()
    }
}