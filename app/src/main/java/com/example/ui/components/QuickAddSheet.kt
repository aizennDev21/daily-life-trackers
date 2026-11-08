package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentExercise
import com.example.ui.theme.AccentFinance
import com.example.ui.theme.AccentFood
import com.example.ui.theme.AccentHabit
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentProductivity
import com.example.ui.theme.AccentReports
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.StatusError
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

data class QuickAddOption(
    val id: String,
    val title: String,
    val emoji: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    viewModel: LifeTrackerViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeForm by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activeForm == null) "⚡ Quick Add Log" else "Tambah Data",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondaryDark)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (activeForm == null) {
                val options = listOf(
                    QuickAddOption("activity", "Aktivitas", "📝", BrandPrimary),
                    QuickAddOption("expense", "Pengeluaran", "💸", AccentFinance),
                    QuickAddOption("income", "Pemasukan", "💵", AccentFinance),
                    QuickAddOption("water", "Minum Air", "💧", AccentReports),
                    QuickAddOption("meal", "Makan", "🍽️", AccentFood),
                    QuickAddOption("exercise", "Olahraga", "💪", AccentExercise),
                    QuickAddOption("task", "Tugas Baru", "📋", AccentProductivity),
                    QuickAddOption("habit", "Habit Baru", "✅", AccentHabit),
                    QuickAddOption("mood", "Mood & Jurnal", "😊", AccentMood),
                    QuickAddOption("sleep", "Tidur & Sehat", "😴", AccentSleep)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(340.dp)
                ) {
                    items(options) { opt ->
                        QuickAddCard(option = opt) {
                            if (opt.id == "water") {
                                viewModel.incrementWater()
                                onDismiss()
                            } else {
                                activeForm = opt.id
                            }
                        }
                    }
                }
            } else {
                when (activeForm) {
                    "activity" -> AddActivityForm(viewModel) { onDismiss() }
                    "expense" -> AddFinanceForm(viewModel, isExpense = true) { onDismiss() }
                    "income" -> AddFinanceForm(viewModel, isExpense = false) { onDismiss() }
                    "meal" -> AddMealForm(viewModel) { onDismiss() }
                    "exercise" -> AddExerciseForm(viewModel) { onDismiss() }
                    "task" -> AddTaskForm(viewModel) { onDismiss() }
                    "habit" -> AddHabitForm(viewModel) { onDismiss() }
                    "mood" -> AddMoodForm(viewModel) { onDismiss() }
                    "sleep" -> AddSleepForm(viewModel) { onDismiss() }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun QuickAddCard(
    option: QuickAddOption,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(option.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = option.emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = option.title,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

// 1. Add Activity Form
@Composable
fun AddActivityForm(viewModel: LifeTrackerViewModel, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("work") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30") }

    val categories = listOf(
        Pair("work", "💼 Work"),
        Pair("personal", "👤 Personal"),
        Pair("health", "❤️ Health"),
        Pair("social", "👥 Social"),
        Pair("rest", "🛋️ Rest"),
        Pair("errands", "🛒 Errands")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DLTTextField(value = title, onValueChange = { title = it }, label = "Judul Aktivitas", placeholder = "Contoh: Rapat tim sprint")
        DLTTextField(value = description, onValueChange = { description = it }, label = "Catatan / Deskripsi", placeholder = "Opsional")
        DLTTextField(value = duration, onValueChange = { duration = it }, label = "Durasi (menit)", placeholder = "30")

        Text("Kategori", color = TextSecondaryDark, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.take(3).forEach { (catId, label) ->
                CategoryChip(label = label, isSelected = category == catId) { category = catId }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.drop(3).forEach { (catId, label) ->
                CategoryChip(label = label, isSelected = category == catId) { category = catId }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = "Simpan Aktivitas",
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addActivity(
                        title = title,
                        category = category,
                        description = description.ifBlank { null },
                        durationMinutes = duration.toIntOrNull() ?: 30,
                        emoji = when (category) {
                            "work" -> "💼"
                            "health" -> "❤️"
                            "social" -> "👥"
                            "rest" -> "🛋️"
                            "errands" -> "🛒"
                            else -> "📝"
                        }
                    )
                    onDone()
                }
            }
        )
    }
}

// 2. Add Finance Form
@Composable
fun AddFinanceForm(viewModel: LifeTrackerViewModel, isExpense: Boolean, onDone: () -> Unit) {
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (isExpense) "Makan" else "Gaji") }
    var note by remember { mutableStateOf("") }

    val expenseCategories = listOf("Makan", "Transport", "Belanja", "Tagihan", "Kesehatan", "Hiburan", "Lainnya")
    val incomeCategories = listOf("Gaji", "Freelance", "Bisnis", "Transfer", "Hadiah", "Lainnya")
    val cats = if (isExpense) expenseCategories else incomeCategories

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DLTTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
            label = "Nominal (IDR)",
            placeholder = "50000"
        )
        DLTTextField(value = note, onValueChange = { note = it }, label = "Catatan Transaksi", placeholder = "Contoh: Makan siang nasi padang")

        Text("Kategori", color = TextSecondaryDark, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            cats.take(3).forEach { cat ->
                CategoryChip(label = cat, isSelected = category == cat) { category = cat }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            cats.drop(3).take(4).forEach { cat ->
                CategoryChip(label = cat, isSelected = category == cat) { category = cat }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = if (isExpense) "Simpan Pengeluaran" else "Simpan Pemasukan",
            backgroundColor = if (isExpense) StatusError else AccentFinance,
            onClick = {
                val amt = amountText.toLongOrNull() ?: 0L
                if (amt > 0) {
                    viewModel.addTransaction(
                        type = if (isExpense) "expense" else "income",
                        amount = amt,
                        category = category,
                        note = note.ifBlank { null }
                    )
                    onDone()
                }
            }
        )
    }
}

// 3. Add Meal Form
@Composable
fun AddMealForm(viewModel: LifeTrackerViewModel, onDone: () -> Unit) {
    var mealType by remember { mutableStateOf("breakfast") }
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("350") }
    var notes by remember { mutableStateOf("") }

    val types = listOf(
        Pair("breakfast", "🌅 Sarapan"),
        Pair("lunch", "☀️ Siang"),
        Pair("dinner", "🌙 Malam"),
        Pair("snack", "🍿 Snack")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            types.forEach { (typeKey, label) ->
                CategoryChip(label = label, isSelected = mealType == typeKey) { mealType = typeKey }
            }
        }
        DLTTextField(value = foodName, onValueChange = { foodName = it }, label = "Nama Makanan / Minuman", placeholder = "Contoh: Nasi Goreng + Telur")
        DLTTextField(value = calories, onValueChange = { calories = it.filter { ch -> ch.isDigit() } }, label = "Estimasi Kalori (kcal)", placeholder = "350")
        DLTTextField(value = notes, onValueChange = { notes = it }, label = "Catatan", placeholder = "Porsi sedang")

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = "Simpan Makanan",
            backgroundColor = AccentFood,
            onClick = {
                if (foodName.isNotBlank()) {
                    viewModel.addMealLog(
                        mealType = mealType,
                        foodName = foodName,
                        calories = calories.toIntOrNull(),
                        notes = notes.ifBlank { null }
                    )
                    onDone()
                }
            }
        )
    }
}

// 4. Add Exercise Form
@Composable
fun AddExerciseForm(viewModel: LifeTrackerViewModel, onDone: () -> Unit) {
    var exerciseType by remember { mutableStateOf("running") }
    var name by remember { mutableStateOf("Lari Santai") }
    var duration by remember { mutableStateOf("30") }
    var intensity by remember { mutableStateOf("moderate") }
    var calories by remember { mutableStateOf("200") }

    val types = listOf(
        Pair("running", "🏃 Lari"),
        Pair("walking", "🚶 Jalan"),
        Pair("gym", "🏋️ Gym"),
        Pair("cycling", "🚴 Sepeda"),
        Pair("swimming", "🏊 Renang")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            types.take(3).forEach { (typeKey, label) ->
                CategoryChip(label = label, isSelected = exerciseType == typeKey) {
                    exerciseType = typeKey
                    name = when (typeKey) {
                        "running" -> "Lari Santai"
                        "walking" -> "Jalan Kaki"
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
            types.drop(3).forEach { (typeKey, label) ->
                CategoryChip(label = label, isSelected = exerciseType == typeKey) {
                    exerciseType = typeKey
                    name = if (typeKey == "cycling") "Gowes Sepeda" else "Berenang"
                }
            }
        }

        DLTTextField(value = name, onValueChange = { name = it }, label = "Nama Olahraga")
        DLTTextField(value = duration, onValueChange = { duration = it.filter { ch -> ch.isDigit() } }, label = "Durasi (menit)")
        DLTTextField(value = calories, onValueChange = { calories = it.filter { ch -> ch.isDigit() } }, label = "Kalori Terbakar (kcal)")

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = "Simpan Sesi Olahraga",
            backgroundColor = AccentExercise,
            onClick = {
                viewModel.addExerciseLog(
                    exerciseType = exerciseType,
                    exerciseName = name,
                    durationMinutes = duration.toIntOrNull() ?: 30,
                    intensity = intensity,
                    caloriesBurned = calories.toIntOrNull()
                )
                onDone()
            }
        )
    }
}

// 5. Add Task Form
@Composable
fun AddTaskForm(viewModel: LifeTrackerViewModel, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("medium") }

    val priorities = listOf(
        Pair("high", "🔴 High"),
        Pair("medium", "🟡 Medium"),
        Pair("low", "🟢 Low")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DLTTextField(value = title, onValueChange = { title = it }, label = "Judul Tugas", placeholder = "Contoh: Review PR Sprint 1")
        DLTTextField(value = description, onValueChange = { description = it }, label = "Keterangan", placeholder = "Opsional")

        Text("Prioritas", color = TextSecondaryDark, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            priorities.forEach { (pKey, label) ->
                CategoryChip(label = label, isSelected = priority == pKey) { priority = pKey }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = "Tambah Tugas",
            backgroundColor = AccentProductivity,
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addTask(title = title, description = description.ifBlank { null }, priority = priority)
                    onDone()
                }
            }
        )
    }
}

// 6. Add Habit Form
@Composable
fun AddHabitForm(viewModel: LifeTrackerViewModel, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("✨") }
    var category by remember { mutableStateOf("health") }

    val emojis = listOf("✨", "📖", "🏃", "💧", "🕌", "🧘", "💰", "🍎", "💤", "🎯")

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DLTTextField(value = name, onValueChange = { name = it }, label = "Nama Habit", placeholder = "Contoh: Meditasi 10 Menit")

        Text("Pilih Ikon Emoji", color = TextSecondaryDark, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            emojis.take(5).forEach { em ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (emoji == em) BrandPrimary.copy(alpha = 0.3f) else SurfaceVariantDark)
                        .border(1.dp, if (emoji == em) BrandPrimary else BorderDark, RoundedCornerShape(10.dp))
                        .clickable { emoji = em },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = em, fontSize = 20.sp)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            emojis.drop(5).forEach { em ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (emoji == em) BrandPrimary.copy(alpha = 0.3f) else SurfaceVariantDark)
                        .border(1.dp, if (emoji == em) BrandPrimary else BorderDark, RoundedCornerShape(10.dp))
                        .clickable { emoji = em },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = em, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = "Simpan Habit",
            backgroundColor = AccentHabit,
            onClick = {
                if (name.isNotBlank()) {
                    viewModel.addHabit(
                        name = name,
                        icon = emoji,
                        colorHex = "#00BFA5",
                        category = category
                    )
                    onDone()
                }
            }
        )
    }
}

// 7. Add Mood Form
@Composable
fun AddMoodForm(viewModel: LifeTrackerViewModel, onDone: () -> Unit) {
    var score by remember { mutableStateOf(4) }
    var journal by remember { mutableStateOf("") }
    var trigger by remember { mutableStateOf("Kerja") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Pilih Mood Hari Ini", color = TextSecondaryDark, fontSize = 13.sp)
        MoodPicker(selectedScore = score, onMoodSelected = { score = it })
        DLTTextField(
            value = journal,
            onValueChange = { journal = it },
            label = "Jurnal Refleksi Harian",
            placeholder = "Apa yang kamu rasakan atau syukuri hari ini?",
            singleLine = false,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = "Simpan Mood & Jurnal",
            backgroundColor = AccentMood,
            onClick = {
                viewModel.saveMood(moodScore = score, stressLevel = 3, triggers = trigger, journalText = journal.ifBlank { null })
                onDone()
            }
        )
    }
}

// 8. Add Sleep Form
@Composable
fun AddSleepForm(viewModel: LifeTrackerViewModel, onDone: () -> Unit) {
    var bedtime by remember { mutableStateOf("23:00") }
    var wakeTime by remember { mutableStateOf("06:30") }
    var quality by remember { mutableStateOf(4) }
    var dreamNote by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DLTTextField(value = bedtime, onValueChange = { bedtime = it }, label = "Jam Tidur", modifier = Modifier.weight(1f))
            DLTTextField(value = wakeTime, onValueChange = { wakeTime = it }, label = "Jam Bangun", modifier = Modifier.weight(1f))
        }
        DLTTextField(value = dreamNote, onValueChange = { dreamNote = it }, label = "Catatan Mimpi", placeholder = "Opsional")

        Spacer(modifier = Modifier.height(6.dp))
        DLTButton(
            text = "Simpan Catatan Tidur",
            backgroundColor = AccentSleep,
            onClick = {
                viewModel.saveSleepLog(
                    bedtime = bedtime,
                    wakeTime = wakeTime,
                    durationMinutes = 450,
                    quality = quality,
                    dreamNote = dreamNote.ifBlank { null }
                )
                onDone()
            }
        )
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) BrandPrimary else SurfaceVariantDark)
            .border(1.dp, if (isSelected) BrandPrimary else BorderDark, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextPrimaryDark
        )
    }
}
