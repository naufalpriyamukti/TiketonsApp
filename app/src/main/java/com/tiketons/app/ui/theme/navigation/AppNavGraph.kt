package com.tiketons.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Import Screens
import com.tiketons.app.ui.views.auth.LoginScreen
import com.tiketons.app.ui.views.auth.RegisterScreen
import com.tiketons.app.ui.views.auth.SplashScreen
import com.tiketons.app.ui.views.user.HomeScreen
import com.tiketons.app.ui.views.user.EventDetailScreen
import com.tiketons.app.ui.payment.PaymentMethodScreen // Import PaymentScreen
import com.tiketons.app.ui.views.user.payment.SuccessScreen
import com.tiketons.app.ui.views.user.transaction.TransactionScreen
import com.tiketons.app.ui.views.user.ticket.TicketScreen
import com.tiketons.app.ui.views.user.ticket.ETicketScreen
import com.tiketons.app.ui.views.user.profile.ProfileScreen
import com.tiketons.app.ui.views.admin.AdminHomeScreen
import com.tiketons.app.ui.views.admin.AdminEventCrudScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- AUTH ROUTES ---
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }

        // --- USER ROUTES ---
        composable(Screen.Home.route) { HomeScreen(navController) }

        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) {
            val eventId = it.arguments?.getInt("eventId") ?: 0
            EventDetailScreen(navController, eventId)
        }

        // --- FIX BAGIAN PAYMENT (TAMBAHKAN eventName) ---
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.IntType },
                // 1. Tambahkan Argument eventName
                navArgument("eventName") { type = NavType.StringType },
                navArgument("tribun") { type = NavType.StringType },
                navArgument("price") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            // 2. Ambil Argument
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            val eventName = backStackEntry.arguments?.getString("eventName") ?: "Event Tiketons"
            val tribun = backStackEntry.arguments?.getString("tribun") ?: ""
            val price = backStackEntry.arguments?.getFloat("price")?.toDouble() ?: 0.0

            // 3. Panggil Screen dengan parameter lengkap
            PaymentMethodScreen(
                navController = navController,
                eventId = eventId,
                eventName = eventName, // <--- KIRIM KE SINI
                tribun = tribun,
                price = price
            )
        }

        // --- FIX BAGIAN SUCCESS (TAMBAHKAN Argument VA, dll) ---
        // Kita perlu menangkap data yang dikirim dari PaymentMethodScreen
        composable(
            route = Screen.Success.route,
            arguments = listOf(
                navArgument("vaNumber") { type = NavType.StringType },
                navArgument("paymentType") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vaNumber = backStackEntry.arguments?.getString("vaNumber") ?: "-"
            val paymentType = backStackEntry.arguments?.getString("paymentType") ?: "-"
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"

            SuccessScreen(
                navController = navController,
                vaNumber = vaNumber,
                paymentType = paymentType,
                amount = amount
            )
        }

        composable(Screen.Transaction.route) { TransactionScreen(navController) }
        composable(Screen.Ticket.route) { TicketScreen(navController) }

        composable(
            route = Screen.ETicket.route,
            arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
        ) {
            val ticketId = it.arguments?.getInt("ticketId") ?: 0
            ETicketScreen(navController, ticketId)
        }

        composable(Screen.Profile.route) { ProfileScreen(navController) }

        // --- ADMIN ROUTES ---
        composable(Screen.AdminHome.route) { AdminHomeScreen(navController) }
        composable(Screen.AdminEventCrud.route) { AdminEventCrudScreen(navController) }
    }
}