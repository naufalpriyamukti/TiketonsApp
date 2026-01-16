package com.tiketons.app.ui.payment

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tiketons.app.R // Pastikan import R sesuai package project kamu
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.TransactionViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentMethodScreen(
    navController: NavController,
    eventId: Int,
    eventName: String,
    tribun: String,
    price: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val viewModel: TransactionViewModel = viewModel(factory = factory)
    val state by viewModel.paymentState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    // List Metode Pembayaran
    val paymentMethods = listOf("bca", "bri", "bni", "alfamart")
    val (selectedMethod, onMethodSelected) = remember { mutableStateOf(paymentMethods[0]) }

    val formattedPrice = remember(price) {
        try {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            format.format(price)
        } catch (e: Exception) { "Rp $price" }
    }

    // --- LOGIKA NAVIGASI ---
    LaunchedEffect(state) {
        when (val currentState = state) {
            is UiState.Loading -> isLoading = true
            is UiState.Success -> {
                isLoading = false
                val response = currentState.data.data
                if (response != null) {
                    Toast.makeText(context, "Order Dibuat!", Toast.LENGTH_SHORT).show()
                    navController.navigate(
                        Screen.Success.createRoute(
                            vaNumber = response.vaNumber ?: "Error VA",
                            paymentType = response.paymentType ?: selectedMethod,
                            amount = response.totalAmount ?: "0"
                        )
                    ) { popUpTo(Screen.Home.route) { saveState = true } }
                }
            }
            is UiState.Error -> {
                isLoading = false
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }
            else -> isLoading = false
        }
    }

    // --- UI UTAMA ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Background Abu Putih
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. JUDUL CENTER
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Pilih Pembayaran",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 2. KARTU RINGKASAN TAGIHAN
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Detail Pesanan", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = eventName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFEEEEEE))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kategori Tiket", color = Color.Gray)
                    Text(tribun, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Tagihan", color = Color.Gray)
                    Text(
                        text = formattedPrice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary // Warna Biru Harga
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. PILIHAN METODE PEMBAYARAN (GAMBAR)
        Text(
            "Metode Pembayaran",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            paymentMethods.forEach { method ->
                PaymentOptionItem(
                    methodName = method,
                    isSelected = selectedMethod == method,
                    onSelect = { onMethodSelected(method) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        // 4. TOMBOL BAYAR
        Button(
            onClick = {
                viewModel.createTransaction(eventId, eventName, price, selectedMethod, tribun)
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Bayar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- ITEM KARTU PEMBAYARAN ---
@Composable
fun PaymentOptionItem(
    methodName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    // Tentukan Logo berdasarkan nama method
    // PASTIKAN FILE GAMBAR ADA DI DRAWABLE: logo_bca.png, logo_bri.png, dst.
    val logoResId = when (methodName) {
        "bca" -> R.drawable.logo_bca         // Pastikan file ini ada
        "bri" -> R.drawable.logo_bri         // Pastikan file ini ada
        "bni" -> R.drawable.logo_bni         // Pastikan file ini ada
        "alfamart" -> R.drawable.logo_alfamart // Pastikan file ini ada
        else -> R.drawable.ic_launcher_foreground // Default jika gambar tidak ada
    }

    val backgroundColor = if (isSelected) BluePrimary.copy(alpha = 0.05f) else Color.White
    val borderColor = if (isSelected) BluePrimary else Color(0xFFEEEEEE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp) // --- UBAH TINGGI KARTU DISINI ---
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(width = if (isSelected) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Area Logo
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = methodName,
            contentScale = ContentScale.Fit, // Agar gambar pas di dalam box
            modifier = Modifier
                .width(80.dp)   // --- UBAH LEBAR LOGO DISINI ---
                .height(40.dp)  // --- UBAH TINGGI LOGO DISINI ---
        )

        // Nama Metode (Teks di Kanan)
        Text(
            text = methodName.uppercase(),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BluePrimary else Color.Gray,
            fontSize = 14.sp
        )
    }
}

// --- PREVIEW (UNTUK EDIT UKURAN) ---
@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    // Ini cuma tampilan dummy untuk melihat ukuran gambar
    // Tidak akan error walau tanpa data asli
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF8F9FA))
    ) {
        Text("Preview Ukuran Gambar", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Contoh Item (Silakan atur ukuran di fun PaymentOptionItem di atas)
        PaymentOptionItem(methodName = "bca", isSelected = true, onSelect = {})
        Spacer(modifier = Modifier.height(10.dp))
        PaymentOptionItem(methodName = "alfamart", isSelected = false, onSelect = {})
    }
}