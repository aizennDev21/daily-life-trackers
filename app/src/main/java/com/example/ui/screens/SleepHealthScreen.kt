package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.utils.DateUtils
import com.example.core.utils.FormatUtils
import com.example.ui.components.CategoryChip
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTTextField
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun SleepHealthScreen(
    viewModel: LifeTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val todaySleep by viewModel.todaySleepLog.collectAsStateWithLifecycle()
    val allSleepLogs by viewModel.sleepLogs.collectAsStateWithLifecycle()

    var bedtime by remember(todaySleep) { mutableStateOf(todaySleep?.bedtime ?: "23:00") }
    var wakeTime by remember(todaySleep) { mutableStateOf(todaySleep?.wakeTime ?: "06:30") }
    var quality by remember(todaySleep) { mutableIntStateOf(todaySleep?.quality ?: 4) }
    var bodyCondition by remember(todaySleep) { mutableStateOf(todaySleep?.bodyCondition ?: "healthy") }
    var weightText by remember(todaySleep) { mutableStateOf(todaySleep?.weightKg?.toString() ?: "68.5") }
    var dreamNote by remember(todaySleep) { mutableStateOf(todaySleep?.dreamNote ?: "") }
    var symptomsNote by remember(todaySleep) { mutableStateOf(todaySleep?.symptomsNote ?: "") }

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
                    text = "😴 Sleep & Health Monitor",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Pantau pola istirahat, kualitas tidur & kondisi fisik",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }
        }

        // Summary Hero Card
        item {
            DLTCard(
                backgroundColor = SurfaceVariantDark,
                borderColor = AccentSleep.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Durasi Tidur Hari Ini", fontSize = 12.sp, color = TextSecondaryDark)
                        Text(
                            text = FormatUtils.formatDuration(todaySleep?.durationMinutes ?: 450),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentSleep
                        )
                        Text(
                            text = "$bedtime → $wakeTime",
                            fontSize = 13.sp,
                            color = TextPrimaryDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Kondisi Tubuh", fontSize = 12.sp, color = TextSecondaryDark)
                        Text(
                            text = when (bodyCondition) {
                                "healthy" -> "🌱 Sehat & Bugar"
                                "unwell" -> "🥱 Kurang Enak"
                                else -> "🤒 Sakit"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (bodyCondition == "healthy") StatusSuccess else StatusWarning
                        )
                    }
                }
            }
        }

        // Input Form Card
        item {
            DLTCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Catat Tidur Hari Ini", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DLTTextField(
                            value = bedtime,
                            onValueChange = { bedtime = it },
                            label = "Jam Tidur",
                            modifier = Modifier.weight(1f)
                        )
                        DLTTextField(
                            value = wakeTime,
                            onValueChange = { wakeTime = it },
                            label = "Jam Bangun",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Sleep Quality Stars
                    Text("Kualitas Tidur (1 - 5 Bintang)", fontSize = 13.sp, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = if (star <= quality) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Star $star",
                                tint = if (star <= quality) StatusWarning else TextSecondaryDark,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { quality = star }
                                    .padding(4.dp)
                            )
                        }
                    }

                    // Body Condition Pills
                    Text("Kondisi Tubuh", fontSize = 13.sp, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("healthy", "🌱 Sehat"),
                            Pair("unwell", "🥱 Kurang Fit"),
                            Pair("sick", "🤒 Sakit")
                        ).forEach { (bKey, label) ->
                            CategoryChip(
                                label = label,
                                isSelected = bodyCondition == bKey,
                                onClick = { bodyCondition = bKey }
                            )
                        }
                    }

                    DLTTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = "Berat Badan (kg)",
                        placeholder = "68.5"
                    )

                    DLTTextField(
                        value = dreamNote,
                        onValueChange = { dreamNote = it },
                        label = "Catatan Mimpi (Opsional)",
                        placeholder = "Tulis mimpi yang kamu ingat..."
                    )

                    DLTButton(
                        text = "Simpan Data Tidur & Kesehatan",
                        backgroundColor = AccentSleep,
                        onClick = {
                            viewModel.saveSleepLog(
                                bedtime = bedtime,
                                wakeTime = wakeTime,
                                durationMinutes = 450,
                                quality = quality,
                                dreamNote = dreamNote.ifBlank { null },
                                bodyCondition = bodyCondition,
                                weightKg = weightText.toFloatOrNull(),
                                symptomsNote = symptomsNote.ifBlank { null }
                            )
                        }
                    )
                }
            }
        }

        // History Sleep Logs
        item {
            Text("Riwayat Tidur Terakhir", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
        }

        items(allSleepLogs) { log ->
            DLTCard(backgroundColor = SurfaceDark) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = log.date, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimaryDark)
                        Text(text = "${log.bedtime} s/d ${log.wakeTime}", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = FormatUtils.formatDuration(log.durationMinutes), fontWeight = FontWeight.Bold, color = AccentSleep)
                        Text(text = "⭐ ${log.quality}/5", fontSize = 12.sp, color = StatusWarning)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
