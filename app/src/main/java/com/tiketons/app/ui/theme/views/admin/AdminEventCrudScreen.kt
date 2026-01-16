package com.tiketons.app.ui.views.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.components.CustomButton
import com.tiketons.app.ui.components.CustomTextField
import com.tiketons.app.ui.viewmodel.AdminViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventCrudScreen(
    navController: NavController
    // HAPUS parameter viewModel dari sini agar tidak error Context
) {
    // 1. Ambil Context dulu
    val context = LocalContext.current

    // 2. Panggil Factory dengan Context (Gunakan getInstance)
    val factory = ViewModelFactory.getInstance(context)

    // 3. Inisialisasi ViewModel di sini
    val viewModel: AdminViewModel = viewModel(factory = factory)

    val adminState by viewModel.adminState.collectAsState()

    // Observe state dari ViewModel
    val name = viewModel.eventName.collectAsState()
    val desc = viewModel.eventDesc.collectAsState()
    val date = viewModel.eventDate.collectAsState()
    val time = viewModel.eventTime.collectAsState()
    val loc = viewModel.eventLoc.collectAsState()
    val vendor = viewModel.eventVendor.collectAsState()
    val priceReg = viewModel.priceReg.collectAsState()
    val priceVip = viewModel.priceVip.collectAsState()
    val imgUrl = viewModel.imageUrl.collectAsState()

    LaunchedEffect(adminState) {
        if (adminState is UiState.Success) {
            Toast.makeText(context, "Event Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.popBackStack()
        }
        if (adminState is UiState.Error) {
            Toast.makeText(context, (adminState as UiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Event Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CustomTextField(value = name.value, onValueChange = { viewModel.eventName.value = it }, label = "Nama Event")
            Spacer(Modifier.height(8.dp))
            CustomTextField(value = desc.value, onValueChange = { viewModel.eventDesc.value = it }, label = "Deskripsi")
            Spacer(Modifier.height(8.dp))
            Row {
                Box(Modifier.weight(1f)) {
                    CustomTextField(value = date.value, onValueChange = { viewModel.eventDate.value = it }, label = "Tgl (YYYY-MM-DD)")
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    CustomTextField(value = time.value, onValueChange = { viewModel.eventTime.value = it }, label = "Jam (HH:MM)")
                }
            }
            Spacer(Modifier.height(8.dp))
            CustomTextField(value = loc.value, onValueChange = { viewModel.eventLoc.value = it }, label = "Lokasi")
            Spacer(Modifier.height(8.dp))
            CustomTextField(value = vendor.value, onValueChange = { viewModel.eventVendor.value = it }, label = "Nama Vendor")
            Spacer(Modifier.height(8.dp))
            Row {
                Box(Modifier.weight(1f)) {
                    CustomTextField(value = priceReg.value, onValueChange = { viewModel.priceReg.value = it }, label = "Harga Regular")
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    CustomTextField(value = priceVip.value, onValueChange = { viewModel.priceVip.value = it }, label = "Harga VIP")
                }
            }
            Spacer(Modifier.height(8.dp))
            CustomTextField(value = imgUrl.value, onValueChange = { viewModel.imageUrl.value = it }, label = "URL Gambar")

            Spacer(Modifier.height(24.dp))

            CustomButton(
                text = "Simpan Event",
                onClick = { viewModel.createEvent() },
                isLoading = adminState is UiState.Loading
            )
        }
    }
}