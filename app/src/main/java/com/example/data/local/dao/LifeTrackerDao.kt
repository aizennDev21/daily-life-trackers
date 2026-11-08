package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface LifeTrackerDao {

    // --- Activities ---
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // --- Sleep Logs ---
    @Query("SELECT * FROM sleep_logs ORDER BY date DESC")
    fun getAllSleepLogs(): Flow<List<SleepLogEntity>>

    @Query("SELECT * FROM sleep_logs WHERE date = :date LIMIT 1")
    fun getSleepLog(date: String): Flow<SleepLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(sleepLog: SleepLogEntity)

    // --- Meal Logs ---
    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY timestamp ASC")
    fun getMealLogsByDate(date: String): Flow<List<MealLogEntity>>

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLogEntity)

    @Delete
    suspend fun deleteMealLog(mealLog: MealLogEntity)

    // --- Water Logs ---
    @Query("SELECT * FROM water_logs WHERE date = :date LIMIT 1")
    fun getWaterLog(date: String): Flow<WaterLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLogEntity)

    // --- Exercise Logs ---
    @Query("SELECT * FROM exercise_logs ORDER BY timestamp DESC")
    fun getAllExerciseLogs(): Flow<List<ExerciseLogEntity>>

    @Query("SELECT * FROM exercise_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getExerciseLogsByDate(date: String): Flow<List<ExerciseLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(exerciseLog: ExerciseLogEntity)

    @Delete
    suspend fun deleteExerciseLog(exerciseLog: ExerciseLogEntity)

    // --- Mood Logs ---
    @Query("SELECT * FROM mood_logs ORDER BY date DESC")
    fun getAllMoodLogs(): Flow<List<MoodLogEntity>>

    @Query("SELECT * FROM mood_logs WHERE date = :date LIMIT 1")
    fun getMoodLog(date: String): Flow<MoodLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodLog(moodLog: MoodLogEntity)

    // --- Habits ---
    @Query("SELECT * FROM habits ORDER BY sortOrder ASC, createdAt ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // --- Habit Daily Logs ---
    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getHabitLogsByDate(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs ORDER BY date DESC")
    fun getAllHabitLogs(): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND completed = 1 ORDER BY date DESC")
    fun getCompletedLogsForHabit(habitId: String): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitLog(habitId: String, date: String)

    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority DESC, sortOrder ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // --- Focus Sessions ---
    @Query("SELECT * FROM focus_sessions ORDER BY startedAt DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY startedAt DESC")
    fun getFocusSessionsByDate(date: String): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity)

    // --- Learning Logs ---
    @Query("SELECT * FROM learning_logs ORDER BY createdAt DESC")
    fun getAllLearningLogs(): Flow<List<LearningLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningLog(log: LearningLogEntity)

    @Delete
    suspend fun deleteLearningLog(log: LearningLogEntity)

    // --- Goals ---
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // --- User Profile ---
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    // --- User Account Authentication ---
    @Query("SELECT * FROM user_accounts WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccountEntity)

    @Update
    suspend fun updateUserAccount(user: UserAccountEntity)

    @Query("UPDATE user_accounts SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE user_accounts SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedInUser(userId: String)

    @Query("UPDATE user_accounts SET isOnboardingCompleted = 1 WHERE id = :userId")
    suspend fun setOnboardingCompleted(userId: String)

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserAccountCount(): Int

    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    fun getAllUserAccounts(): Flow<List<UserAccountEntity>>

    // --- Reset / Clear Data ---
    @Query("DELETE FROM activities")
    suspend fun clearActivities()

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("DELETE FROM habit_logs")
    suspend fun clearHabitLogs()

    @Query("DELETE FROM sleep_logs")
    suspend fun clearSleepLogs()

    @Query("DELETE FROM water_logs")
    suspend fun clearWaterLogs()

    @Query("DELETE FROM meal_logs")
    suspend fun clearMealLogs()

    @Query("DELETE FROM exercise_logs")
    suspend fun clearExerciseLogs()

    @Query("DELETE FROM mood_logs")
    suspend fun clearMoodLogs()

    @Query("DELETE FROM focus_sessions")
    suspend fun clearFocusSessions()

    @Query("DELETE FROM learning_logs")
    suspend fun clearLearningLogs()

    @Query("DELETE FROM goals")
    suspend fun clearGoals()
}
