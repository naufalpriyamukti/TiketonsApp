package com.tiketons.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiketons.app.data.di.Injection
import com.tiketons.app.ui.theme.viewmodel.PaymentViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // 1. AUTH
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(Injection.provideAuthRepository(context)) as T
            }

            // 2. EVENT
            modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                EventViewModel(Injection.provideEventRepository()) as T
            }

            // 3. PAYMENT
            modelClass.isAssignableFrom(PaymentViewModel::class.java) -> {
                PaymentViewModel(
                    Injection.providePaymentApiService(),
                    Injection.provideUserRepository(context)
                ) as T
            }

            // 4. TRANSACTION (Perbaikan: Kirim context)
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                // Tambahkan (context) di sini untuk mengatasi error
                TransactionViewModel(Injection.provideTransactionRepository(context)) as T
            }

            // 5. TICKET (Perbaikan: Kirim context)
            modelClass.isAssignableFrom(TicketViewModel::class.java) -> {
                // Tambahkan (context) di sini untuk mengatasi error
                TicketViewModel(Injection.provideTicketRepository(context)) as T
            }

            // 6. ADMIN
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(Injection.provideEventRepository()) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(context)
            }.also { INSTANCE = it }
    }
}