package com.example.budgettracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.budgettracker.data.local.dao.CategoryDao
import com.example.budgettracker.data.local.dao.ExpenseDao
import com.example.budgettracker.data.local.entity.CategoryEntity
import com.example.budgettracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ExpenseEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {

        @Volatile
        private var INSTANCE: BudgetDatabase? = null

        fun getDatabase(context: Context): BudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java,
                    "budget_tracker_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    // Callback runs code when the database is first created
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Pre-populate with default categories on first launch
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultCategories(database.categoryDao())
                }
            }
        }

        suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                CategoryEntity(name = "Food & Drink",    iconEmoji = "🍔", colorHex = "#FF5722"),
                CategoryEntity(name = "Transport",       iconEmoji = "🚗", colorHex = "#2196F3"),
                CategoryEntity(name = "Entertainment",   iconEmoji = "🎮", colorHex = "#9C27B0"),
                CategoryEntity(name = "Utilities",       iconEmoji = "💡", colorHex = "#FF9800"),
                CategoryEntity(name = "Health",          iconEmoji = "💊", colorHex = "#4CAF50"),
                CategoryEntity(name = "Shopping",        iconEmoji = "🛍️", colorHex = "#E91E63"),
                CategoryEntity(name = "Education",       iconEmoji = "📚", colorHex = "#3F51B5"),
                CategoryEntity(name = "Other",           iconEmoji = "📦", colorHex = "#607D8B")
            )
            defaultCategories.forEach { categoryDao.insertCategory(it) }
        }
    }
}