package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentExercise
import com.example.ui.theme.AccentFinance
import com.example.ui.theme.AccentHabit
import com.example.ui.theme.AccentProductivity
import com.example.ui.theme.AccentReports
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val heroEmoji: String,
    val heroIcon: ImageVector,
    val accentColor: Color,
    val badges: List<Pair<String, Color>>,
    val featurePoints: List<String>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = remember {
        listOf(
            OnboardingPageData(
                title = "Kuasai Ritme & Produktivitasmu",
                subtitle = "Pantau aktivitas harian, bangun kebiasaan positif (habits), dan selesaikan tugas penting dengan fokus tinggi.",
                heroEmoji = "⚡",
                heroIcon = Icons.Default.Speed,
                accentColor = BrandPrimary,
                badges = listOf(
                    "Aktivitas Harian" to AccentProductivity,
                    "Habit Tracker" to AccentHabit,
                    "Pomodoro Focus" to AccentExercise
                ),
                featurePoints = listOf(
                    "Pencatatan aktivitas dengan durasi & tag fleksibel",
                    "Target kebiasaan harian & pelacak streak",
                    "Manajemen tugas & to-do list terintegrasi"
                )
            ),
            OnboardingPageData(
                title = "Kesehatan & Finansial Presisi",
                subtitle = "Pola hidup teratur dengan pemantauan tidur, hidrasi 8 gelas, makanan bernutrisi, olahraga, dan arus kas harian.",
                heroEmoji = "💧",
                heroIcon = Icons.Default.Favorite,
                accentColor = AccentFinance,
                badges = listOf(
                    "Tidur & Mood" to AccentSleep,
                    "8 Gelas Air" to AccentReports,
                    "Cashflow Terkendali" to AccentFinance
                ),
                featurePoints = listOf(
                    "Pencatat jam tidur, kualitas, dan jurnal mood",
                    "Pencatatan makan & hidrasi air harian",
                    "Laporan pemasukan, pengeluaran, dan tabungan"
                )
            ),
            OnboardingPageData(
                title = "AI Life Coach & Data Mandiri",
                subtitle = "Dapatkan rekomendasi evaluasi harian dari AI Coach berbasis Gemini. Data tersimpan aman di perangkat Anda.",
                heroEmoji = "🤖",
                heroIcon = Icons.Default.AutoAwesome,
                accentColor = AccentProductivity,
                badges = listOf(
                    "Gemini AI" to BrandPrimary,
                    "Laporan & Grafik" to AccentReports,
                    "Privasi Database Lokal" to AccentHabit
                ),
                featurePoints = listOf(
                    "Analisis harian cerdas & tips keseimbangan hidup",
                    "Laporan komprehensif 7 hari & metrik holistik",
                    "100% dinamis dan tersimpan lokal di Room DB"
                )
            )
        )
    }

    val page = pages[currentPage]

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        color = BackgroundDark
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            page.accentColor.copy(alpha = 0.15f),
                            BackgroundDark,
                            BackgroundDark
                        )
                    )
                )
                .padding(24.dp)
        ) {
            // Top Bar: Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Life Tracker",
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (currentPage < pages.size - 1) {
                    TextButton(
                        onClick = onFinishOnboarding,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Lewati",
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Visual Card
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.size(160.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        page.accentColor.copy(alpha = 0.35f),
                                        SurfaceDark
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = page.heroEmoji,
                                fontSize = 56.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Badges
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    page.badges.forEach { (text, color) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = color.copy(alpha = 0.15f),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = text,
                                color = color,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = page.subtitle,
                    fontSize = 14.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Feature check items
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        page.featurePoints.forEach { point ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = page.accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = point,
                                    fontSize = 12.sp,
                                    color = TextPrimaryDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicator dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPage
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            animationSpec = tween(300),
                            label = "dot_width"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) page.accentColor else SurfaceElevatedDark,
                            animationSpec = tween(300),
                            label = "dot_color"
                        )

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { currentPage = index }
                        )
                        if (index < pages.size - 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }

                // Action Button
                if (currentPage < pages.size - 1) {
                    Button(
                        onClick = { currentPage++ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_next_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text(
                            text = "Lanjut",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onFinishOnboarding,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_get_started_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text(
                            text = "Mulai Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
