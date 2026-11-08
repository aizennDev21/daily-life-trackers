package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.utils.FormatUtils
import com.example.ui.components.CategoryChip
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTTextField
import com.example.ui.components.EmptyStateWidget
import com.example.ui.theme.AccentExercise
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun ExerciseScreen(
    viewModel: LifeTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val exerciseLogs by viewModel.exerciseLogs.collectAsStateWithLifecycle()
    val totalMinutes = exerciseLogs.sumOf { it.durationMinutes }
    val totalCalories = exerciseLogs.sumOf { it.caloriesBurned ?: 0 }

    var isAdding by remember { mutableStateOf(false) }
    var exerciseType by remember { mutableStateOf("running") }
    var name by remember { mutableStateOf("Lari Pagi") }
    var duration by remember { mutableStateOf("30") }
    var intensity by remember { mutableStateOf("moderate") }
    var calories by remember { mutableStateOf("220") }
    var notes by remember { mutableStateOf("") }

    val types = listOf(
        Pair("running", "🏃 Lari"),
        Pair("walking", "🚶 Jalan"),
        Pair("gym", "🏋️ Gym"),
        Pair("cycling", "🚴 Sepeda"),
        Pair("swimming", "🏊 Renang"),
        Pair("yoga", "🧘 Yoga")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                Text(
                    text = "💪 Olahraga & Aktivitas Fisik",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Catat sesi workout, durasi dan kalori terbakar",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }
        }

        // Summary Hero Card
        item {
            DLTCard(
                backgroundColor = SurfaceVariantDark,
                borderColor = AccentExercise.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total Durasi", fontSize = 12.sp, color = TextSecondaryDark)
                        Text(
                            text = FormatUtils.formatDuration(totalMinutes),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(BorderDark))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Kalori Terbakar", fontSize = 12.sp, color = TextSecondaryDark)
                        Text(
                            text = "$totalCalories kcal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentExercise
                        )
                    }
                }
            }
        }

        // Add Button / Form
        if (isAdding) {
            item {
                DLTCard(backgroundColor = SurfaceVariantDark) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Catat Workout Baru", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            types.take(3).forEach { (tKey, label) ->
                                CategoryChip(label = label, isSelected = exerciseType == tKey) {
                                    exerciseType = tKey
                                    name = when (tKey) {
                                        "running" -> "Lari Pagi"
                                        "walking" -> "Jalan Santai"
                                        "gym" -> "Latihan Beban"
                                        else -> "Olahraga"
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            types.drop(3).forEach { (tKey, label) ->
                                CategoryChip(label = label, isSelected = exerciseType == tKey) {
                                    exerciseType = tKey
                                    name = when (tKey) {
                                        "cycling" -> "Gowes Sepeda"
                                        "swimming" -> "Berenang"
                                        "yoga" -> "Yoga & Stretching"
                                        else -> "Olahraga"
                                    }
                                }
                            }
                        }

                        DLTTextField(value = name, onValueChange = { name = it }, label = "Nama Aktivitas")
                        DLTTextField(value = duration, onValueChange = { duration = it.filter { ch -> ch.isDigit() } }, label = "Durasi (menit)")
                        DLTTextField(value = calories, onValueChange = { calories = it.filter { ch -> ch.isDigit() } }, label = "Kalori (kcal)")
                        DLTTextField(value = notes, onValueChange = { notes = it }, label = "Catatan Workout", placeholder = "Misal: 4 set pushup, bench press")

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DLTButton(
                                text = "Simpan",
                                modifier = Modifier.weight(1f),
                                backgroundColor = AccentExercise,
                                onClick = {
                                    viewModel.addExerciseLog(
                                        exerciseType = exerciseType,
                                        exerciseName = name,
                                        durationMinutes = duration.toIntOrNull() ?: 30,
                                        intensity = intensity,
                                        caloriesBurned = calories.toIntOrNull() ?: 200,
                                        notes = notes.ifBlank { null }
                                    )
                                    isAdding = false
                                }
                            )
                            DLTButton(
                                text = "Batal",
                                modifier = Modifier.weight(1f),
                                backgroundColor = SurfaceDark,
                                onClick = { isAdding = false }
                            )
                        }
                    }
                }
            }
        } else {
            item {
                DLTButton(
                    text = "+ Catat Sesi Olahraga",
                    backgroundColor = AccentExercise,
                    onClick = { isAdding = true }
                )
            }
        }

        // Workout Log List
        if (exerciseLogs.isEmpty()) {
            item {
                EmptyStateWidget(
                    emoji = "🏃",
                    title = "Belum Ada Catatan Olahraga",
                    description = "Mulai bergerak hari ini dan catat setiap sesi kebugaranmu."
                )
            }
        } else {
            items(exerciseLogs) { exercise ->
                val emoji = when (exercise.exerciseType) {
                    "running" -> "🏃"
                    "walking" -> "🚶"
                    "gym" -> "🏋️"
                    "cycling" -> "🚴"
                    "swimming" -> "🏊"
                    "yoga" -> "🧘"
                    else -> "💪"
                }

                DLTCard(backgroundColor = SurfaceDark) {
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentExercise.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = exercise.exerciseName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimaryDark)
                                if (!exercise.notes.isNullOrBlank()) {
                                    Text(text = exercise.notes, fontSize = 12.sp, color = TextSecondaryDark)
                                }
                                Text(
                                    text = "⏱️ ${exercise.durationMinutes} menit • 🔥 ${exercise.caloriesBurned ?: 0} kcal",
                                    fontSize = 11.sp,
                                    color = AccentExercise
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteExerciseLog(exercise) }) {
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
