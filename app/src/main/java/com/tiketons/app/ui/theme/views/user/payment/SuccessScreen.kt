package com.tiketons.app.ui.views.user.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tiketons.app.R // Pastikan import R sesuai package project kamu
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.utils.CurrencyHelper

@Composable
fun SuccessScreen(
    navController: NavController,
    vaNumber: String,
    paymentType: String,
    amount: String
) {
    val context = LocalContext.current

    // --- LOGIKA PEMILIHAN GAMBAR ---
    val logoResId = when (paymentType.lowercase()) {
        "bca" -> R.drawable.logo_bca
        "bri" -> R.drawable.logo_bri
        "bni" -> R.drawable.logo_bni
        "alfamart" -> R.drawable.logo_alfamart
        else -> R.drawable.ic_launcher_foreground // Default icon jika tidak ditemukan
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ikon Sukses
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Order Berhasil Dibuat!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Segera lakukan pembayaran sebelum waktu habis.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // KARTU INFORMASI PEMBAYARAN
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // Metode Pembayaran (GAMBAR + TEKS)
                Text("Metode Pembayaran", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tampilkan Logo
                    Image(
                        painter = painterResource(id = logoResId),
                        contentDescription = paymentType,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(30.dp) // Tinggi logo disesuaikan
                            .width(60.dp)  // Lebar logo disesuaikan
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Teks Nama Bank
                    Text(
                        text = paymentType.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFEEEEEE))

                // Nomor VA / Kode Bayar
                Text("Nomor Virtual Account / Kode Bayar", fontSize = 12.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = vaNumber,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f) // Agar teks membungkus jika terlalu panjang
                    )
                    // Tombol Copy
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("VA Number", vaNumber)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Disalin!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Total Bayar
                Text("Total Tagihan", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = CurrencyHelper.formatRupiah(amount.toDoubleOrNull() ?: 0.0),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Navigasi
        Button(
            onClick = {
                // Ke Halaman Transaksi untuk cek status
                navController.navigate(Screen.Transaction.route) {
                    // Hapus history agar tidak back ke halaman success
                    popUpTo(Screen.Home.route)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cek Status Transaksi", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        ) {
            Icon(Icons.Default.Home, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kembali ke Beranda")
        }
    }
}