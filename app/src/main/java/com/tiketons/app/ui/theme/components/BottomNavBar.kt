package com.tiketons.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tiketons.app.ui.navigation.Screen
import com.tiketons.app.ui.theme.BluePrimary

@Composable
fun BottomNavBar(navController: NavController) {
    // Definisi Item (Tetap menggunakan ikon modern)
    val items = listOf(
        BottomNavItem(
            title = "Home",
            route = Screen.Home.route,
            selectedIcon = Icons.Rounded.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            title = "Tiket",
            route = Screen.Ticket.route,
            selectedIcon = Icons.Rounded.ConfirmationNumber,
            unselectedIcon = Icons.Outlined.ConfirmationNumber
        ),
        BottomNavItem(
            title = "Transaksi",
            route = Screen.Transaction.route,
            selectedIcon = Icons.Rounded.ReceiptLong,
            unselectedIcon = Icons.Outlined.ReceiptLong
        ),
        BottomNavItem(
            title = "Profil",
            route = Screen.Profile.route,
            selectedIcon = Icons.Rounded.Person,
            unselectedIcon = Icons.Outlined.Person
        ),
    )

    // NAVBAR FULL WIDTH (Tidak Melayang)
    // Menggunakan shadow di bagian atas untuk memisahkan dari konten
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp) // Shadow halus ke atas
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        // Logika ganti icon (Solid vs Outline)
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(26.dp)
                    )
                },
                // Label KITA HAPUS (return null/empty) agar indikator pill pas di tengah
                // Ini membuat tampilan ikon lebih besar dan "clean"
                label = { },

                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    // KEADAAN AKTIF
                    selectedIconColor = Color.White, // Ikon jadi Putih
                    indicatorColor = BluePrimary,    // Kapsul background jadi Biru

                    // KEADAAN TIDAK AKTIF
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Transparent
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)