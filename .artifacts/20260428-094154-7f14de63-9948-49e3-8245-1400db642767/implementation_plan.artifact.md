# Implementation Plan - Min/Max Budget Limit Feature

Add a feature that allows users to set a minimum and maximum global monthly budget limit. These limits will be stored in the local database and used to provide visual feedback on the dashboard.

## User Review Required

> [!NOTE]
> The database version will be incremented to 2. To avoid complex migrations in this phase, I will use `fallbackToDestructiveMigration()`. This means existing data might be cleared on the first launch after this update.

## Proposed Changes

### Data Layer

#### [BudgetLimitEntity.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/data/local/entity/BudgetLimitEntity.kt) [NEW]
- Define `BudgetLimitEntity` with `id`, `minLimit`, and `maxLimit`.

#### [BudgetLimitDao.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/data/local/dao/BudgetLimitDao.kt) [NEW]
- Create `BudgetLimitDao` with methods to get and insert/update the limit.

#### [BudgetDatabase.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/data/local/database/BudgetDatabase.kt)
- Add `BudgetLimitEntity` to the `@Database` annotation.
- Increment version to 2.
- Add `budgetLimitDao()` abstract function.
- Add `.fallbackToDestructiveMigration()` to the builder.

#### [BudgetLimit.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/data/model/BudgetLimit.kt) [NEW]
- Define domain model `BudgetLimit`.

#### [Mappers.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/data/model/Mappers.kt)
- Add mapping functions between `BudgetLimitEntity` and `BudgetLimit`.

#### [BudgetLimitRepository.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/data/repository/BudgetLimitRepository.kt) [NEW]
- Create repository to handle budget limit data operations.

---

### ViewModel Layer

#### [ExpenseViewModel.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/viewmodel/ExpenseViewModel.kt)
- Inject `BudgetLimitRepository`.
- Add `budgetLimit: StateFlow<BudgetLimit?>`.
- Add `updateBudgetLimits(min: Double, max: Double)` function.

---

### UI Layer

#### [ProfileScreen.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/ui/screens/ProfileScreen.kt)
- Add a "Budget Settings" section.
- Provide input fields for Minimum and Maximum budget limits.
- Add a "Save Limits" button.

#### [DashboardScreen.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/app/src/main/java/com/example/budgettracker/ui/screens/DashboardScreen.kt)
- Add a "Monthly Budget Status" card.
- Show a progress bar indicating current spending relative to the max limit.
- Display status messages:
    - "Under Minimum Goal" if spending < min limit.
    - "Within Budget" if spending is between min and max.
    - "Over Budget" if spending > max limit.

---

### Dependency Injection / Setup

#### [MainActivity.kt](file:///Volumes/DevDrive/WorkSpace/KotlinProjects/BudgetTracker/MainActivity.kt)
- Instantiate `BudgetLimitRepository`.
- Pass it to `ExpenseViewModelFactory`.

## Verification Plan

### Automated Tests
- I will run existing tests to ensure no regressions:
  ```bash
  ./gradlew testDebugUnitTest
  ```

### Manual Verification
1.  **Set Limits**: Navigate to Profile screen and set Min Budget = 500 and Max Budget = 1000. Save them.
2.  **Verify Persistence**: Restart the app and check if limits are still there in the Profile screen.
3.  **Dashboard Feedback**:
    - Add expenses such that total is 300. Verify Dashboard says "Under Minimum Goal".
    - Add expenses such that total is 750. Verify Dashboard says "Within Budget".
    - Add expenses such that total is 1200. Verify Dashboard says "Over Budget" (and progress bar is red).
