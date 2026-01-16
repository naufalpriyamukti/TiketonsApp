package com.tiketons.app.ui.views.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Avatar Icon
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -- BAGIAN NAMA & EMAIL --
            when (val state = profileState) {
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Success -> {
                    val user = state.data

                    // Baris Nama + Tombol Edit
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Nama", tint = BluePrimary)
                        }
                    }

                    Text(text = user.email, color = Color.Gray)
                }
                is UiState.Error -> Text("Gagal memuat profil")
                else -> {}
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Tombol Logout
            Button(
                onClick = {
                    viewModel.logout()
                    // Pindah ke Login dan hapus history back stack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Merah
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Keluar Aplikasi", fontWeight = FontWeight.Bold)
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
fun EditNameDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Nama") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onSave(name) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}