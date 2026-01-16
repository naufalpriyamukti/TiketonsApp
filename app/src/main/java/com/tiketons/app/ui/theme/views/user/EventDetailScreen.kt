package com.tiketons.app.ui.views.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tiketons.app.modeldata.EventModel
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.EventViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory
import com.tiketons.app.utils.CurrencyHelper
import com.tiketons.app.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavController,
    eventId: Int
) {
    // 1. Ambil Context & Factory
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)

    // 2. Init ViewModel
    val viewModel: EventViewModel = viewModel(factory = factory)
    val eventState by viewModel.eventState.collectAsState()

    // Cari event berdasarkan ID
    val event = (eventState as? UiState.Success)?.data?.find { it.id == eventId }

    if (event == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Scaffold(
        // Bottom Bar Melayang
        bottomBar = {
            BottomBuyBar(event, navController)
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding) // Padding dari scaffold agar tidak tertutup sistem nav
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. HEADER IMAGE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                                startY = 200f
                            )
                        )
                )

                // Tombol Back
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(top = 48.dp, start = 20.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            // --- 2. KONTEN DETAIL ---
            Column(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                // Judul
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.headlineSmall, // Ukuran font sedikit dikecilkan agar pas
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info Waktu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CalendarToday, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${DateFormatter.formatDate(event.date)} • ${event.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Info Lokasi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 20.dp), color = Color(0xFFF0F0F0))

                // Penyelenggara
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF5F5F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Store, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Penyelenggara", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = event.vendorName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Deskripsi
                Text("Tentang Event", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                // --- PENTING: Spacer Besar di Bawah ---
                // Agar teks deskripsi bisa discroll sampai ke atas Bottom Bar
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
fun BottomBuyBar(event: EventModel, navController: NavController) {
    var selectedTribun by remember { mutableStateOf("Regular") }
    var price by remember { mutableStateOf(event.priceRegular) }

    // Floating Card yang Lebih Ringkas
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp) // Padding luar dikurangi
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Padding dalam dikurangi dari 20 jadi 16
        ) {
            // Baris 1: Pilihan Tiket (Compact)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kategori:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))

                // Chips
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TicketChoiceChip(
                        label = "Regular",
                        isSelected = selectedTribun == "Regular",
                        onClick = { selectedTribun = "Regular"; price = event.priceRegular },
                        modifier = Modifier.weight(1f)
                    )
                    TicketChoiceChip(
                        label = "VIP",
                        isSelected = selectedTribun == "VIP",
                        onClick = { selectedTribun = "VIP"; price = event.priceVip },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp)) // Jarak antar baris diperkecil

            Divider(color = Color(0xFFF0F0F0))

            Spacer(modifier = Modifier.height(12.dp))

            // Baris 2: Harga & Tombol Beli
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Harga", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = CurrencyHelper.formatRupiah(price),
                        style = MaterialTheme.typography.titleMedium, // Ukuran font pas
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        navController.navigate(
                            Screen.Payment.createRoute(
                                eventId = event.id ?: 0,
                                eventName = event.name,
                                tribun = selectedTribun,
                                price = price
                            )
                        )
                    },
                    modifier = Modifier
                        .height(42.dp) // Tombol lebih pendek (Compact)
                        .width(130.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Beli Tiket", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// Komponen Chip Custom (Lebih Tipis)
@Composable
fun TicketChoiceChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp) // Tinggi Chip dikurangi agar tidak memakan tempat
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) BluePrimary.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
            .border(
                width = 1.dp,
                color = if (isSelected) BluePrimary else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) BluePrimary else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}