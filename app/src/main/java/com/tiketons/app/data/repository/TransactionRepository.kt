package com.tiketons.app.data.repository

import android.util.Log
import com.tiketons.app.data.remote.PaymentApiService
import com.tiketons.app.data.remote.SupabaseClient
import com.tiketons.app.modeldata.PaymentRequest
import com.tiketons.app.modeldata.PaymentResponse
import com.tiketons.app.modeldata.TransactionModel
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonPrimitive

class TransactionRepository(private val apiService: PaymentApiService) {

    private val db = SupabaseClient.client
    private val auth = SupabaseClient.client.auth

    // --- 1. REQUEST PEMBAYARAN KE BACKEND ---
    suspend fun requestPayment(
        eventId: Int,
        eventName: String, // Parameter ini sudah ada, tapi sebelumnya tidak dikirim
        amount: Double,
        paymentMethod: String,
        tribun: String
    ): Result<PaymentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // A. Cek Login User
                val user = auth.currentUserOrNull() ?: throw Exception("User belum login")

                // B. Ambil Nama User (Metadata)
                val customerName = try {
                    user.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: "Customer"
                } catch (e: Exception) { "Customer" }

                val userEmail = user.email ?: "email@example.com"

                // PENTING: Order ID dikosongkan.
                // Biarkan Server Node.js yang generate (4-6 digit angka)
                // supaya VA BCA di Simulator tidak error (kepanjangan).
                val orderId = ""

                // C. Buat Request Body
                val request = PaymentRequest(
                    orderId = orderId,
                    amount = amount.toInt(),
                    paymentType = paymentMethod,
                    customerName = customerName,
                    customerEmail = userEmail,
                    userId = user.id,
                    eventId = eventId.toString(),

                    // --- PERBAIKAN UTAMA DI SINI ---
                    // Kirim nama event ke backend supaya tersimpan di database
                    eventName = eventName,

                    tribunName = tribun
                )

                // D. Panggil API Node.js
                val response = apiService.createTransaction(request)

                // E. Cek Response
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status) {
                        // Sukses, Backend sudah handle save DB & Midtrans
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal memproses transaksi"))
                    }
                } else {
                    Result.failure(Exception("Server Error: ${response.code()} ${response.message()}"))
                }

            } catch (e: Exception) {
                Log.e("TransactionRepo", "Error requestPayment: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // --- 2. AMBIL RIWAYAT TRANSAKSI (LOAD HISTORY) ---
    suspend fun loadHistory(): List<TransactionModel> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUserOrNull()?.id ?: return@withContext emptyList()

                db.from("transactions")
                    .select {
                        filter { eq("user_id", userId) }
                        order("created_at", order = Order.DESCENDING)
                    }
                    .decodeList<TransactionModel>()

            } catch (e: Exception) {
                Log.e("TransactionRepo", "Error loadHistory: ${e.message}")
                emptyList()
            }
        }
    }
}