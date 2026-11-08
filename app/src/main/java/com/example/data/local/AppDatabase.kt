package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        ActivityEntity::class,
        TransactionEntity::class,
        SleepLogEntity::class,
        MealLogEntity::class,
        WaterLogEntity::class,
        ExerciseLogEntity::class,
        MoodLogEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        TaskEntity::class,
        FocusSessionEntity::class,
        LearningLogEntity::class,
        GoalEntity::class,
        UserProfileEntity::class,
        UserAccountEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lifeTrackerDao(): LifeTrackerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_life_tracker.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
