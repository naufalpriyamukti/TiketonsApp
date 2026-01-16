package com.tiketons.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiketons.app.data.repository.TransactionRepository
import com.tiketons.app.modeldata.PaymentResponse
import com.tiketons.app.modeldata.TransactionModel
import com.tiketons.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // 1. State untuk Status Pembayaran (Loading, Success, Error)
    private val _paymentState = MutableStateFlow<UiState<PaymentResponse>>(UiState.Idle)
    val paymentState: StateFlow<UiState<PaymentResponse>> = _paymentState

    // 2. State untuk History Transaksi
    private val _historyState = MutableStateFlow<UiState<List<TransactionModel>>>(UiState.Loading)
    val historyState: StateFlow<UiState<List<TransactionModel>>> = _historyState

    // Fungsi Membuat Transaksi (Dipanggil saat tombol "Bayar Sekarang" ditekan)
    fun createTransaction(
        eventId: Int,
        eventName: String, // <--- Parameter Penting yang baru ditambahkan
        amount: Double,
        paymentMethod: String,
        tribun: String
    ) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading

            // Panggil Repository (Pastikan repository juga sudah diupdate menerima eventName)
            val result = repository.requestPayment(eventId, eventName, amount, paymentMethod, tribun)

            result.onSuccess { response ->
                _paymentState.value = UiState.Success(response)

                // Opsional: Langsung refresh history agar transaksi baru (status PENDING) muncul di list
                loadHistory()

            }.onFailure { error ->
                _paymentState.value = UiState.Error(error.message ?: "Gagal membuat transaksi")
            }
        }
    }

    // Fungsi Load History (Dipanggil di TransactionScreen)
    fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = UiState.Loading

            // Memanggil fungsi loadHistory dari Repository
            val result = repository.loadHistory()

            // Update state UI
            if (result.isNotEmpty()) {
                _historyState.value = UiState.Success(result)
            } else {
                _historyState.value = UiState.Success(emptyList())
            }
        }
    }
}