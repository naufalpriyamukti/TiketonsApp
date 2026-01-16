package com.tiketons.app.data.di

import android.content.Context
import com.tiketons.app.data.preferences.UserPreference
import com.tiketons.app.data.preferences.dataStore
import com.tiketons.app.data.remote.PaymentApiService
import com.tiketons.app.data.repository.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {

    // ⚠️ PENTING: GANTI URL INI DENGAN URL NODE.JS RAILWAY KAMU YANG ASLI
    // Contoh: "https://tiketons-production.up.railway.app/" (Jangan lupa akhiran slash /)
    private const val BASE_URL = "https://my-node-payment-production.up.railway.app/"

    // 1. User Repository
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    // 2. Auth Repository
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return AuthRepository.getInstance(pref)
    }

    // 3. Payment API Service (Retrofit)
    fun providePaymentApiService(): PaymentApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PaymentApiService::class.java)
    }

    // 4. Event Repository
    fun provideEventRepository(): EventRepository {
        return EventRepository.getInstance()
    }

    // 5. Transaction Repository
    // --- PERBAIKAN DI SINI ---
    // Tambahkan parameter (context: Context) agar cocok dengan ViewModelFactory
    fun provideTransactionRepository(context: Context): TransactionRepository {
        // Meskipun context tidak dipakai di dalam sini,
        // parameter ini wajib ada karena ViewModelFactory mengirimnya.
        val apiService = providePaymentApiService()
        return TransactionRepository(apiService)
    }

    // 6. Ticket Repository
    fun provideTicketRepository(context: Context): TicketRepository {
        return TicketRepository.getInstance()
    }
}