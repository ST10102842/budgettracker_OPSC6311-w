# Budget Tracker 💰

A modern, feature-rich Android expense tracking application built with **Kotlin** and **Jetpack Compose**. Take control of your spending with intuitive budget management, category-based expense tracking, and beautiful data visualization.
## Demonstration Video

## References

- [Kotlin Official Website](https://kotlinlang.org/) - For language reference and documentation.
- [Jetpack Compose Website](https://developer.android.com/jetpack/compose) - For UI framework reference and documentation.
- Material Design 3 guidelines
- Android Best Practices
- Open source community contributions

## 📚 Additional Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Material Design 3](https://m3.material.io/)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

## ✨ Features

### 🔐 Authentication
- **User Registration** - Create new accounts with email and password
- **User Login** - Secure login with credentials validation
- **User Profiles** - View profile information with username and email
- **Logout** - Secure logout functionality

### 💳 Expense Management
- **Add Expenses** - Quick and easy expense entry with amount, description, category, and date
- **View Expenses** - Browse all expenses in a clean list format
- **Delete Expenses** - Remove expenses with confirmation dialog
- **Real-time Database Sync** - All changes sync instantly to local database

### 🏷️ Categories
- **Manage Categories** - Create and manage expense categories
- **Category-based Tracking** - Organize expenses by custom categories
- **Category Spending** - Visual breakdown of spending by category

### 📊 Filtering & Analytics
- **Date Range Filtering** - Filter expenses by start and end date using Material3 date pickers
- **Category Filtering** - Filter expenses by specific category
- **Combined Filtering** - Apply both date and category filters simultaneously
- **Monthly Statistics** - View total spending for the current month
- **Pie Chart Visualization** - Visual representation of spending distribution

### 🎨 UI/UX
- **Modern Material Design 3** - Beautiful, responsive user interface
- **Dark Mode Support** - Automatic dark/light theme support
- **Smooth Navigation** - Seamless screen transitions
- **Intuitive Layouts** - User-friendly interface design
- **Animated Transitions** - Smooth and polished interactions

## 📱 Technical Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: Android 26 (Android 8.0)
- **Target SDK**: Android 35

### Libraries & Dependencies
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite with Kotlin Coroutines)
- **Navigation**: Jetpack Navigation Compose
- **State Management**: Kotlin Flows & StateFlow
- **Lifecycle**: Jetpack Lifecycle components
- **Testing**: JUnit, Mockito, Coroutines Test

### Key Dependencies
```gradle
// Jetpack Components
androidx.lifecycle:lifecycle-runtime-ktx:2.8.7
androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7
androidx.navigation:navigation-compose:2.8.4

// Room Database
androidx.room:room-runtime:2.x.x
androidx.room:room-ktx:2.x.x

// Compose
androidx.compose.ui:ui:latest
androidx.compose.material3:material3:latest

// Testing
junit:junit:4.x.x
org.mockito.kotlin:mockito-kotlin:5.x.x
org.jetbrains.kotlinx:kotlinx-coroutines-test:1.x.x
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK 26 (API 26) or higher

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/BudgetTracker.git
cd BudgetTracker
```

2. **Open in Android Studio**
- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to the BudgetTracker folder
- Click "Open"

3. **Build the project**
```bash
./gradlew build
```

4. **Run on emulator or device**
- Click the "Run" button or press Shift + F10
- Select your target device/emulator
- Wait for the app to build and install

## 📋 Usage

### First Time Setup

1. **Register Account**
   - Click "Register" on the login screen
   - Enter your username, email, and password
   - Click "Register" to create your account

2. **Or Login with Demo Account**
   - Username: `admin`
   - Password: `1234`

### Adding Expenses

1. Click the **+** button on the Dashboard
2. Fill in the expense details:
   - **Amount**: Enter the expense amount
   - **Description**: Brief description of the expense
   - **Date**: Select the date (YYYY-MM-DD format)
   - **Category**: Choose from available categories
3. Click **"Save Expense"**

### Managing Expenses

- **View All Expenses**: Click "View All Expenses" button on Dashboard
- **Filter by Date**: Click filter icon → Select date range → Click "Apply Date Range"
- **Filter by Category**: Click filter icon → Select category chip
- **Delete Expense**: Click the trash icon on any expense and confirm deletion
- **Clear Filters**: Click "Clear All Filters" button in the filter panel

### Managing Categories

- Navigate to "Manage Categories" from the Dashboard
- Add, view, and manage your expense categories
- Use emoji icons to quickly identify categories

### View Profile

- Click the **👤 Profile** icon in the top-right corner of the Dashboard
- View your username and email
- Click **"Logout"** to logout from your account

## 🏗️ Project Structure

```
BudgetTracker/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/budgettracker/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   │   └── BudgetDatabase.kt
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── ExpenseDao.kt
│   │   │   │   │   │   │   └── CategoryDao.kt
│   │   │   │   │   │   └── entity/
│   │   │   │   │   │       ├── ExpenseEntity.kt
│   │   │   │   │   │       └── CategoryEntity.kt
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Expense.kt
│   │   │   │   │   │   ├── Category.kt
│   │   │   │   │   │   └── Mappers.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── ExpenseRepository.kt
│   │   │   │   │       └── CategoryRepository.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── Screen.kt
│   │   │   │   │   └── NavGraph.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   │   ├── ProfileScreen.kt
│   │   │   │   │   │   ├── AddExpenseScreen.kt
│   │   │   │   │   │   ├── ExpenseListScreen.kt
│   │   │   │   │   │   ├── CategoryScreen.kt
│   │   │   │   │   │   └── SplashScreen.kt
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   └── Theme.kt
│   │   │   │   │   └── components/
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── AuthViewModel.kt
│   │   │   │   │   ├── ExpenseViewModel.kt
│   │   │   │   │   └── CategoryViewModel.kt
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       ├── mipmap-*/
│   │   │       ├── values/
│   │   │       └── xml/
│   │   ├── test/
│   │   │   └── java/com/example/budgettracker/
│   │   │       └── viewmodel/
│   │   │           ├── AuthViewModelTest.kt
│   │   │           └── ExpenseViewModelTest.kt
│   │   └── androidTest/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── .github/
│   └── workflows/
│       └── android_ci.yml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🧪 Testing

### Running Tests

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run specific test class
./gradlew testDebugUnitTest --tests com.example.budgettracker.viewmodel.AuthViewModelTest

# Run specific test method
./gradlew testDebugUnitTest --tests com.example.budgettracker.viewmodel.AuthViewModelTest.testLoginWithValidCredentials
```

### Test Coverage

- **AuthViewModel Tests** (8 tests)
  - Login with valid/invalid credentials
  - Registration with validation
  - Logout functionality
  - Error handling

- **ExpenseViewModel Tests** (8 tests)
  - Filter operations (date range, category)
  - Clear filters
  - UI state management

### Testing Libraries
- **JUnit 4** - Unit testing framework
- **Mockito** - Mocking framework
- **Coroutines Test** - Coroutine testing utilities

## 🔄 CI/CD Pipeline

The project includes a GitHub Actions workflow (`android_ci.yml`) that automatically:

1. **Builds the project** on every push and pull request
2. **Runs unit tests** to ensure code quality
3. **Performs static analysis** with lint
4. **Generates APK** for testing
5. **Uploads artifacts** for download

### Workflow Triggers
- Pushes to `main` and `develop` branches
- Pull requests to `main` and `develop` branches

### Artifacts Generated
- **app-debug.apk** - Debuggable APK for testing
- **test-results** - JUnit test reports
- **lint-results** - Static analysis reports

## 🎨 Theming

The app uses Material Design 3 with automatic light/dark mode support based on system settings.

### Color Scheme
- **Primary**: Material Blue
- **Secondary**: Material Teal
- **Tertiary**: Material Purple
- **Error**: Material Red

## 🐛 Known Issues

- None currently reported

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## 👨‍💻 Author

- **Your Name** - Initial work and development




**Happy Budgeting! 💚**

