package com.tiketons.app.ui.views.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.modeldata.EventModel
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.components.BottomNavBar
import com.tiketons.app.ui.components.EventCard
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.EventViewModel
import com.tiketons.app.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    // 1. Ambil Context & Factory
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)

    // 2. Inisialisasi ViewModel
    val viewModel: EventViewModel = viewModel(factory = factory)

    // Ambil state dari ViewModel
    val eventState by viewModel.eventState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Controller Keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // Load data awal
    LaunchedEffect(Unit) {
        viewModel.getAllEvents()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFF8F9FA) // Background Putih Abu
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // --- 1. HEADER BIRU (Search Only) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4C4DDC), BluePrimary)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp) // Padding vertikal diperkecil
                ) {
                    Spacer(modifier = Modifier.height(8.dp)) // Jarak atas diperkecil

                    // SEARCH BAR (Tanpa Filter)
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Cari konser...", color = Color.White.copy(alpha = 0.7f)) },
                        // Menggunakan Icon Rounded agar lebih modern
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)), // Efek Glassmorphism
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.searchEvents(searchQuery)
                                keyboardController?.hide()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Jarak bawah diperkecil agar list naik
                }
            }

            // --- 2. KONTEN LIST ---
            Box(modifier = Modifier.weight(1f)) {
                when (val state = eventState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is UiState.Error -> Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                    is UiState.Success -> {
                        EventList(events = state.data, navController = navController)
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun EventList(events: List<EventModel>, navController: NavController) {
    LazyColumn(
        // Padding top diperkecil (12.dp) agar tulisan "Daftar Konser" naik ke atas mendekati header
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
    ) {
        // --- JUDUL DENGAN GARIS TENGAH ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp), // Jarak ke kartu juga diperkecil
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )
                Text(
                    text = "Daftar Konser",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )
            }
        }

        // --- ITEM EVENTS ---
        items(events) { event ->
            EventCard(event = event) {
                navController.navigate(Screen.EventDetail.createRoute(event.id ?: 0))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}