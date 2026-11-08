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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DLTButton
import com.example.ui.components.DLTCard
import com.example.ui.components.DLTTextField
import com.example.ui.theme.AccentExercise
import com.example.ui.theme.AccentFood
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentReports
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextDisabledDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

data class HubItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val color: Color
)

@Composable
fun MoreHubScreen(
    viewModel: LifeTrackerViewModel,
    onNavigateToDestination: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()

    var isEditingProfile by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val currentDisplayName = activeUser?.fullName ?: userProfile?.displayName ?: "Pengguna"
    val currentEmail = activeUser?.email ?: userProfile?.email ?: "user@lifetracker.app"
    val currentAvatar = activeUser?.avatarEmoji ?: "👤"

    var nameInput by remember(currentDisplayName) { mutableStateOf(currentDisplayName) }
    var emailInput by remember(currentEmail) { mutableStateOf(currentEmail) }

    val hubItems = listOf(
        HubItem("sleep", "Tidur & Kesehatan", "Pantau istirahat & tubuh", "😴", AccentSleep),
        HubItem("food", "Nutrisi & Air", "Log makan & 8 gelas air", "🍽️", AccentFood),
        HubItem("exercise", "Olahraga & Gym", "Workout & kalori terbakar", "💪", AccentExercise),
        HubItem("mood", "Mood & Jurnal", "Refleksi perasaan & stres", "😊", AccentMood),
        HubItem("reports", "Analisis & Laporan", "Statistik & life score", "📊", AccentReports),
        HubItem("coach", "AI Life Coach", "Gemini 3.1 Pro Thinking", "✨", BrandPrimary)
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = { Text("Apakah Anda yakin ingin keluar dari akun Anda?", color = TextSecondaryDark) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text("Keluar", color = StatusError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = SurfaceDark
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Data Lokal", fontWeight = FontWeight.Bold, color = StatusError) },
            text = { Text("Semua data aktivitas, transaksi, habit, dan log di database lokal akan dihapus bersih. Lanjutkan?", color = TextSecondaryDark) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.clearAllData()
                    },
                    modifier = Modifier.testTag("confirm_reset_data_button")
                ) {
                    Text("Reset Bersih", color = StatusError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = SurfaceDark
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "⚡ Semua Modul & Profil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Text(
                text = "Pusat pelacak hidup & manajemen akun",
                fontSize = 13.sp,
                color = TextSecondaryDark
            )
        }

        // Profile Card
        item {
            DLTCard(
                backgroundColor = SurfaceVariantDark,
                borderColor = BorderDark
            ) {
                if (isEditingProfile) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Edit Profil Pengguna", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        DLTTextField(value = nameInput, onValueChange = { nameInput = it }, label = "Nama Lengkap")
                        DLTTextField(value = emailInput, onValueChange = { emailInput = it }, label = "Email")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DLTButton(
                                text = "Simpan",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.updateProfile(nameInput, emailInput, true)
                                    isEditingProfile = false
                                }
                            )
                            DLTButton(
                                text = "Batal",
                                modifier = Modifier.weight(1f),
                                backgroundColor = SurfaceDark,
                                onClick = { isEditingProfile = false }
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentAvatar, fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = currentDisplayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = currentEmail,
                                    fontSize = 13.sp,
                                    color = TextSecondaryDark
                                )
                                Text(
                                    text = "🟢 Akun Terotentikasi Lokal",
                                    fontSize = 11.sp,
                                    color = BrandPrimaryLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        DLTButton(
                            text = "Edit",
                            backgroundColor = SurfaceElevatedDark,
                            onClick = { isEditingProfile = true },
                            modifier = Modifier.width(90.dp)
                        )
                    }
                }
            }
        }

        // Section Title: Modul Khusus
        item {
            Text(
                text = "Modul Khusus & Pelacak",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // Module Items List
        items(hubItems.size) { index ->
            val item = hubItems[index]
            DLTCard(
                backgroundColor = SurfaceDark,
                borderColor = BorderDark,
                onClick = { onNavigateToDestination(item.id) }
            ) {
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
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.emoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = item.subtitle,
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Account & Data Management Section
        item {
            Text(
                text = "Manajemen Akun & Data",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        item {
            DLTCard(
                backgroundColor = SurfaceDark,
                borderColor = BorderDark
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Reset data button
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_reset_all_data")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bersihkan Semua Data Lokal",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    // Logout button
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_logout")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = TextPrimaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Keluar dari Akun (Logout)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
