package com.tiketons.app.data.repository

import android.util.Log
import com.tiketons.app.data.remote.SupabaseClient
import com.tiketons.app.modeldata.TicketModel
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TicketRepository {

    private val db = SupabaseClient.client
    private val auth = SupabaseClient.client.auth

    // Ambil tiket milik user yang sedang login
    suspend fun getMyTickets(): List<TicketModel> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUserOrNull()?.id ?: return@withContext emptyList()

                // Mengambil data dari tabel 'tickets'
                db.from("tickets").select {
                    filter {
                        eq("user_id", userId)
                    }
                    // PENTING: Urutkan dari yang terbaru (Created At Descending)
                    // Supaya tiket yang barusan dibeli muncul paling atas
                    order("created_at", order = Order.DESCENDING)
                }.decodeList<TicketModel>()

            } catch (e: Exception) {
                Log.e("TicketRepository", "Error getMyTickets: ${e.message}")
                emptyList()
            }
        }
    }

    // Ambil detail tiket berdasarkan ID (untuk scan QR atau Detail E-Ticket)
    suspend fun getTicketById(ticketId: Int): TicketModel? {
        return withContext(Dispatchers.IO) {
            try {
                db.from("tickets").select {
                    filter { eq("id", ticketId) }
                }.decodeSingleOrNull<TicketModel>()
            } catch (e: Exception) {
                Log.e("TicketRepository", "Error getTicketById: ${e.message}")
                null
            }
        }
    }

    // --- SINGLETON PATTERN ---
    // Diperlukan agar Injection.kt bisa memanggil TicketRepository.getInstance()
    companion object {
        @Volatile
        private var instance: TicketRepository? = null

        fun getInstance(): TicketRepository =
            instance ?: synchronized(this) {
                instance ?: TicketRepository().also { instance = it }
            }
    }
}