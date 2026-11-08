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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.CategoryChip
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTTextField
import com.example.ui.components.EmptyStateWidget
import com.example.ui.theme.AccentFood
import com.example.ui.theme.AccentReports
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun FoodWaterScreen(
    viewModel: LifeTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val meals by viewModel.todayMealLogs.collectAsStateWithLifecycle()
    val waterLog by viewModel.todayWaterLog.collectAsStateWithLifecycle()

    val glassesCount = waterLog?.glassesCount ?: 0
    val targetGlasses = waterLog?.targetGlasses ?: 8
    val waterProgress = if (targetGlasses > 0) glassesCount.toFloat() / targetGlasses else 0f
    val totalCalories = meals.sumOf { it.caloriesEstimate ?: 0 }

    var isAddingMeal by remember { mutableStateOf(false) }
    var mealType by remember { mutableStateOf("breakfast") }
    var foodName by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("350") }
    var notesText by remember { mutableStateOf("") }

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
                    text = "🍽️ Nutrisi & Hidrasi Air",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Catat asupan makan harian dan target 8 gelas air",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }
        }

        // 1. Water Tracker Hero Card
        item {
            DLTCard(
                backgroundColor = SurfaceVariantDark,
                borderColor = AccentReports.copy(alpha = 0.5f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "💧 Target Minum Air Harian", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(
                                text = "$glassesCount / $targetGlasses Gelas (${glassesCount * 250} ml)",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.decrementWater() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceDark)
                                    .border(1.dp, BorderDark, CircleShape)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "-1", tint = TextPrimaryDark)
                            }
                            IconButton(
                                onClick = { viewModel.incrementWater() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentReports)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "+1", tint = Color.White)
                            }
                        }
                    }

                    val animProgress by animateFloatAsState(targetValue = waterProgress.coerceIn(0f, 1f), label = "water")
                    LinearProgressIndicator(
                        progress = { animProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AccentReports,
                        trackColor = SurfaceDark
                    )

                    // 8 Glass Icons Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..8).forEach { glassIndex ->
                            val isDrank = glassIndex <= glassesCount
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDrank) AccentReports.copy(alpha = 0.3f) else SurfaceDark)
                                    .border(1.dp, if (isDrank) AccentReports else BorderDark, RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (glassIndex > glassesCount) viewModel.incrementWater()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isDrank) "💧" else "🥛",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Meal Section Header with Total Calories
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Log Makanan Hari Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)
                    Text(text = "Total Asupan: $totalCalories kcal", fontSize = 12.sp, color = AccentFood, fontWeight = FontWeight.SemiBold)
                }

                if (!isAddingMeal) {
                    DLTButton(
                        text = "+ Catat Makan",
                        onClick = { isAddingMeal = true },
                        backgroundColor = AccentFood,
                        modifier = Modifier.width(140.dp)
                    )
                }
            }
        }

        // Add Meal Inline Form
        if (isAddingMeal) {
            item {
                DLTCard(backgroundColor = SurfaceVariantDark) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Catat Makanan Baru", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Pair("breakfast", "🌅 Sarapan"),
                                Pair("lunch", "☀️ Siang"),
                                Pair("dinner", "🌙 Malam"),
                                Pair("snack", "🍿 Snack")
                            ).forEach { (typeKey, label) ->
                                CategoryChip(label = label, isSelected = mealType == typeKey) { mealType = typeKey }
                            }
                        }

                        DLTTextField(value = foodName, onValueChange = { foodName = it }, label = "Nama Menu", placeholder = "Contoh: Ayam Bakar + Nasi Merah")
                        DLTTextField(value = caloriesText, onValueChange = { caloriesText = it.filter { ch -> ch.isDigit() } }, label = "Estimasi Kalori (kcal)", placeholder = "450")
                        DLTTextField(value = notesText, onValueChange = { notesText = it }, label = "Catatan", placeholder = "Porsi kenyang")

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DLTButton(
                                text = "Simpan",
                                modifier = Modifier.weight(1f),
                                backgroundColor = AccentFood,
                                onClick = {
                                    if (foodName.isNotBlank()) {
                                        viewModel.addMealLog(
                                            mealType = mealType,
                                            foodName = foodName,
                                            calories = caloriesText.toIntOrNull() ?: 300,
                                            notes = notesText.ifBlank { null }
                                        )
                                        isAddingMeal = false
                                        foodName = ""
                                        notesText = ""
                                    }
                                }
                            )
                            DLTButton(
                                text = "Batal",
                                modifier = Modifier.weight(1f),
                                backgroundColor = SurfaceDark,
                                onClick = { isAddingMeal = false }
                            )
                        }
                    }
                }
            }
        }

        // Meals List
        if (meals.isEmpty()) {
            item {
                EmptyStateWidget(
                    emoji = "🥗",
                    title = "Belum Ada Catatan Makanan",
                    description = "Catat menu sarapan, makan siang, atau makan malammu hari ini."
                )
            }
        } else {
            items(meals) { meal ->
                val typeEmoji = when (meal.mealType) {
                    "breakfast" -> "🌅"
                    "lunch" -> "☀️"
                    "dinner" -> "🌙"
                    else -> "🍿"
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentFood.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = typeEmoji, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = meal.foodName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimaryDark)
                                if (!meal.notes.isNullOrBlank()) {
                                    Text(text = meal.notes, fontSize = 12.sp, color = TextSecondaryDark)
                                }
                                if (meal.caloriesEstimate != null) {
                                    Text(text = "🔥 ${meal.caloriesEstimate} kcal", fontSize = 11.sp, color = AccentFood, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.deleteMealLog(meal) }) {
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
