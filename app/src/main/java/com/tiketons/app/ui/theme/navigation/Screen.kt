package com.tiketons.app.ui.navigation

sealed class Screen(val route: String) {
    // Auth Routes
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // User Routes
    object Home : Screen("home")
    object Transaction : Screen("transaction")
    object Ticket : Screen("ticket")
    object Profile : Screen("profile")

    // Detail Routes (dengan parameter)
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: Int) = "event_detail/$eventId"
    }

    // --- UPDATE PENTING DI SINI ---
    // Input Pembayaran: Kita TAMBAHKAN parameter {eventName}
    // Agar bisa dikirim dari EventDetail -> PaymentScreen -> Backend
    object Payment : Screen("payment/{eventId}/{eventName}/{tribun}/{price}") {
        fun createRoute(eventId: Int, eventName: String, tribun: String, price: Double): String {
            return "payment/$eventId/$eventName/$tribun/${price.toFloat()}"
        }
    }

    // Hasil Pembayaran (VA Display)
    object Success : Screen("success/{vaNumber}/{paymentType}/{amount}") {
        fun createRoute(vaNumber: String, paymentType: String, amount: String) = "success/$vaNumber/$paymentType/$amount"
    }

    object ETicket : Screen("eticket/{ticketId}") {
        fun createRoute(ticketId: Int) = "eticket/$ticketId"
    }

    // Admin Routes
    object AdminHome : Screen("admin_home")
    object AdminEventCrud : Screen("admin_event_crud")
}