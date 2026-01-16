package com.tiketons.app.ui.views.user.ticket

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.modeldata.TicketModel
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.components.BottomNavBar
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.TicketViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory
import com.tiketons.app.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(navController: NavController) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val viewModel: TicketViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        viewModel.loadTickets()
    }

    val ticketState by viewModel.ticketState.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFF8F9FA)
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
                        imageVector = Icons.Rounded.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tiket Saya",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // --- 2. LIST TIKET ---
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = ticketState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Gagal memuat tiket", color = Color.Red)
                            Text(text = state.message, fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadTickets() }) { Text("Coba Lagi") }
                        }
                    }
                    is UiState.Success -> {
                        val tickets = state.data
                        if (tickets.isEmpty()) {
                            EmptyTicketState()
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(tickets) { ticket ->
                                    TicketCardItem(ticket = ticket) {
                                        navController.navigate(Screen.ETicket.createRoute(ticket.id ?: 0))
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
}

// --- PERBAIKAN: Ganti nama class menjadi TicketListShape ---
class TicketListShape(private val cornerRadius: Float, private val cutoutRadius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(Path().apply {
            val w = size.width
            val h = size.height
            val stubPosition = w * 0.72f // Posisi cekungan di 72% lebar kartu

            reset()
            moveTo(cornerRadius, 0f)
            lineTo(stubPosition - cutoutRadius, 0f)
            arcTo(
                rect = Rect(
                    left = stubPosition - cutoutRadius,
                    top = -cutoutRadius,
                    right = stubPosition + cutoutRadius,
                    bottom = cutoutRadius
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            lineTo(w - cornerRadius, 0f)
            arcTo(Rect(w - 2 * cornerRadius, 0f, w, 2 * cornerRadius), 270f, 90f, false)
            lineTo(w, h - cornerRadius)
            arcTo(Rect(w - 2 * cornerRadius, h - 2 * cornerRadius, w, h), 0f, 90f, false)
            lineTo(stubPosition + cutoutRadius, h)
            arcTo(
                rect = Rect(
                    left = stubPosition - cutoutRadius,
                    top = h - cutoutRadius,
                    right = stubPosition + cutoutRadius,
                    bottom = h + cutoutRadius
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            lineTo(cornerRadius, h)
            arcTo(Rect(0f, h - 2 * cornerRadius, 2 * cornerRadius, h), 90f, 90f, false)
            lineTo(0f, cornerRadius)
            arcTo(Rect(0f, 0f, 2 * cornerRadius, 2 * cornerRadius), 180f, 90f, false)
            close()
        })
    }
}

@Composable
fun TicketCardItem(ticket: TicketModel, onClick: () -> Unit) {
    val density = LocalContext.current.resources.displayMetrics.density
    val cornerRadius = 16.dp.value * density
    val cutoutRadius = 10.dp.value * density

    // Gunakan Class yang sudah direname
    val ticketShape = TicketListShape(cornerRadius, cutoutRadius)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(elevation = 6.dp, shape = ticketShape)
            .clickable { onClick() },
        shape = ticketShape,
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stubPosition = size.width * 0.72f
                drawLine(
                    color = Color.LightGray,
                    start = Offset(stubPosition, cutoutRadius + 5f),
                    end = Offset(stubPosition, size.height - cutoutRadius - 5f),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(0.72f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = ticket.eventName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(Icons.Rounded.CalendarToday, DateFormatter.formatDate(ticket.eventDate ?: ""))
                        Spacer(modifier = Modifier.height(6.dp))
                        InfoRow(Icons.Rounded.LocationOn, ticket.location ?: "Lokasi Belum Ditentukan")
                    }

                    Surface(
                        color = BluePrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = ticket.tribun.uppercase(),
                            color = BluePrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(0.28f)
                        .fillMaxHeight()
                        .background(Color(0xFFF8F9FA))
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val isUsed = ticket.isUsed
                    val statusColor = if (isUsed) Color.Red else Color(0xFF00C853)
                    val statusText = if (isUsed) "TERPAKAI" else "AKTIF"

                    Box(modifier = Modifier.rotate(-90f)) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

// ... (Sisa fungsi helper seperti InfoRow dan EmptyTicketState sama) ...
@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun EmptyTicketState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.ConfirmationNumber,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum Ada Tiket",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = "Tiket yang kamu beli akan muncul di sini.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}