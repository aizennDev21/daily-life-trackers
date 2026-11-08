package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.QuickAddSheet
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: LifeTrackerViewModel) {
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    var isOnboardingCompleted by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var activeSubScreen by remember { mutableStateOf<String?>(null) }
    var isQuickAddOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // Step 1: Check if user is logged in
    if (activeUser == null) {
        if (!isOnboardingCompleted) {
            OnboardingScreen(
                onFinishOnboarding = { isOnboardingCompleted = true }
            )
        } else {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    // Handled automatically via activeUser StateFlow
                }
            )
        }
        return
    }

    // Step 2: Logged in - render full application navigation
    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Dashboard, "nav_dashboard"),
        NavItem("Aktivitas", Icons.Default.ListAlt, "nav_activity"),
        NavItem("Keuangan", Icons.Default.Payments, "nav_finance"),
        NavItem("Habit", Icons.Default.CheckCircle, "nav_habit"),
        NavItem("Lainnya", Icons.Default.MoreHoriz, "nav_more")
    )

    Scaffold(
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (activeSubScreen != null) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (activeSubScreen) {
                                "sleep" -> "Tidur & Kesehatan"
                                "food" -> "Nutrisi & Air"
                                "exercise" -> "Olahraga & Gym"
                                "mood" -> "Mood & Jurnal"
                                "reports" -> "Analisis Laporan"
                                "coach" -> "AI Life Coach"
                                else -> "Detail"
                            },
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { activeSubScreen = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = TextPrimaryDark
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceDark
                    )
                )
            }
        },
        bottomBar = {
            if (activeSubScreen == null) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 8.dp,
                    modifier = Modifier.border(1.dp, BorderDark)
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) BrandPrimaryLight else TextSecondaryDark
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BrandPrimaryLight else TextSecondaryDark
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = BrandPrimary.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (activeSubScreen == null) {
                FloatingActionButton(
                    onClick = { isQuickAddOpen = true },
                    containerColor = BrandPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("fab_quick_add")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Add",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (activeSubScreen != null) {
                when (activeSubScreen) {
                    "sleep" -> SleepHealthScreen(viewModel = viewModel)
                    "food" -> FoodWaterScreen(viewModel = viewModel)
                    "exercise" -> ExerciseScreen(viewModel = viewModel)
                    "mood" -> MoodJournalScreen(viewModel = viewModel)
                    "reports" -> ReportsScreen(viewModel = viewModel, onNavigateToAiCoach = { activeSubScreen = "coach" })
                    "coach" -> AICoachScreen(viewModel = viewModel)
                }
            } else {
                when (selectedTab) {
                    0 -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { tabIdx -> selectedTab = tabIdx },
                        onNavigateToAiCoach = { activeSubScreen = "coach" }
                    )
                    1 -> ActivityLogScreen(
                        viewModel = viewModel,
                        onOpenQuickAdd = { isQuickAddOpen = true }
                    )
                    2 -> FinanceScreen(
                        viewModel = viewModel,
                        onOpenQuickAdd = { isQuickAddOpen = true }
                    )
                    3 -> HabitScreen(
                        viewModel = viewModel,
                        onOpenQuickAdd = { isQuickAddOpen = true }
                    )
                    4 -> MoreHubScreen(
                        viewModel = viewModel,
                        onNavigateToDestination = { dest -> activeSubScreen = dest }
                    )
                }
            }

            if (isQuickAddOpen) {
                QuickAddSheet(
                    viewModel = viewModel,
                    onDismiss = { isQuickAddOpen = false }
                )
            }
        }
    }
}
