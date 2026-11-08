package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTTextField
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun AICoachScreen(
    viewModel: LifeTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val isThinking by viewModel.aiCoachThinking.collectAsStateWithLifecycle()
    val coachResponse by viewModel.aiCoachResponse.collectAsStateWithLifecycle()
    var userPrompt by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (coachResponse == null && !isThinking) {
            viewModel.requestAiLifeCoach()
        }
    }

    val suggestionPrompts = listOf(
        "🌟 Evaluasi Keseimbangan Hari Ini",
        "💡 Saran Perbaikan Produktivitas",
        "🥗 Analisis Nutrisi & Istirahat",
        "💰 Review Kebiasaan Keuangan",
        "🧘 Tips Mengurangi Stres Kerja"
    )

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Powered by Gemini 3.1 Pro • High Thinking",
                    fontSize = 12.sp,
                    color = BrandPrimaryLight
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Suggestion Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(suggestionPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceVariantDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                        .clickable {
                            userPrompt = prompt
                            viewModel.requestAiLifeCoach(prompt)
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = prompt,
                        fontSize = 12.sp,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Response Body
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isThinking) {
                item {
                    DLTCard(
                        backgroundColor = Color(0xFF1B1834),
                        borderColor = BrandPrimary.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = BrandPrimaryLight,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "AI Coach sedang berpikir mendalam...",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = BrandPrimaryLight
                                )
                                Text(
                                    text = "Menganalisis data aktivitas, kebiasaan, dan pola hidup",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }

            if (!coachResponse.isNullOrBlank()) {
                item {
                    DLTCard(
                        backgroundColor = SurfaceDark,
                        borderColor = BorderDark
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = BrandPrimaryLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rangkuman & Rekomendasi Coach",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimaryDark
                                )
                            }

                            Text(
                                text = coachResponse ?: "",
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = TextPrimaryDark
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Custom Question Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DLTTextField(
                value = userPrompt,
                onValueChange = { userPrompt = it },
                label = "Tanya AI Coach...",
                placeholder = "Misal: Bagaimana cara meningkatkan fokus belajar?",
                modifier = Modifier.weight(1f),
                testTag = "ai_coach_input"
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (userPrompt.isNotBlank() && !isThinking) {
                        val promptToSend = userPrompt
                        userPrompt = ""
                        viewModel.requestAiLifeCoach(promptToSend)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isThinking) SurfaceVariantDark else BrandPrimary)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Kirim",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
