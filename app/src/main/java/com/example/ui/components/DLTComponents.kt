package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentHabit
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

// 1. DLT Base Card
@Composable
fun DLTCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceDark,
    borderColor: Color = BorderDark,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// 2. Module Color Badge
@Composable
fun ModuleBadge(
    icon: ImageVector? = null,
    emoji: String? = null,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    size: Int = 40,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor.copy(alpha = 0.2f))
            .border(1.dp, backgroundColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (emoji != null) {
            Text(text = emoji, fontSize = (size * 0.5).sp)
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = backgroundColor,
                modifier = Modifier.size((size * 0.55).dp)
            )
        }
    }
}

// 3. Primary Button
@Composable
fun DLTButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = BrandPrimary,
    icon: ImageVector? = null,
    testTag: String = "dlt_button"
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag(testTag),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
            disabledContainerColor = backgroundColor.copy(alpha = 0.4f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

// 4. Secondary Outlined Button
@Composable
fun DLTOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = BorderDark,
    contentColor: Color = TextPrimaryDark,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

// 5. Input Text Field
@Composable
fun DLTTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    testTag: String = "dlt_text_field"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondaryDark) },
        placeholder = placeholder?.let { { Text(it, color = TextSecondaryDark.copy(alpha = 0.6f)) } },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = TextSecondaryDark) }
        },
        singleLine = singleLine,
        maxLines = maxLines,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceVariantDark,
            unfocusedContainerColor = SurfaceDark,
            focusedBorderColor = BrandPrimary,
            unfocusedBorderColor = BorderDark,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
        )
    )
}

// 6. Mood Quick Picker
@Composable
fun MoodPicker(
    selectedScore: Int,
    onMoodSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val moods = listOf(
        Pair(1, "😔"),
        Pair(2, "😐"),
        Pair(3, "🙂"),
        Pair(4, "😄"),
        Pair(5, "🤩")
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        moods.forEach { (score, emoji) ->
            val isSelected = selectedScore == score
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.25f else 1.0f, label = "scale")

            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) BrandPrimary.copy(alpha = 0.35f)
                        else SurfaceVariantDark
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BrandPrimary else BorderDark,
                        shape = CircleShape
                    )
                    .clickable { onMoodSelected(score) }
                    .testTag("mood_option_$score"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }
        }
    }
}

// 7. Habit Check Item
@Composable
fun HabitCheckItem(
    name: String,
    icon: String,
    streak: Int,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    accentColor: Color = AccentHabit,
    modifier: Modifier = Modifier
) {
    DLTCard(
        modifier = modifier,
        backgroundColor = if (isCompleted) SurfaceVariantDark else SurfaceDark,
        borderColor = if (isCompleted) accentColor.copy(alpha = 0.5f) else BorderDark
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (isCompleted) TextPrimaryDark else TextPrimaryDark
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔥 $streak hari streak",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (streak > 0) Color(0xFFFF9800) else TextSecondaryDark
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) accentColor
                        else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (isCompleted) accentColor else TextSecondaryDark.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .clickable(onClick = onToggle)
                    .testTag("habit_toggle_${name.lowercase().replace(" ", "_")}"),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selesai",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// 8. Empty State Encouraging Component
@Composable
fun EmptyStateWidget(
    emoji: String,
    title: String,
    description: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceVariantDark)
                .border(1.dp, BorderDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 34.sp)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            DLTButton(
                text = buttonText,
                onClick = onButtonClick,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}
