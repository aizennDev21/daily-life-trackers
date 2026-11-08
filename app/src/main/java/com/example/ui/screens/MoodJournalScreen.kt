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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.CategoryChip
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTTextField
import com.example.ui.components.MoodPicker
import com.example.ui.theme.AccentMood
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun MoodJournalScreen(
    viewModel: LifeTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val todayMood by viewModel.todayMoodLog.collectAsStateWithLifecycle()
    val allMoods by viewModel.moodLogs.collectAsStateWithLifecycle()

    var selectedMood by remember(todayMood) { mutableIntStateOf(todayMood?.moodScore ?: 4) }
    var stressLevel by remember(todayMood) { mutableFloatStateOf(todayMood?.stressLevel?.toFloat() ?: 3f) }
    var selectedTrigger by remember(todayMood) { mutableStateOf(todayMood?.triggers ?: "Kerja") }
    var journalText by remember(todayMood) { mutableStateOf(todayMood?.journalText ?: "") }

    val triggers = listOf("Kerja", "Belajar", "Keluarga", "Kesehatan", "Finansial", "Sosial", "Cuaca")

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
                    text = "😊 Mood & Journal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Refleksi suasana hati, tingkat stres dan jurnal harian",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }
        }

        // Mood & Journal Input Card
        item {
            DLTCard(
                backgroundColor = SurfaceVariantDark,
                borderColor = AccentMood.copy(alpha = 0.5f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Bagaimana perasaanmu hari ini?", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)

                    MoodPicker(
                        selectedScore = selectedMood,
                        onMoodSelected = { selectedMood = it }
                    )

                    // Stress Level Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tingkat Stres", fontSize = 13.sp, color = TextSecondaryDark)
                        Text(
                            text = "${stressLevel.toInt()}/10",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (stressLevel > 6) StatusWarning else AccentMood
                        )
                    }

                    Slider(
                        value = stressLevel,
                        onValueChange = { stressLevel = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentMood,
                            activeTrackColor = AccentMood,
                            inactiveTrackColor = SurfaceDark
                        )
                    )

                    // Trigger Chips
                    Text("Pemicu / Faktor Utama", fontSize = 13.sp, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        triggers.take(4).forEach { trig ->
                            CategoryChip(
                                label = trig,
                                isSelected = selectedTrigger.contains(trig),
                                onClick = { selectedTrigger = trig }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        triggers.drop(4).forEach { trig ->
                            CategoryChip(
                                label = trig,
                                isSelected = selectedTrigger.contains(trig),
                                onClick = { selectedTrigger = trig }
                            )
                        }
                    }

                    // Journal Textarea
                    DLTTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        label = "Jurnal Refleksi Harian",
                        placeholder = "Tulis apa yang kamu syukuri atau hal berkesan hari ini...",
                        singleLine = false,
                        maxLines = 4
                    )

                    DLTButton(
                        text = "Simpan Jurnal Hari Ini",
                        backgroundColor = AccentMood,
                        onClick = {
                            viewModel.saveMood(
                                moodScore = selectedMood,
                                stressLevel = stressLevel.toInt(),
                                triggers = selectedTrigger,
                                journalText = journalText.ifBlank { null }
                            )
                        }
                    )
                }
            }
        }

        // History Log
        item {
            Text("Riwayat Jurnal", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
        }

        items(allMoods) { mood ->
            val emoji = when (mood.moodScore) {
                1 -> "😔"
                2 -> "😐"
                3 -> "🙂"
                4 -> "😄"
                5 -> "🤩"
                else -> "🙂"
            }

            DLTCard(backgroundColor = SurfaceDark) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentMood.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = mood.date, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                            Text(text = "Stres: ${mood.stressLevel}/10", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        if (!mood.journalText.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = mood.journalText, fontSize = 13.sp, color = TextSecondaryDark)
                        }
                        if (mood.triggers.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "🏷️ ${mood.triggers}", fontSize = 11.sp, color = AccentMood)
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
