package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// 1. Activity Log
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val category: String, // work, personal, health, social, errands, rest, finance, food, exercise
    val emoji: String? = null,
    val location: String? = null,
    val durationMinutes: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// 2. Finance Transactions
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String, // income, expense, saving
    val amount: Long, // IDR
    val category: String, // Makan, Transport, Belanja, Tagihan, Kesehatan, Gaji, Freelance, etc.
    val note: String? = null,
    val isImportant: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// 3. Sleep & Health Logs
@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey val date: String, // yyyy-MM-dd
    val bedtime: String? = "23:00",
    val wakeTime: String? = "06:30",
    val durationMinutes: Int = 450, // 7h 30m
    val quality: Int = 4, // 1-5 stars
    val dreamNote: String? = null,
    val bodyCondition: String = "healthy", // healthy, unwell, sick
    val weightKg: Float? = null,
    val symptomsNote: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// 4. Meal Logs
@Entity(tableName = "meal_logs")
data class MealLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String, // yyyy-MM-dd
    val mealType: String, // breakfast, lunch, dinner, snack
    val foodName: String,
    val caloriesEstimate: Int? = null,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// 5. Water Logs
@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey val date: String, // yyyy-MM-dd
    val glassesCount: Int = 0,
    val targetGlasses: Int = 8
)

// 6. Exercise Logs
@Entity(tableName = "exercise_logs")
data class ExerciseLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String, // yyyy-MM-dd
    val exerciseType: String, // running, walking, gym, cycling, swimming, yoga, custom
    val exerciseName: String,
    val durationMinutes: Int,
    val intensity: String = "moderate", // light, moderate, intense
    val caloriesBurned: Int? = null,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// 7. Mood & Journal Logs
@Entity(tableName = "mood_logs")
data class MoodLogEntity(
    @PrimaryKey val date: String, // yyyy-MM-dd
    val moodScore: Int = 4, // 1: 😔, 2: 😐, 3: 🙂, 4: 😄, 5: 🤩
    val stressLevel: Int = 3, // 1-10
    val triggers: String = "", // Comma-separated (Kerja, Kesehatan, dll)
    val journalText: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// 8. Habit
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String, // Emoji or icon name
    val colorHex: String, // #00BFA5
    val category: String, // health, faith, learning, fitness, social, finance, personal
    val frequency: String = "daily", // daily, specific_days
    val targetDays: String = "0,1,2,3,4,5,6", // 0=Mon..6=Sun
    val reminderTime: String? = null,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// 9. Habit Daily Log
@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey val id: String, // {date}_{habitId}
    val habitId: String,
    val date: String, // yyyy-MM-dd
    val completed: Boolean = true,
    val completedAt: Long = System.currentTimeMillis(),
    val note: String? = null
)

// 10. Task
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val priority: String = "medium", // high, medium, low
    val dueDate: String? = null, // yyyy-MM-dd
    val subTasksJson: String = "[]", // [{"id":"1","title":"Subtask","isCompleted":false}]
    val tags: String = "", // Comma-separated
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// 11. Focus Session
@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val taskTitle: String? = null,
    val durationMinutes: Int = 25,
    val type: String = "focus", // focus, break
    val date: String, // yyyy-MM-dd
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long = System.currentTimeMillis(),
    val completed: Boolean = true
)

// 12. Learning Log
@Entity(tableName = "learning_logs")
data class LearningLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String, // yyyy-MM-dd
    val topic: String,
    val sourceType: String = "book", // book, video, course, article, podcast, other
    val sourceName: String? = null,
    val durationMinutes: Int = 30,
    val notes: String? = null,
    val tags: String = "",
    val progressPercent: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// 13. Goal Tracker
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val category: String = "personal", // career, finance, health, learning, personal, relationship
    val deadline: String? = null,
    val progressPercent: Int = 0, // 0-100
    val milestonesJson: String = "[]", // [{"id":"1","title":"Milestone","isCompleted":false}]
    val status: String = "active", // active, paused, achieved, cancelled
    val reflectionNote: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// 14. User Profile & Settings
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String = "primary_user",
    val displayName: String = "Pengguna",
    val email: String = "user@lifetracker.app",
    val waterGoalGlasses: Int = 8,
    val isDarkMode: Boolean = true,
    val morningReminderTime: String = "06:00",
    val nightReminderTime: String = "21:00"
)

// 15. User Account Authentication
@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val avatarEmoji: String = "👤",
    val isLoggedIn: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
