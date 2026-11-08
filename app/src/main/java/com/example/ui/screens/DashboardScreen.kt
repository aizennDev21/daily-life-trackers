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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.utils.DateUtils
import com.example.core.utils.FormatUtils
import com.example.ui.components.DLTCard
import com.example.ui.components.HabitCheckItem
import com.example.ui.components.MoodPicker
import com.example.ui.theme.AccentExercise
import com.example.ui.theme.AccentFinance
import com.example.ui.theme.AccentFood
import com.example.ui.theme.AccentHabit
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentProductivity
import com.example.ui.theme.AccentReports
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun DashboardScreen(
    viewModel: LifeTrackerViewModel,
    onNavigateToTab: (Int) -> Unit,
    onNavigateToAiCoach: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val financeSummary by viewModel.financeSummary.collectAsStateWithLifecycle()
    val habits by viewModel.habitsWithStreaks.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val todaySleep by viewModel.todaySleepLog.collectAsStateWithLifecycle()
    val todayWater by viewModel.todayWaterLog.collectAsStateWithLifecycle()
    val todayMood by viewModel.todayMoodLog.collectAsStateWithLifecycle()
    val exerciseLogs by viewModel.exerciseLogs.collectAsStateWithLifecycle()

    val userName = activeUser?.fullName ?: userProfile?.displayName ?: "Pengguna"
    val greeting = DateUtils.getGreeting()
    val greetingEmoji = DateUtils.getGreetingEmoji()
    val todayDateFormatted = DateUtils.formatFullDateIndonesian()

    val completedHabitsCount = habits.count { it.isCompletedToday }
    val totalHabitsCount = habits.size
    val habitProgress = if (totalHabitsCount > 0) completedHabitsCount.toFloat() / totalHabitsCount else 0f
    val pendingTasks = tasks.filter { !it.isCompleted }.take(3)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // 1. Header & Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting, $userName! $greetingEmoji",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todayDateFormatted,
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                }

                IconButton(
                    onClick = { viewModel.showMessage("Semua pengingat aktif") },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariantDark)
                        .border(1.dp, BorderDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = BrandPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. AI Life Coach Card Banner (Gemini 3.1 Pro High Thinking)
        item {
            DLTCard(
                backgroundColor = Color(0xFF1B1834),
                borderColor = BrandPrimary.copy(alpha = 0.6f),
                onClick = onNavigateToAiCoach
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandPrimary.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BrandPrimaryLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI Life Coach",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BrandPrimaryLight
                            )
                            Text(
                                text = "Evaluasi keseimbangan & insight hidup hari ini",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Buka Coach",
                        tint = BrandPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3. Mood Hari Ini Quick Pick
        item {
            DLTCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "😄 Mood Hari Ini?",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                        if (todayMood != null) {
                            Text(
                                text = "Tercatat ✨",
                                fontSize = 12.sp,
                                color = StatusSuccess,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    MoodPicker(
                        selectedScore = todayMood?.moodScore ?: 0,
                        onMoodSelected = { score ->
                            viewModel.saveMood(
                                moodScore = score,
                                stressLevel = todayMood?.stressLevel ?: 3,
                                triggers = todayMood?.triggers ?: "",
                                journalText = todayMood?.journalText
                            )
                        }
                    )
                }
            }
        }

        // 4. Ringkasan Keuangan Card
        item {
            DLTCard(
                onClick = { onNavigateToTab(2) } // Navigate to Finance
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💰", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Keuangan Hari Ini",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextPrimaryDark
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Keuangan",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Saldo Bersih",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                        Text(
                            text = FormatUtils.formatRupiah(financeSummary.balance),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${FormatUtils.formatRupiah(financeSummary.totalIncome)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusSuccess
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TrendingDown, contentDescription = null, tint = StatusError, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "-${FormatUtils.formatRupiah(financeSummary.totalExpense)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusError
                            )
                        }
                    }
                }
            }
        }

        // 5. Habit Progress Card & Quick Checks
        item {
            DLTCard(
                onClick = { onNavigateToTab(3) } // Habit tab
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✅", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Habit ($completedHabitsCount/$totalHabitsCount selesai)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextPrimaryDark
                            )
                        }
                        Text(
                            text = "${(habitProgress * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentHabit
                        )
                    }

                    val animatedProgress by animateFloatAsState(targetValue = habitProgress, label = "prog")
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AccentHabit,
                        trackColor = SurfaceVariantDark
                    )
                }
            }
        }

        // 6. Quick Habit Items (first 3)
        items(habits.take(3)) { item ->
            HabitCheckItem(
                name = item.habit.name,
                icon = item.habit.icon,
                streak = item.currentStreak,
                isCompleted = item.isCompletedToday,
                onToggle = { viewModel.toggleHabit(item.habit.id, item.isCompletedToday) }
            )
        }

        // 7. Pending Tasks
        item {
            DLTCard(
                onClick = { onNavigateToTab(4) } // More / Productivity tab
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📋", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tugas Hari Ini (${pendingTasks.size} pending)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextPrimaryDark
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Tugas",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (pendingTasks.isEmpty()) {
                        Text(
                            text = "Semua tugas hari ini selesai! 🎉",
                            fontSize = 13.sp,
                            color = StatusSuccess
                        )
                    } else {
                        pendingTasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceVariantDark)
                                    .clickable { viewModel.toggleTask(task) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val dotColor = when (task.priority) {
                                        "high" -> StatusError
                                        "medium" -> Color(0xFFFFD740)
                                        else -> StatusSuccess
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = task.title,
                                        fontSize = 13.sp,
                                        color = TextPrimaryDark,
                                        maxLines = 1
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, TextSecondaryDark, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {}
                            }
                        }
                    }
                }
            }
        }

        // 8. Health & Daily Stats Grid (Tidur, Air, Olahraga)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sleep Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌙", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Tidur", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val duration = todaySleep?.durationMinutes ?: 450
                        Text(
                            text = FormatUtils.formatDuration(duration),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentSleep
                        )
                        Text(
                            text = "★★★★☆ (${todaySleep?.quality ?: 4}/5)",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )
                    }
                }

                // Water Card with interactive +1 button
                val glasses = todayWater?.glassesCount ?: 0
                val target = todayWater?.targetGlasses ?: 8
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💧", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Air", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AccentReports.copy(alpha = 0.2f))
                                    .clickable { viewModel.incrementWater() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "+1", tint = AccentReports, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$glasses/$target gelas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentReports
                        )
                        Text(
                            text = if (glasses >= target) "Target tercapai 🎉" else "Kurang ${target - glasses} gelas",
                            fontSize = 11.sp,
                            color = if (glasses >= target) StatusSuccess else TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Motivational Daily Quote
        item {
            DLTCard(
                backgroundColor = SurfaceVariantDark,
                borderColor = BorderDark
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "\"Disiplin harian adalah jembatan antara tujuan dan pencapaian nyata.\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondaryDark
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom bar & FAB
        }
    }
}
