package com.tiketons.app.ui.theme.views.user.payment

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.ui.common.UiState // 1. FIX: Import UiState
import com.tiketons.app.ui.theme.viewmodel.PaymentViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory

@Composable
fun PaymentScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 1. Ambil Factory
    val factory = ViewModelFactory.getInstance(context)

    // 2. Load ViewModel
    val viewModel: PaymentViewModel = viewModel(factory = factory)

    // 3. FIX: Ganti 'state' jadi 'paymentState'
    val state by viewModel.paymentState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    // Data Dummy
    val radioOptions = listOf("bca", "bri", "alfamart")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    // 4. FIX: Logic UiState (Ganti PaymentState jadi UiState)
    LaunchedEffect(state) {
        when (val currentState = state) { // Gunakan variable baru untuk smart cast
            is UiState.Loading -> isLoading = true
            is UiState.Success -> {
                isLoading = false

                // Ambil data dari response backend
                val response = currentState.data
                val vaNumber = response.data?.vaNumber ?: "-"

                Toast.makeText(context, "VA Berhasil: $vaNumber", Toast.LENGTH_LONG).show()

                // Opsi: Navigasi ke halaman sukses jika perlu
                // navController.navigate(...)
            }
            is UiState.Error -> {
                isLoading = false
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }
            else -> isLoading = false
        }
    }

    // TAMPILAN UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Pembayaran Tiket", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Info Tagihan
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Pembayaran", style = MaterialTheme.typography.labelLarge)
                Text("Rp 50.000", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Pilih Metode:", fontWeight = FontWeight.SemiBold)

        // Radio Buttons
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) }
                    )
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) }
                )
                Text(
                    text = text.uppercase(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Bayar
        Button(
            onClick = {
                // 5. Panggil fungsi di ViewModel
                viewModel.processPayment(
                    orderId = "ORDER-${System.currentTimeMillis()}",
                    amount = 50000,
                    paymentType = selectedOption,
                    eventId = "1", // Pastikan ID event valid (String angka)
                    tribunName = "VIP"
                )
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if(isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            else Text("BAYAR SEKARANG")
        }
    }
}