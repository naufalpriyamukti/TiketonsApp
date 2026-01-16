package com.tiketons.app.ui.views.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.data.remote.SupabaseClient
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.AdminViewModel
import com.tiketons.app.ui.viewmodel.EventViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController
    // HAPUS ViewModel dari parameter agar tidak error Context
) {
    // 1. Ambil Context
    val context = LocalContext.current

    // 2. Buat Factory dengan Context
    val factory = ViewModelFactory.getInstance(context)

    // 3. Inisialisasi ViewModel di sini
    val viewModel: AdminViewModel = viewModel(factory = factory)
    val eventViewModel: EventViewModel = viewModel(factory = factory)

    // Load data event saat dibuka
    LaunchedEffect(Unit) { eventViewModel.getAllEvents() }

    val eventState by eventViewModel.eventState.collectAsState()
    val adminState by viewModel.adminState.collectAsState()

    // Handle Delete Success
    LaunchedEffect(adminState) {
        if (adminState is UiState.Success) {
            Toast.makeText(context, "Operasi Berhasil", Toast.LENGTH_SHORT).show()
            eventViewModel.getAllEvents() // Refresh list
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = {
                        // Logout Logic
                        // Sebaiknya logout juga via ViewModel, tapi ini sementara oke
                        kotlinx.coroutines.GlobalScope.launch {
                            try {
                                SupabaseClient.client.auth.signOut()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AdminEventCrud.route) },
                containerColor = BluePrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event", tint = Color.White)
            }
        }
    ) { p ->
        Box(Modifier.padding(p).fillMaxSize()) {
            when (val state = eventState) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(state.data) { event ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(event.name, fontWeight = FontWeight.Bold)
                                        Text(event.date, style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = {
                                        if (event.id != null) viewModel.deleteEvent(event.id)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> Text(state.message, Modifier.align(Alignment.Center))
                else -> {}
            }
        }
    }
}