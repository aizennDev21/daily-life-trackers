package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.DLTCard
import com.example.ui.components.EmptyStateWidget
import com.example.ui.theme.AccentFinance
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.LifeTrackerViewModel

@Composable
fun FinanceScreen(
    viewModel: LifeTrackerViewModel,
    onOpenQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val financeSummary by viewModel.financeSummary.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Semua", "Pemasukan", "Pengeluaran", "Tabungan")

    val filteredTransactions = when (selectedTab) {
        1 -> transactions.filter { it.type == "income" }
        2 -> transactions.filter { it.type == "expense" }
        3 -> transactions.filter { it.type == "saving" }
        else -> transactions
    }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "💰 Keuangan Pribadi",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Buku kas harian, pemasukan & pengeluaran",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }

            IconButton(
                onClick = onOpenQuickAdd,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AccentFinance)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Transaksi", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Balance Hero Card
        DLTCard(
            backgroundColor = SurfaceVariantDark,
            borderColor = AccentFinance.copy(alpha = 0.5f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    Text(text = "Saldo Saat Ini", fontSize = 13.sp, color = TextSecondaryDark)
                    Text(
                        text = FormatUtils.formatRupiah(financeSummary.balance),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Income
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceDark)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Pemasukan", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = FormatUtils.formatRupiah(financeSummary.totalIncome),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusSuccess
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Expense
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceDark)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TrendingDown, contentDescription = null, tint = StatusError, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Pengeluaran", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = FormatUtils.formatRupiah(financeSummary.totalExpense),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusError
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = BrandPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BrandPrimary
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) BrandPrimary else TextSecondaryDark
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transactions List
        if (filteredTransactions.isEmpty()) {
            EmptyStateWidget(
                emoji = "💳",
                title = "Belum Ada Transaksi",
                description = "Catat transaksi pemasukan atau pengeluaran harianmu untuk menjaga kesehatan finansial.",
                buttonText = "+ Tambah Transaksi",
                onButtonClick = onOpenQuickAdd
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTransactions) { item ->
                    TransactionItemCard(
                        transaction = item,
                        onDelete = { viewModel.deleteTransaction(item) }
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
fun TransactionItemCard(
    transaction: com.example.data.local.entities.TransactionEntity,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type == "income"
    val isSaving = transaction.type == "saving"

    val typeColor = when {
        isIncome -> StatusSuccess
        isSaving -> Color(0xFF00B0FF)
        else -> StatusError
    }

    val typePrefix = when {
        isIncome -> "+"
        isSaving -> "💼"
        else -> "-"
    }

    val categoryEmoji = when (transaction.category.lowercase()) {
        "makan" -> "🍽️"
        "transport" -> "🛵"
        "belanja" -> "🛍️"
        "tagihan" -> "🧾"
        "kesehatan" -> "💊"
        "hiburan" -> "🎮"
        "gaji" -> "💵"
        "freelance" -> "💻"
        "bisnis" -> "📈"
        else -> "🏷️"
    }

    DLTCard(
        backgroundColor = SurfaceDark,
        borderColor = BorderDark
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(typeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = categoryEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.category,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TextPrimaryDark
                        )
                        if (transaction.isImportant) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Penting", tint = StatusWarning, modifier = Modifier.size(14.dp))
                        }
                    }

                    if (!transaction.note.isNullOrBlank()) {
                        Text(
                            text = transaction.note,
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = DateUtils.formatShortDate(transaction.timestamp),
                        fontSize = 11.sp,
                        color = TextSecondaryDark.copy(alpha = 0.7f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$typePrefix${FormatUtils.formatRupiah(transaction.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = typeColor
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = TextSecondaryDark.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
