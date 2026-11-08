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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.core.utils.DateUtils
import com.example.core.utils.FormatUtils
import com.example.ui.components.CategoryChip
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTTextField
import com.example.ui.components.EmptyStateWidget
import com.example.ui.theme.AccentExercise
import com.example.ui.theme.AccentFinance
import com.example.ui.theme.AccentFood
import com.example.ui.theme.AccentHabit
import com.example.ui.theme.AccentProductivity
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun ActivityLogScreen(
    viewModel: LifeTrackerViewModel,
    onOpenQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }

    val categories = listOf(
        Pair("all", "Semua"),
        Pair("work", "💼 Work"),
        Pair("personal", "👤 Personal"),
        Pair("health", "❤️ Health"),
        Pair("food", "🍽️ Food"),
        Pair("social", "👥 Social"),
        Pair("rest", "🛋️ Rest")
    )

    val filteredActivities = activities.filter { act ->
        (selectedCategory == "all" || act.category.equals(selectedCategory, ignoreCase = true)) &&
                (searchQuery.isBlank() || act.title.contains(searchQuery, ignoreCase = true) || (act.description?.contains(searchQuery, ignoreCase = true) == true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📝 Activity Log",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Timeline aktivitas harian kronologis",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }

            IconButton(
                onClick = onOpenQuickAdd,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        DLTTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Cari Aktivitas",
            placeholder = "Ketik judul atau lokasi...",
            leadingIcon = Icons.Default.Search
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Category Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { (catKey, label) ->
                CategoryChip(
                    label = label,
                    isSelected = selectedCategory == catKey,
                    onClick = { selectedCategory = catKey }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Timeline List
        if (filteredActivities.isEmpty()) {
            EmptyStateWidget(
                emoji = "📝",
                title = "Belum Ada Aktivitas",
                description = "Catat hal-hal yang kamu lakukan hari ini dari bangun tidur sampai istirahat.",
                buttonText = "+ Catat Aktivitas",
                onButtonClick = onOpenQuickAdd
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredActivities) { activity ->
                    TimelineItemCard(
                        activity = activity,
                        onDelete = { viewModel.deleteActivity(activity) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun TimelineItemCard(
    activity: com.example.data.local.entities.ActivityEntity,
    onDelete: () -> Unit
) {
    val categoryColor = when (activity.category.lowercase()) {
        "work" -> AccentProductivity
        "food" -> AccentFood
        "health" -> AccentSleep
        "social" -> Color(0xFFFF4081)
        "exercise" -> AccentExercise
        "finance" -> AccentFinance
        else -> AccentHabit
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timeline time & node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(54.dp)
        ) {
            Text(
                text = DateUtils.formatTime(activity.timestamp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(BorderDark)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Content Card
        DLTCard(
            modifier = Modifier.weight(1f),
            backgroundColor = SurfaceDark,
            borderColor = BorderDark
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = activity.emoji ?: "📝", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activity.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                    }

                    if (!activity.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activity.description,
                            fontSize = 13.sp,
                            color = TextSecondaryDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(categoryColor.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = activity.category.replaceFirstChar { it.uppercase() },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = categoryColor
                            )
                        }

                        if (activity.durationMinutes != null && activity.durationMinutes > 0) {
                            Text(
                                text = "⏱️ ${FormatUtils.formatDuration(activity.durationMinutes)}",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }

                        if (!activity.location.isNullOrBlank()) {
                            Text(
                                text = "📍 ${activity.location}",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = TextSecondaryDark.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
