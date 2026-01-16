package com.tiketons.app.ui.views.user.ticket

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.R
import com.tiketons.app.modeldata.TicketModel
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.TicketViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory
import com.tiketons.app.utils.CurrencyHelper
import com.tiketons.app.utils.DateFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ETicketScreen(
    navController: NavController,
    ticketId: Int
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val viewModel: TicketViewModel = viewModel(factory = factory)

    LaunchedEffect(ticketId) { viewModel.loadTicketDetail(ticketId) }
    val ticketState by viewModel.selectedTicket.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("E-Ticket", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val state = ticketState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red)
                        Button(onClick = { viewModel.loadTicketDetail(ticketId) }) { Text("Coba Lagi") }
                    }
                }
                is UiState.Success -> {
                    val ticket = state.data
                    ETicketContent(ticket = ticket)
                }
                else -> {}
            }
        }
    }
}

// --- LOGIC HELPER UNTUK CEK TANGGAL SELESAI ---
fun getTicketStatus(ticket: TicketModel): Pair<String, Color> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val eventDate = try {
        sdf.parse(ticket.eventDate ?: "")
    } catch (e: Exception) { null }

    if (eventDate != null) {
        val twoDaysAfterEvent = Date(eventDate.time + (2L * 24 * 60 * 60 * 1000))
        val today = Date()

        if (today.after(twoDaysAfterEvent)) {
            return Pair("SELESAI", Color.Gray)
        }
    }

    return if (ticket.isUsed) {
        Pair("TERPAKAI", Color.Red)
    } else {
        Pair("AKTIF", Color(0xFF00C853)) // Hijau
    }
}

// --- CUSTOM SHAPE DETAIL TIKET (GELOMBANG KANAN) ---
// Perbaikan: Menggunakan androidx.compose.ui.graphics.Shape
class ETicketDetailShape(private val cornerRadius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(Path().apply {
            val w = size.width
            val h = size.height
            val waveRadius = cornerRadius / 2

            reset()
            // Kiri Atas
            arcTo(Rect(0f, 0f, 2 * cornerRadius, 2 * cornerRadius), 180f, 90f, false)
            lineTo(w - cornerRadius, 0f)
            // Kanan Atas
            arcTo(Rect(w - 2 * cornerRadius, 0f, w, 2 * cornerRadius), 270f, 90f, false)

            // Sisi Kanan Bergelombang
            var currentY = cornerRadius
            while (currentY < h - cornerRadius) {
                arcTo(
                    rect = Rect(w - waveRadius, currentY, w + waveRadius, currentY + 2 * waveRadius),
                    startAngleDegrees = 270f, sweepAngleDegrees = -180f, forceMoveTo = false
                )
                currentY += 2 * waveRadius
            }

            // Kanan Bawah
            lineTo(w, h - cornerRadius)
            arcTo(Rect(w - 2 * cornerRadius, h - 2 * cornerRadius, w, h), 0f, 90f, false)
            // Bawah
            lineTo(cornerRadius, h)
            // Kiri Bawah
            arcTo(Rect(0f, h - 2 * cornerRadius, 2 * cornerRadius, h), 90f, 90f, false)
            // Sisi Kiri Lurus
            lineTo(0f, cornerRadius)
            close()
        })
    }
}

@Composable
fun ETicketContent(ticket: TicketModel) {
    val density = LocalContext.current.resources.displayMetrics.density
    val cornerRadius = 16.dp.value * density
    val ticketShape = ETicketDetailShape(cornerRadius)

    val (statusText, statusColor) = getTicketStatus(ticket)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = ticketShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {

                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_tiketons),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TIKETONS", style = MaterialTheme.typography.titleMedium, color = BluePrimary, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }

                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // EVENT NAME
                Text(
                    text = ticket.eventName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // INFO UTAMA
                DetailRow(Icons.Rounded.CalendarToday, DateFormatter.formatDate(ticket.eventDate ?: ""))
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(Icons.Rounded.LocationOn, ticket.location ?: "Lokasi belum ditentukan")

                Spacer(modifier = Modifier.height(24.dp))

                // GARIS PUTUS-PUTUS (FIX IMPORTS)
                Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // INFO DETAIL TIKET
                Text("Nama Customer", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = ticket.userName ?: "-",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Order ID", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = ticket.transactionId?.uppercase() ?: "-",
                            fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Kategori", fontSize = 12.sp, color = Color.Gray)
                        Text(ticket.tribun, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BluePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Total Harga", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = CurrencyHelper.formatRupiah(ticket.amount ?: 0.0),
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BluePrimary
                )

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Syarat & Ketentuan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Tunjukkan E-Ticket ini di pintu masuk.\n" +
                            "• Tiket yang sudah dipindai tidak berlaku lagi.\n" +
                            "• Dilarang membawa senjata & obat terlarang.",
                    style = MaterialTheme.typography.bodySmall, color = Color.Gray, lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}