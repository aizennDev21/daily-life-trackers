package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.utils.DateUtils
import com.example.core.utils.FormatUtils
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.theme.AccentExercise
import com.example.ui.theme.AccentFinance
import com.example.ui.theme.AccentHabit
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentProductivity
import com.example.ui.theme.AccentReports
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun ReportsScreen(
    viewModel: LifeTrackerViewModel,
    onNavigateToAiCoach: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val habits by viewModel.habitsWithStreaks.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val financeSummary by viewModel.financeSummary.collectAsStateWithLifecycle()
    val todaySleep by viewModel.todaySleepLog.collectAsStateWithLifecycle()
    val todayWater by viewModel.todayWaterLog.collectAsStateWithLifecycle()
    val todayMood by viewModel.todayMoodLog.collectAsStateWithLifecycle()
    val exerciseLogs by viewModel.exerciseLogs.collectAsStateWithLifecycle()
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()

    val habitScore = if (habits.isNotEmpty()) (habits.count { it.isCompletedToday }.toFloat() / habits.size * 100).toInt() else 80
    val taskScore = if (tasks.isNotEmpty()) (tasks.count { it.isCompleted }.toFloat() / tasks.size * 100).toInt() else 75
    val waterScore = if ((todayWater?.targetGlasses ?: 8) > 0) ((todayWater?.glassesCount ?: 0).toFloat() / (todayWater?.targetGlasses ?: 8) * 100).coerceAtMost(100f).toInt() else 60
    val sleepScore = ((todaySleep?.quality ?: 4).toFloat() / 5 * 100).toInt()
    val overallLifeScore = ((habitScore + taskScore + waterScore + sleepScore) / 4)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📊 Analisis & Laporan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Statistik & keseimbangan hidup holistik",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                }

                IconButton(
                    onClick = { viewModel.showMessage("Laporan siap dibagikan!") },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariantDark)
                        .border(1.dp, BorderDark, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = BrandPrimaryLight)
                }
            }
        }

        // 1. Overall Life Balance Score Card
        item {
            DLTCard(
                backgroundColor = SurfaceVariantDark,
                borderColor = BrandPrimary.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Indeks Keseimbangan Hidup", fontSize = 13.sp, color = TextSecondaryDark)
                        Text(
                            text = "$overallLifeScore / 100",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimaryLight
                        )
                        Text(
                            text = if (overallLifeScore >= 80) "🌟 Sangat Optimal & Seimbang!" else "👍 Kategori Baik & Terarah",
                            fontSize = 13.sp,
                            color = StatusSuccess,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.2f))
                            .border(2.dp, BrandPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔥", fontSize = 28.sp)
                    }
                }
            }
        }

        // 2. AI Life Coach Prompt Button
        item {
            DLTButton(
                text = "✨ Minta Evaluasi Lengkap AI Coach",
                icon = Icons.Default.AutoAwesome,
                onClick = onNavigateToAiCoach,
                backgroundColor = Color(0xFF53389E)
            )
        }

        // 3. Domain Metric Breakdown Cards
        item {
            Text("Distribusi Pilar Kehidupan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Habit Score
                MetricProgressBar(
                    title = "✅ Konsistensi Kebiasaan (Habits)",
                    score = habitScore,
                    detail = "${habits.count { it.isCompletedToday }} / ${habits.size} habit selesai",
                    color = AccentHabit
                )

                // Productivity / Tasks
                MetricProgressBar(
                    title = "📋 Penyelesaian Tugas (Productivity)",
                    score = taskScore,
                    detail = "${tasks.count { it.isCompleted }} / ${tasks.size} tugas terselesaikan",
                    color = AccentProductivity
                )

                // Hydration
                MetricProgressBar(
                    title = "💧 Asupan Hidrasi Air",
                    score = waterScore,
                    detail = "${todayWater?.glassesCount ?: 0} / ${todayWater?.targetGlasses ?: 8} gelas",
                    color = AccentReports
                )

                // Sleep & Recovery
                MetricProgressBar(
                    title = "😴 Kualitas Tidur & Pemulihan",
                    score = sleepScore,
                    detail = "Durasi: ${FormatUtils.formatDuration(todaySleep?.durationMinutes ?: 450)} (${todaySleep?.quality ?: 4}/5 ★)",
                    color = AccentSleep
                )
            }
        }

        // 4. Financial Health Snapshot
        item {
            DLTCard(backgroundColor = SurfaceDark) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("💰 Arus Kas & Finansial", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pemasukan", fontSize = 13.sp, color = TextSecondaryDark)
                        Text(FormatUtils.formatRupiah(financeSummary.totalIncome), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pengeluaran", fontSize = 13.sp, color = TextSecondaryDark)
                        Text(FormatUtils.formatRupiah(financeSummary.totalExpense), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tabungan / Investasi", fontSize = 13.sp, color = TextSecondaryDark)
                        Text(FormatUtils.formatRupiah(financeSummary.totalSaving), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00B0FF))
                    }
                }
            }
        }

        // 5. Deep Work / Focus Hours
        item {
            DLTCard(backgroundColor = SurfaceDark) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("🍅 Total Deep Work Focus", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                        Text("Sesi Pomodoro yang diselesaikan hari ini", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                    Text(
                        text = "${pomodoroState.totalFocusMinutesToday} Menit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentProductivity
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MetricProgressBar(
    title: String,
    score: Int,
    detail: String,
    color: Color
) {
    DLTCard(backgroundColor = SurfaceDark) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                Text(text = "$score%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            }
            LinearProgressIndicator(
                progress = { (score / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = SurfaceVariantDark
            )
            Text(text = detail, fontSize = 11.sp, color = TextSecondaryDark)
        }
    }
}
