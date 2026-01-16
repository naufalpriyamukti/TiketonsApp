package com.tiketons.app.ui.views.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tiketons.app.ui.common.UiState
import com.tiketons.app.ui.components.BottomNavBar
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.ui.theme.BluePrimary
import com.tiketons.app.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    // Load data saat pertama buka
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFF8F9FA) // Background Abu-abu terang
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. HEADER BIRU GRADASI ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Tinggi header
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4C4DDC), BluePrimary)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profil Pengguna",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Kelola informasi akun anda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // --- 2. KARTU PROFIL (OVERLAPPING HEADER) ---
            // Menggeser kartu ke atas agar menumpuk header (efek modern)
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-50).dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar Icon
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .background(BluePrimary.copy(alpha = 0.1f), CircleShape)
                                .padding(10.dp),
                            tint = BluePrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // State Handling
                        when (val state = profileState) {
                            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            is UiState.Success -> {
                                val user = state.data

                                // Nama User
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = user.fullName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    IconButton(
                                        onClick = { showEditDialog = true },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = BluePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Email User
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(24.dp))
                                Divider(color = Color(0xFFEEEEEE))
                                Spacer(modifier = Modifier.height(24.dp))

                                // Info Tambahan (Opsional)
                                ProfileInfoRow(label = "Username", value = "@${user.email.split("@")[0]}")
                            }
                            is UiState.Error -> {
                                Text("Gagal memuat profil", color = Color.Red)
                                Button(onClick = { viewModel.loadProfile() }) { Text("Coba Lagi") }
                            }
                            else -> {}
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 3. TOMBOL LOGOUT ---
                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar Aplikasi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // -- DIALOG EDIT NAMA --
        if (showEditDialog) {
            EditNameDialog(
                currentName = (profileState as? UiState.Success)?.data?.fullName ?: "",
                onDismiss = { showEditDialog = false },
                onSave = { newName ->
                    viewModel.updateName(newName)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun EditNameDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Ubah Nama Lengkap", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    focusedLabelColor = BluePrimary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(name) },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        }
    )
}