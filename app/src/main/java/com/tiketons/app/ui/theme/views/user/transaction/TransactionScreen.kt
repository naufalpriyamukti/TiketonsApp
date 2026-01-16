package com.tiketons.app.ui.views.user.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.modeldata.TransactionModel
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.components.BottomNavBar
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.TransactionViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory
import com.tiketons.app.utils.CurrencyHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val viewModel: TransactionViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    val historyState by viewModel.historyState.collectAsState()
    var selectedTransaction by remember { mutableStateOf<TransactionModel?>(null) }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFF8F9FA) // Background Putih Abu
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // --- 1. HEADER BIRU GRADASI ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4C4DDC), BluePrimary)
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.ReceiptLong,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Riwayat Transaksi",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // --- 2. LIST TRANSAKSI ---
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = historyState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.message, color = Color.Red)
                            Button(onClick = { viewModel.loadHistory() }) { Text("Coba Lagi") }
                        }
                    }
                    is UiState.Success -> {
                        val transactions = state.data
                        if (transactions.isEmpty()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Belum ada transaksi", color = Color.Gray, style = MaterialTheme.typography.titleMedium)
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(transactions) { trx ->
                                    TransactionCard(trx) {
                                        selectedTransaction = it
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    // Dialog Detail
    if (selectedTransaction != null) {
        PaymentDetailDialog(
            transaction = selectedTransaction!!,
            onDismiss = { selectedTransaction = null }
        )
    }
}

// --- KOMPONEN KARTU TRANSAKSI BARU ---
@Composable
fun TransactionCard(transaction: TransactionModel, onClick: (TransactionModel) -> Unit) {
    val isSuccess = transaction.status == "SUCCESS"
    val statusColor = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFFF9800) // Hijau / Orange
    val statusIcon = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(transaction) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Nama Event & Tanggal (Placeholder)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.eventName ?: "Event Tanpa Nama",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID: ${transaction.orderId.takeLast(10)}", // Tampilkan ID sebagian
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = transaction.status,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))

            // Footer: Info Pembayaran & Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kiri: Metode Bayar & Tribun
                Column {
                    val method = transaction.paymentMethod?.uppercase() ?: "MANUAL"
                    Text(
                        text = "$method • ${transaction.tribun}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Kanan: Harga
                Text(
                    text = CurrencyHelper.formatRupiah(transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- DIALOG DETAIL PEMBAYARAN ---
@Composable
fun PaymentDetailDialog(transaction: TransactionModel, onDismiss: () -> Unit) {
    val bankName = transaction.paymentMethod?.uppercase() ?: "BANK"
    val isSuccess = transaction.status == "SUCCESS"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Header
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isSuccess) "Pembayaran Berhasil" else "Menunggu Pembayaran",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detail Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    DetailRow("Metode Pembayaran", bankName)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isSuccess) {
                        Text("Nomor Virtual Account", fontSize = 12.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = transaction.vaNumber ?: "-",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BluePrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow("Total Tagihan", CurrencyHelper.formatRupiah(transaction.amount), isTotal = true)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = if (isTotal) 16.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isTotal) BluePrimary else Color.Black
        )
    }
}