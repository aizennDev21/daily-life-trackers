package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.ui.components.DLTCard
import com.example.ui.components.EmptyStateWidget
import com.example.ui.components.HabitCheckItem
import com.example.ui.theme.AccentHabit
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.HabitWithStreak
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun HabitScreen(
    viewModel: LifeTrackerViewModel,
    onOpenQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habitsWithStreaks.collectAsStateWithLifecycle()

    val completedCount = habits.count { it.isCompletedToday }
    val totalCount = habits.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val past7Days = DateUtils.getPastDays(7).reversed()

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
                    text = "✅ Habit Tracker",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Bangun & pantau konsistensi kebiasaan positif",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }

            IconButton(
                onClick = onOpenQuickAdd,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AccentHabit)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Habit", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Summary Card
        DLTCard(
            backgroundColor = SurfaceVariantDark,
            borderColor = AccentHabit.copy(alpha = 0.5f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Progres Hari Ini", fontSize = 13.sp, color = TextSecondaryDark)
                        Text(
                            text = "$completedCount dari $totalCount Selesai",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentHabit
                    )
                }

                val animatedProgress by animateFloatAsState(targetValue = progress, label = "prog")
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AccentHabit,
                    trackColor = SurfaceDark
                )

                // 7-day mini heatmap header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    past7Days.forEach { dayId ->
                        val dayShort = DateUtils.getDayOfWeekShort(dayId)
                        val isToday = dayId == viewModel.todayDateId
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayShort,
                                fontSize = 10.sp,
                                color = if (isToday) AccentHabit else TextSecondaryDark,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isToday && completedCount > 0) AccentHabit else SurfaceDark)
                                    .border(1.dp, if (isToday) AccentHabit else BorderDark, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Habit Items
        if (habits.isEmpty()) {
            EmptyStateWidget(
                emoji = "🌱",
                title = "Belum Ada Kebiasaan",
                description = "Tambahkan kebiasaan harian baru untuk mulai membangun streak disiplinmu.",
                buttonText = "+ Buat Habit Baru",
                onButtonClick = onOpenQuickAdd
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(habits) { item ->
                    HabitCheckItem(
                        name = item.habit.name,
                        icon = item.habit.icon,
                        streak = item.currentStreak,
                        isCompleted = item.isCompletedToday,
                        onToggle = { viewModel.toggleHabit(item.habit.id, item.isCompletedToday) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
