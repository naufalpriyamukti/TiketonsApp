package com.tiketons.app.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiketons.app.data.remote.PaymentApiService
import com.tiketons.app.data.repository.TransactionRepository
import com.tiketons.app.data.repository.UserRepository
import com.tiketons.app.modeldata.PaymentResponse
import com.tiketons.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val apiService: PaymentApiService,
    private val userRepository: UserRepository
) : ViewModel() {

    // Kita gunakan TransactionRepository agar data tersimpan ke database otomatis
    // Kita buat instance-nya di sini menggunakan apiService yang sudah ada
    private val transactionRepository = TransactionRepository(apiService)

    // Gunakan UiState agar konsisten dengan Login/Register
    private val _paymentState = MutableStateFlow<UiState<PaymentResponse>>(UiState.Idle)
    val paymentState: StateFlow<UiState<PaymentResponse>> = _paymentState.asStateFlow()

    fun processPayment(
        orderId: String, // Parameter ini sebenarnya di-generate ulang di Repo, tapi tak apa
        amount: Int,
        paymentType: String,
        eventId: String,
        tribunName: String
    ) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            try {
                // Panggil Repository (yang sudah mencakup: Cek Login, Hit API, Simpan ke DB)
                // Kita convert amount ke Double karena Repo mintanya Double
                val result = transactionRepository.requestPayment(
                    eventId = eventId.toIntOrNull() ?: 0,
                    eventName = "Tiket Event", // Nama event bisa diambil detailnya jika perlu
                    amount = amount.toDouble(),
                    paymentMethod = paymentType,
                    tribun = tribunName
                )

                result.onSuccess { response ->
                    // Sukses
                    _paymentState.value = UiState.Success(response)
                }.onFailure { exception ->
                    // Gagal
                    val errorMessage = exception.message ?: "Pembayaran Gagal"
                    _paymentState.value = UiState.Error(errorMessage)
                }

            } catch (e: Exception) {
                _paymentState.value = UiState.Error("System Error: ${e.message}")
            }
        }
    }
}