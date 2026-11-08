package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.utils.FormatUtils
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.LearningLogEntity
import com.example.data.local.entities.TaskEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTOutlinedButton
import com.example.ui.components.DLTTextField
import com.example.ui.components.EmptyStateWidget
import com.example.ui.theme.AccentLearning
import com.example.ui.theme.AccentProductivity
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun ProductivityScreen(
    viewModel: LifeTrackerViewModel,
    onOpenQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("📋 Tasks", "🍅 Focus", "📚 Learn", "🎯 Goals")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "⚡ Produktivitas & Fokus",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Tugas harian, Pomodoro, belajar & goal tracking",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }

            IconButton(
                onClick = onOpenQuickAdd,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AccentProductivity)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = AccentProductivity,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AccentProductivity
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) AccentProductivity else TextSecondaryDark
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content
        when (selectedTab) {
            0 -> TasksTab(viewModel = viewModel, onAddTask = onOpenQuickAdd)
            1 -> FocusTimerTab(viewModel = viewModel)
            2 -> LearningTab(viewModel = viewModel)
            3 -> GoalsTab(viewModel = viewModel)
        }
    }
}

// 1. Tasks Tab
@Composable
fun TasksTab(viewModel: LifeTrackerViewModel, onAddTask: () -> Unit) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            DLTCard(backgroundColor = SurfaceVariantDark) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Task Completion Rate", fontSize = 12.sp, color = TextSecondaryDark)
                        Text(
                            text = "$completedCount/$totalCount tugas selesai",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (progress >= 0.7f) StatusSuccess else AccentProductivity
                    )
                }
            }
        }

        if (tasks.isEmpty()) {
            item {
                EmptyStateWidget(
                    emoji = "📋",
                    title = "Belum Ada Tugas",
                    description = "Mulai hari dengan mencatat daftar tugas pentingmu.",
                    buttonText = "+ Tambah Tugas",
                    onButtonClick = onAddTask
                )
            }
        } else {
            items(tasks) { task ->
                TaskItemCard(
                    task = task,
                    onToggle = { viewModel.toggleTask(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        "high" -> StatusError
        "medium" -> Color(0xFFFFD740)
        else -> StatusSuccess
    }

    DLTCard(
        backgroundColor = if (task.isCompleted) SurfaceVariantDark.copy(alpha = 0.6f) else SurfaceDark,
        borderColor = if (task.isCompleted) BorderDark else priorityColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) AccentProductivity else Color.Transparent)
                        .border(
                            2.dp,
                            if (task.isCompleted) AccentProductivity else TextSecondaryDark,
                            CircleShape
                        )
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (task.isCompleted) TextSecondaryDark else TextPrimaryDark,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (!task.description.isNullOrBlank()) {
                        Text(
                            text = task.description,
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            maxLines = 1
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(priorityColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.priority.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            )
                        }
                        if (task.tags.isNotBlank()) {
                            Text(text = "• ${task.tags}", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = TextSecondaryDark.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

// 2. Focus Timer (Pomodoro) Tab
@Composable
fun FocusTimerTab(viewModel: LifeTrackerViewModel) {
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()
    val totalTime = if (pomodoroState.isBreak) 5 * 60 else 25 * 60
    val progress = (totalTime - pomodoroState.secondsRemaining).toFloat() / totalTime

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode indicator pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (pomodoroState.isBreak) AccentSleep.copy(alpha = 0.2f) else AccentProductivity.copy(alpha = 0.2f))
                .border(1.dp, if (pomodoroState.isBreak) AccentSleep else AccentProductivity, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (pomodoroState.isBreak) "☕ Sesi Istirahat (Break)" else "🍅 Sesi Fokus (Deep Work)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (pomodoroState.isBreak) AccentSleep else AccentProductivity
            )
        }

        // Circular Timer Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(210.dp),
                color = if (pomodoroState.isBreak) AccentSleep else AccentProductivity,
                trackColor = SurfaceVariantDark,
                strokeWidth = 10.dp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = FormatUtils.formatSecondsToTimer(pomodoroState.secondsRemaining),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = if (pomodoroState.isRunning) "Berjalan..." else "Siap",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }
        }

        // Timer Control Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.resetPomodoro(pomodoroState.isBreak) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariantDark)
                    .border(1.dp, BorderDark, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = TextPrimaryDark)
            }

            // Main Play/Pause Button
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(if (pomodoroState.isBreak) AccentSleep else AccentProductivity)
                    .clickable {
                        if (pomodoroState.isRunning) viewModel.pausePomodoro()
                        else viewModel.startPomodoro()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (pomodoroState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (pomodoroState.isRunning) "Pause" else "Start",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Switch to Break / Focus
            IconButton(
                onClick = { viewModel.resetPomodoro(!pomodoroState.isBreak) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariantDark)
                    .border(1.dp, BorderDark, CircleShape)
            ) {
                Text(text = if (pomodoroState.isBreak) "🍅" else "☕", fontSize = 18.sp)
            }
        }

        // Daily Focus Stats Card
        DLTCard(backgroundColor = SurfaceDark) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Sesi Selesai", fontSize = 12.sp, color = TextSecondaryDark)
                    Text(
                        text = "${pomodoroState.sessionsCompletedToday} 🍅",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(36.dp).background(BorderDark))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Total Waktu Fokus", fontSize = 12.sp, color = TextSecondaryDark)
                    Text(
                        text = "${pomodoroState.totalFocusMinutesToday} menit",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentProductivity
                    )
                }
            }
        }
    }
}

// 3. Learning Log Tab
@Composable
fun LearningTab(viewModel: LifeTrackerViewModel) {
    val learningLogs by viewModel.learningLogs.collectAsStateWithLifecycle()
    var isAdding by remember { mutableStateOf(false) }
    var topic by remember { mutableStateOf("") }
    var sourceName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("45") }
    var notes by remember { mutableStateOf("") }
    var sourceType by remember { mutableStateOf("book") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (isAdding) {
            item {
                DLTCard(backgroundColor = SurfaceVariantDark) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Catat Sesi Belajar Baru", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        DLTTextField(value = topic, onValueChange = { topic = it }, label = "Topik / Skill", placeholder = "Contoh: Kotlin Coroutines & Flow")
                        DLTTextField(value = sourceName, onValueChange = { sourceName = it }, label = "Sumber (Buku / Video / Kursus)", placeholder = "Contoh: Official Docs")
                        DLTTextField(value = duration, onValueChange = { duration = it }, label = "Durasi (menit)", placeholder = "45")
                        DLTTextField(value = notes, onValueChange = { notes = it }, label = "Ringkasan Materi", placeholder = "Poin penting yang dipelajari...")

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DLTButton(
                                text = "Simpan",
                                modifier = Modifier.weight(1f),
                                backgroundColor = AccentLearning,
                                onClick = {
                                    if (topic.isNotBlank()) {
                                        viewModel.addLearningLog(
                                            topic = topic,
                                            sourceType = sourceType,
                                            sourceName = sourceName.ifBlank { null },
                                            durationMinutes = duration.toIntOrNull() ?: 30,
                                            notes = notes.ifBlank { null },
                                            tags = "Learning"
                                        )
                                        isAdding = false
                                        topic = ""
                                        sourceName = ""
                                        notes = ""
                                    }
                                }
                            )
                            DLTOutlinedButton(
                                text = "Batal",
                                modifier = Modifier.weight(1f),
                                onClick = { isAdding = false }
                            )
                        }
                    }
                }
            }
        } else {
            item {
                DLTButton(
                    text = "+ Catat Belajar Hari Ini",
                    backgroundColor = AccentLearning,
                    onClick = { isAdding = true }
                )
            }
        }

        if (learningLogs.isEmpty()) {
            item {
                EmptyStateWidget(
                    emoji = "📚",
                    title = "Belum Ada Catatan Belajar",
                    description = "Tuliskan materi atau skill baru yang kamu pelajari hari ini."
                )
            }
        } else {
            items(learningLogs) { log ->
                DLTCard(backgroundColor = SurfaceDark) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = log.topic, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                            if (!log.sourceName.isNullOrBlank()) {
                                Text(text = "📖 ${log.sourceName}", fontSize = 12.sp, color = AccentLearning)
                            }
                            if (!log.notes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = log.notes, fontSize = 13.sp, color = TextSecondaryDark)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "⏱️ ${log.durationMinutes} menit • ${log.date}", fontSize = 11.sp, color = TextSecondaryDark.copy(alpha = 0.7f))
                        }
                        IconButton(onClick = { viewModel.deleteLearningLog(log) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = TextSecondaryDark.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// 4. Goal Tracker Tab
@Composable
fun GoalsTab(viewModel: LifeTrackerViewModel) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    var isAdding by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("health") }
    var deadline by remember { mutableStateOf("2026-12-31") }
    var description by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (isAdding) {
            item {
                DLTCard(backgroundColor = SurfaceVariantDark) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Buat Goal Baru", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        DLTTextField(value = title, onValueChange = { title = it }, label = "Judul Goal", placeholder = "Contoh: Lari Marathon 10K")
                        DLTTextField(value = description, onValueChange = { description = it }, label = "Deskripsi / Target", placeholder = "Rencana aksi...")
                        DLTTextField(value = deadline, onValueChange = { deadline = it }, label = "Target Tanggal", placeholder = "yyyy-MM-dd")

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DLTButton(
                                text = "Simpan Goal",
                                modifier = Modifier.weight(1f),
                                backgroundColor = BrandPrimary,
                                onClick = {
                                    if (title.isNotBlank()) {
                                        viewModel.addGoal(title = title, category = category, deadline = deadline, description = description.ifBlank { null })
                                        isAdding = false
                                        title = ""
                                        description = ""
                                    }
                                }
                            )
                            DLTOutlinedButton(
                                text = "Batal",
                                modifier = Modifier.weight(1f),
                                onClick = { isAdding = false }
                            )
                        }
                    }
                }
            }
        } else {
            item {
                DLTButton(
                    text = "+ Buat Goal Baru",
                    backgroundColor = BrandPrimary,
                    onClick = { isAdding = true }
                )
            }
        }

        if (goals.isEmpty()) {
            item {
                EmptyStateWidget(
                    emoji = "🎯",
                    title = "Belum Ada Goal",
                    description = "Tetapkan target jangka pendek & menengah untuk memotivasi harimu."
                )
            }
        } else {
            items(goals) { goal ->
                GoalItemCard(
                    goal = goal,
                    onProgressChanged = { newProg -> viewModel.updateGoalProgress(goal, newProg) },
                    onDelete = { viewModel.deleteGoal(goal) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun GoalItemCard(
    goal: GoalEntity,
    onProgressChanged: (Int) -> Unit,
    onDelete: () -> Unit
) {
    DLTCard(
        backgroundColor = SurfaceDark,
        borderColor = if (goal.progressPercent >= 100) StatusSuccess.copy(alpha = 0.5f) else BorderDark
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = goal.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                    if (!goal.description.isNullOrBlank()) {
                        Text(text = goal.description, fontSize = 12.sp, color = TextSecondaryDark)
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = TextSecondaryDark.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (goal.deadline != null) "📅 Target: ${goal.deadline}" else "Status: ${goal.status}",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
                Text(
                    text = "${goal.progressPercent}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (goal.progressPercent >= 100) StatusSuccess else BrandPrimary
                )
            }

            Slider(
                value = goal.progressPercent.toFloat(),
                onValueChange = { onProgressChanged(it.toInt()) },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = BrandPrimary,
                    activeTrackColor = BrandPrimary,
                    inactiveTrackColor = SurfaceVariantDark
                )
            )
        }
    }
}
