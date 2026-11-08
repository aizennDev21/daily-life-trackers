package com.example.data.repository

import com.example.core.utils.DateUtils
import com.example.data.local.dao.LifeTrackerDao
import com.example.data.local.entities.ActivityEntity
import com.example.data.local.entities.ExerciseLogEntity
import com.example.data.local.entities.FocusSessionEntity
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.HabitLogEntity
import com.example.data.local.entities.LearningLogEntity
import com.example.data.local.entities.MealLogEntity
import com.example.data.local.entities.MoodLogEntity
import com.example.data.local.entities.SleepLogEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserProfileEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.WaterLogEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LifeTrackerRepository(private val dao: LifeTrackerDao) {

    // --- Flows ---
    val activeUser: Flow<UserAccountEntity?> = dao.getActiveUser()
    val allActivities: Flow<List<ActivityEntity>> = dao.getAllActivities()
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allSleepLogs: Flow<List<SleepLogEntity>> = dao.getAllSleepLogs()
    val allMealLogs: Flow<List<MealLogEntity>> = dao.getAllMealLogs()
    val allExerciseLogs: Flow<List<ExerciseLogEntity>> = dao.getAllExerciseLogs()
    val allMoodLogs: Flow<List<MoodLogEntity>> = dao.getAllMoodLogs()
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits()
    val allHabitLogs: Flow<List<HabitLogEntity>> = dao.getAllHabitLogs()
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val allFocusSessions: Flow<List<FocusSessionEntity>> = dao.getAllFocusSessions()
    val allLearningLogs: Flow<List<LearningLogEntity>> = dao.getAllLearningLogs()
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()

    fun getSleepLog(date: String): Flow<SleepLogEntity?> = dao.getSleepLog(date)
    fun getWaterLog(date: String): Flow<WaterLogEntity?> = dao.getWaterLog(date)
    fun getMoodLog(date: String): Flow<MoodLogEntity?> = dao.getMoodLog(date)
    fun getMealLogsByDate(date: String): Flow<List<MealLogEntity>> = dao.getMealLogsByDate(date)
    fun getHabitLogsByDate(date: String): Flow<List<HabitLogEntity>> = dao.getHabitLogsByDate(date)

    // --- Activities ---
    suspend fun addActivity(activity: ActivityEntity) = dao.insertActivity(activity)
    suspend fun deleteActivity(activity: ActivityEntity) = dao.deleteActivity(activity)

    // --- Transactions ---
    suspend fun addTransaction(transaction: TransactionEntity) = dao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = dao.deleteTransaction(transaction)

    // --- Sleep & Health ---
    suspend fun saveSleepLog(sleepLog: SleepLogEntity) = dao.insertSleepLog(sleepLog)

    // --- Meals ---
    suspend fun addMealLog(mealLog: MealLogEntity) = dao.insertMealLog(mealLog)
    suspend fun deleteMealLog(mealLog: MealLogEntity) = dao.deleteMealLog(mealLog)

    // --- Water ---
    suspend fun saveWaterLog(waterLog: WaterLogEntity) = dao.insertWaterLog(waterLog)

    // --- Exercise ---
    suspend fun addExerciseLog(exerciseLog: ExerciseLogEntity) = dao.insertExerciseLog(exerciseLog)
    suspend fun deleteExerciseLog(exerciseLog: ExerciseLogEntity) = dao.deleteExerciseLog(exerciseLog)

    // --- Mood ---
    suspend fun saveMoodLog(moodLog: MoodLogEntity) = dao.insertMoodLog(moodLog)

    // --- Habits ---
    suspend fun addHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)
    suspend fun toggleHabitCompletion(habitId: String, date: String, completed: Boolean) {
        if (completed) {
            dao.insertHabitLog(HabitLogEntity(id = "${date}_$habitId", habitId = habitId, date = date, completed = true))
        } else {
            dao.deleteHabitLog(habitId, date)
        }
    }

    // --- Tasks ---
    suspend fun addTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)

    // --- Focus ---
    suspend fun addFocusSession(session: FocusSessionEntity) = dao.insertFocusSession(session)

    // --- Learning ---
    suspend fun addLearningLog(log: LearningLogEntity) = dao.insertLearningLog(log)
    suspend fun deleteLearningLog(log: LearningLogEntity) = dao.deleteLearningLog(log)

    // --- Goals ---
    suspend fun addGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)

    // --- Profile ---
    suspend fun saveUserProfile(profile: UserProfileEntity) = dao.insertUserProfile(profile)

    // --- Authentication & User Accounts ---
    suspend fun registerUser(fullName: String, email: String, password: String): Result<UserAccountEntity> {
        val trimmedEmail = email.trim()
        val trimmedName = fullName.trim()
        if (trimmedEmail.isBlank() || trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama dan email wajib diisi."))
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Format email tidak valid."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password minimal 6 karakter."))
        }

        val existing = dao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalStateException("Email sudah terdaftar. Silakan login."))
        }

        // Logout any other accounts
        dao.logoutAllUsers()

        val newUser = UserAccountEntity(
            id = UUID.randomUUID().toString(),
            fullName = trimmedName,
            email = trimmedEmail,
            passwordHash = password,
            avatarEmoji = listOf("🚀", "⚡", "🌟", "🦁", "🌸", "🎯", "🔥").random(),
            isLoggedIn = true,
            isOnboardingCompleted = true
        )
        dao.insertUserAccount(newUser)

        // Also sync profile entity
        dao.insertUserProfile(
            UserProfileEntity(
                uid = newUser.id,
                displayName = newUser.fullName,
                email = newUser.email,
                waterGoalGlasses = 8,
                isDarkMode = true
            )
        )

        return Result.success(newUser)
    }

    suspend fun loginUser(email: String, password: String): Result<UserAccountEntity> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email dan password tidak boleh kosong."))
        }

        val user = dao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("Akun tidak ditemukan. Silakan daftar."))

        if (user.passwordHash != password) {
            return Result.failure(IllegalArgumentException("Password salah. Silakan coba lagi."))
        }

        dao.logoutAllUsers()
        val updated = user.copy(isLoggedIn = true, isOnboardingCompleted = true)
        dao.updateUserAccount(updated)

        // Sync profile entity
        dao.insertUserProfile(
            UserProfileEntity(
                uid = updated.id,
                displayName = updated.fullName,
                email = updated.email,
                waterGoalGlasses = 8,
                isDarkMode = true
            )
        )

        return Result.success(updated)
    }

    suspend fun logoutUser() {
        dao.logoutAllUsers()
    }

    suspend fun setOnboardingCompleted(userId: String) {
        dao.setOnboardingCompleted(userId)
    }

    suspend fun clearAllData() {
        dao.clearActivities()
        dao.clearTransactions()
        dao.clearTasks()
        dao.clearHabits()
        dao.clearHabitLogs()
        dao.clearSleepLogs()
        dao.clearWaterLogs()
        dao.clearMealLogs()
        dao.clearExerciseLogs()
        dao.clearMoodLogs()
        dao.clearFocusSessions()
        dao.clearLearningLogs()
        dao.clearGoals()
    }

    // --- Clean Initialization (No Dummy / Mock Data) ---
    suspend fun seedInitialDataIfNeeded() {
        // Clean initialization: We do not inject fake/dummy tasks, transactions or logs.
        // Data is 100% dynamic and created by the logged-in user!
    }
}
