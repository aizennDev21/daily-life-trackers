package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.utils.DateUtils
import com.example.data.gemini.GeminiLifeCoachService
import com.example.data.local.AppDatabase
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
import com.example.data.repository.LifeTrackerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HabitWithStreak(
    val habit: HabitEntity,
    val isCompletedToday: Boolean,
    val currentStreak: Int
)

data class FinanceSummary(
    val balance: Long = 0,
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val totalSaving: Long = 0
)

data class PomodoroState(
    val isRunning: Boolean = false,
    val isBreak: Boolean = false,
    val secondsRemaining: Int = 25 * 60, // 25 minutes default
    val totalFocusMinutesToday: Int = 0,
    val sessionsCompletedToday: Int = 0
)

class LifeTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LifeTrackerRepository
    private val geminiService = GeminiLifeCoachService()

    val todayDateId: String = DateUtils.todayId()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LifeTrackerRepository(db.lifeTrackerDao())
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    // --- State Flows from DB ---
    val activeUser: StateFlow<UserAccountEntity?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activities: StateFlow<List<ActivityEntity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepLogs: StateFlow<List<SleepLogEntity>> = repository.allSleepLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaySleepLog: StateFlow<SleepLogEntity?> = repository.getSleepLog(todayDateId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayMealLogs: StateFlow<List<MealLogEntity>> = repository.getMealLogsByDate(todayDateId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWaterLog: StateFlow<WaterLogEntity?> = repository.getWaterLog(todayDateId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val exerciseLogs: StateFlow<List<ExerciseLogEntity>> = repository.allExerciseLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMoodLog: StateFlow<MoodLogEntity?> = repository.getMoodLog(todayDateId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val moodLogs: StateFlow<List<MoodLogEntity>> = repository.allMoodLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.allFocusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val learningLogs: StateFlow<List<LearningLogEntity>> = repository.allLearningLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Computed Habits with Streaks ---
    val habitsWithStreaks: StateFlow<List<HabitWithStreak>> = combine(
        repository.allHabits,
        repository.allHabitLogs
    ) { habitsList, logsList ->
        habitsList.map { habit ->
            val habitLogs = logsList.filter { it.habitId == habit.id && it.completed }
            val completedToday = habitLogs.any { it.date == todayDateId }

            // Calculate streak count
            val pastDays = DateUtils.getPastDays(30)
            var streak = 0
            for (day in pastDays) {
                if (habitLogs.any { it.date == day }) {
                    streak++
                } else if (day == todayDateId) {
                    // today not yet checked, continue checking previous days
                    continue
                } else {
                    break
                }
            }
            HabitWithStreak(
                habit = habit,
                isCompletedToday = completedToday,
                currentStreak = streak
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Computed Finance Summary ---
    val financeSummary: StateFlow<FinanceSummary> = transactions.combine(transactions) { txList, _ ->
        var income = 0L
        var expense = 0L
        var saving = 0L
        for (tx in txList) {
            when (tx.type) {
                "income" -> income += tx.amount
                "expense" -> expense += tx.amount
                "saving" -> saving += tx.amount
            }
        }
        val balance = income - expense
        FinanceSummary(
            balance = balance,
            totalIncome = income,
            totalExpense = expense,
            totalSaving = saving
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinanceSummary())

    // --- Pomodoro Focus Timer State ---
    private val _pomodoroState = MutableStateFlow(PomodoroState())
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()

    private var timerJob: Job? = null

    // --- AI Life Coach State ---
    private val _aiCoachThinking = MutableStateFlow(false)
    val aiCoachThinking: StateFlow<Boolean> = _aiCoachThinking.asStateFlow()

    private val _aiCoachResponse = MutableStateFlow<String?>(null)
    val aiCoachResponse: StateFlow<String?> = _aiCoachResponse.asStateFlow()

    // --- Toast / Snackbar Message ---
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // --- Actions: Activity ---
    fun addActivity(
        title: String,
        category: String,
        description: String? = null,
        emoji: String? = null,
        location: String? = null,
        durationMinutes: Int? = null
    ) {
        viewModelScope.launch {
            repository.addActivity(
                ActivityEntity(
                    title = title,
                    category = category,
                    description = description,
                    emoji = emoji,
                    location = location,
                    durationMinutes = durationMinutes
                )
            )
            showMessage("Aktivitas berhasil dicatat!")
        }
    }

    fun deleteActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
            showMessage("Aktivitas dihapus")
        }
    }

    // --- Actions: Finance ---
    fun addTransaction(
        type: String,
        amount: Long,
        category: String,
        note: String? = null,
        isImportant: Boolean = false
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    note = note,
                    isImportant = isImportant
                )
            )
            showMessage("Transaksi berhasil disimpan!")
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            showMessage("Transaksi dihapus")
        }
    }

    // --- Actions: Habit ---
    fun toggleHabit(habitId: String, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, todayDateId, !currentCompleted)
        }
    }

    fun addHabit(
        name: String,
        icon: String,
        colorHex: String,
        category: String,
        frequency: String = "daily"
    ) {
        viewModelScope.launch {
            repository.addHabit(
                HabitEntity(
                    name = name,
                    icon = icon,
                    colorHex = colorHex,
                    category = category,
                    frequency = frequency
                )
            )
            showMessage("Habit baru berhasil ditambahkan!")
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            showMessage("Habit dihapus")
        }
    }

    // --- Actions: Tasks ---
    fun addTask(
        title: String,
        description: String? = null,
        priority: String = "medium",
        dueDate: String? = todayDateId,
        tags: String = ""
    ) {
        viewModelScope.launch {
            repository.addTask(
                TaskEntity(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    tags = tags
                )
            )
            showMessage("Tugas berhasil ditambahkan!")
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            showMessage("Tugas dihapus")
        }
    }

    // --- Actions: Water ---
    fun incrementWater() {
        viewModelScope.launch {
            val current = todayWaterLog.value ?: WaterLogEntity(date = todayDateId, glassesCount = 0)
            val updated = current.copy(glassesCount = current.glassesCount + 1)
            repository.saveWaterLog(updated)
            showMessage("Minum air +1 gelas 💧 (${updated.glassesCount}/${updated.targetGlasses})")
        }
    }

    fun decrementWater() {
        viewModelScope.launch {
            val current = todayWaterLog.value ?: return@launch
            if (current.glassesCount > 0) {
                val updated = current.copy(glassesCount = current.glassesCount - 1)
                repository.saveWaterLog(updated)
            }
        }
    }

    // --- Actions: Sleep & Health ---
    fun saveSleepLog(
        bedtime: String,
        wakeTime: String,
        durationMinutes: Int,
        quality: Int,
        dreamNote: String? = null,
        bodyCondition: String = "healthy",
        weightKg: Float? = null,
        symptomsNote: String? = null
    ) {
        viewModelScope.launch {
            repository.saveSleepLog(
                SleepLogEntity(
                    date = todayDateId,
                    bedtime = bedtime,
                    wakeTime = wakeTime,
                    durationMinutes = durationMinutes,
                    quality = quality,
                    dreamNote = dreamNote,
                    bodyCondition = bodyCondition,
                    weightKg = weightKg,
                    symptomsNote = symptomsNote
                )
            )
            showMessage("Catatan tidur & kesehatan tersimpan!")
        }
    }

    // --- Actions: Meals ---
    fun addMealLog(
        mealType: String,
        foodName: String,
        calories: Int? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            repository.addMealLog(
                MealLogEntity(
                    date = todayDateId,
                    mealType = mealType,
                    foodName = foodName,
                    caloriesEstimate = calories,
                    notes = notes
                )
            )
            showMessage("Catatan makan disimpan 🍽️")
        }
    }

    fun deleteMealLog(meal: MealLogEntity) {
        viewModelScope.launch {
            repository.deleteMealLog(meal)
        }
    }

    // --- Actions: Exercise ---
    fun addExerciseLog(
        exerciseType: String,
        exerciseName: String,
        durationMinutes: Int,
        intensity: String,
        caloriesBurned: Int? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            repository.addExerciseLog(
                ExerciseLogEntity(
                    date = todayDateId,
                    exerciseType = exerciseType,
                    exerciseName = exerciseName,
                    durationMinutes = durationMinutes,
                    intensity = intensity,
                    caloriesBurned = caloriesBurned,
                    notes = notes
                )
            )
            showMessage("Sesi olahraga berhasil dicatat 💪")
        }
    }

    fun deleteExerciseLog(exercise: ExerciseLogEntity) {
        viewModelScope.launch {
            repository.deleteExerciseLog(exercise)
        }
    }

    // --- Actions: Mood & Journal ---
    fun saveMood(
        moodScore: Int,
        stressLevel: Int,
        triggers: String,
        journalText: String?
    ) {
        viewModelScope.launch {
            repository.saveMoodLog(
                MoodLogEntity(
                    date = todayDateId,
                    moodScore = moodScore,
                    stressLevel = stressLevel,
                    triggers = triggers,
                    journalText = journalText
                )
            )
            showMessage("Jurnal & mood hari ini tersimpan ✨")
        }
    }

    // --- Actions: Pomodoro Timer ---
    fun startPomodoro() {
        if (_pomodoroState.value.isRunning) return
        _pomodoroState.value = _pomodoroState.value.copy(isRunning = true)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_pomodoroState.value.isRunning && _pomodoroState.value.secondsRemaining > 0) {
                delay(1000)
                val newSec = _pomodoroState.value.secondsRemaining - 1
                _pomodoroState.value = _pomodoroState.value.copy(secondsRemaining = newSec)
            }

            if (_pomodoroState.value.secondsRemaining <= 0) {
                val wasBreak = _pomodoroState.value.isBreak
                if (!wasBreak) {
                    // Completed focus session
                    repository.addFocusSession(
                        FocusSessionEntity(
                            taskTitle = "Sesi Fokus Pomodoro",
                            durationMinutes = 25,
                            type = "focus",
                            date = todayDateId
                        )
                    )
                    _pomodoroState.value = _pomodoroState.value.copy(
                        isRunning = false,
                        isBreak = true,
                        secondsRemaining = 5 * 60,
                        sessionsCompletedToday = _pomodoroState.value.sessionsCompletedToday + 1,
                        totalFocusMinutesToday = _pomodoroState.value.totalFocusMinutesToday + 25
                    )
                    showMessage("🍅 Sesi fokus selesai! Waktunya istirahat 5 menit.")
                } else {
                    _pomodoroState.value = _pomodoroState.value.copy(
                        isRunning = false,
                        isBreak = false,
                        secondsRemaining = 25 * 60
                    )
                    showMessage("Waktu istirahat selesai. Siap mulai fokus lagi!")
                }
            }
        }
    }

    fun pausePomodoro() {
        timerJob?.cancel()
        _pomodoroState.value = _pomodoroState.value.copy(isRunning = false)
    }

    fun resetPomodoro(isBreak: Boolean = false) {
        timerJob?.cancel()
        val duration = if (isBreak) 5 * 60 else 25 * 60
        _pomodoroState.value = _pomodoroState.value.copy(
            isRunning = false,
            isBreak = isBreak,
            secondsRemaining = duration
        )
    }

    // --- Actions: Learning & Goals ---
    fun addLearningLog(
        topic: String,
        sourceType: String,
        sourceName: String?,
        durationMinutes: Int,
        notes: String?,
        tags: String
    ) {
        viewModelScope.launch {
            repository.addLearningLog(
                LearningLogEntity(
                    date = todayDateId,
                    topic = topic,
                    sourceType = sourceType,
                    sourceName = sourceName,
                    durationMinutes = durationMinutes,
                    notes = notes,
                    tags = tags
                )
            )
            showMessage("Catatan belajar disimpan 📚")
        }
    }

    fun deleteLearningLog(log: LearningLogEntity) {
        viewModelScope.launch {
            repository.deleteLearningLog(log)
        }
    }

    fun addGoal(
        title: String,
        category: String,
        deadline: String?,
        description: String? = null
    ) {
        viewModelScope.launch {
            repository.addGoal(
                GoalEntity(
                    title = title,
                    category = category,
                    deadline = deadline,
                    description = description,
                    progressPercent = 0
                )
            )
            showMessage("Goal baru dibuat 🎯")
        }
    }

    fun updateGoalProgress(goal: GoalEntity, newProgress: Int) {
        viewModelScope.launch {
            val status = if (newProgress >= 100) "achieved" else goal.status
            repository.updateGoal(goal.copy(progressPercent = newProgress, status = status))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // --- Actions: AI Life Coach (Gemini 3.1 Pro High Thinking) ---
    fun requestAiLifeCoach(userPrompt: String? = null) {
        viewModelScope.launch {
            _aiCoachThinking.value = true
            val name = userProfile.value?.displayName ?: "Aizen"

            // Construct daily context for Gemini
            val mood = todayMoodLog.value?.let { "Mood: ${it.moodScore}/5, Stress: ${it.stressLevel}/10, Jurnal: ${it.journalText}" } ?: "Mood belum diisi"
            val water = todayWaterLog.value?.let { "Air: ${it.glassesCount}/${it.targetGlasses} gelas" } ?: "Air: 0 gelas"
            val sleep = todaySleepLog.value?.let { "Tidur: ${it.bedtime} s/d ${it.wakeTime} (${it.durationMinutes} menit, Kualitas: ${it.quality}/5)" } ?: "Tidur: belum dicatat"
            val habitSummary = "${habitsWithStreaks.value.count { it.isCompletedToday }}/${habitsWithStreaks.value.size} habit selesai hari ini"
            val taskSummary = "${tasks.value.count { it.isCompleted }}/${tasks.value.size} task selesai"
            val finance = "Saldo: ${financeSummary.value.balance}, Pengeluaran: ${financeSummary.value.totalExpense}, Pemasukan: ${financeSummary.value.totalIncome}"

            val summaryText = """
                Pengguna: $name
                Tanggal: $todayDateId
                Status Tidur: $sleep
                Status Hidrasi: $water
                Status Mood: $mood
                Status Kebiasaan: $habitSummary
                Status Tugas: $taskSummary
                Ringkasan Finansial: $finance
            """.trimIndent()

            val result = geminiService.getDailyLifeAnalysis(
                userName = name,
                dailySummaryText = summaryText,
                userPrompt = userPrompt
            )

            _aiCoachResponse.value = result.getOrNull()
            _aiCoachThinking.value = false
        }
    }

    fun updateProfile(name: String, email: String, isDark: Boolean) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.saveUserProfile(
                current.copy(
                    displayName = name,
                    email = email,
                    isDarkMode = isDark
                )
            )
            showMessage("Profil berhasil diperbarui!")
        }
    }

    // --- Authentication Actions ---
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun clearAuthError() {
        _authError.value = null
    }

    fun login(email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.loginUser(email, password)
            _isAuthLoading.value = false
            result.onSuccess { user ->
                showMessage("Selamat datang kembali, ${user.fullName}!")
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message ?: "Gagal masuk. Periksa kembali email dan password."
            }
        }
    }

    fun register(fullName: String, email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.registerUser(fullName, email, password)
            _isAuthLoading.value = false
            result.onSuccess { user ->
                showMessage("Akun berhasil dibuat! Selamat datang, ${user.fullName}.")
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message ?: "Gagal mendaftar. Silakan coba lagi."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
            showMessage("Anda telah keluar.")
        }
    }

    fun setOnboardingCompleted(userId: String) {
        viewModelScope.launch {
            repository.setOnboardingCompleted(userId)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            showMessage("Semua data berhasil dibersihkan.")
        }
    }
}
