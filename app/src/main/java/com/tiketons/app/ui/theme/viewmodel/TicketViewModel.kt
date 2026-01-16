package com.tiketons.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiketons.app.data.repository.TicketRepository
import com.tiketons.app.modeldata.TicketModel
import com.tiketons.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {

    // State untuk List Tiket (Dipakai di TicketScreen)
    private val _ticketState = MutableStateFlow<UiState<List<TicketModel>>>(UiState.Loading)
    val ticketState: StateFlow<UiState<List<TicketModel>>> = _ticketState

    // State untuk Detail Tiket (Dipakai di ETicketScreen)
    // Data ini nanti berisi detail lengkap: Harga, Nama Customer, Order ID, dll.
    private val _selectedTicket = MutableStateFlow<UiState<TicketModel>>(UiState.Idle)
    val selectedTicket: StateFlow<UiState<TicketModel>> = _selectedTicket

    // --- FUNGSI 1: LOAD LIST TIKET ---
    fun loadTickets() {
        viewModelScope.launch {
            // Set loading hanya jika data belum pernah dimuat (agar tidak flickering saat refresh)
            if (_ticketState.value !is UiState.Success) {
                _ticketState.value = UiState.Loading
            }

            try {
                // Ambil data dari Repository
                val tickets = repository.getMyTickets()

                // Jika list kosong, tetap Success (nanti UI yang handle gambar kosong)
                _ticketState.value = UiState.Success(tickets)

            } catch (e: Exception) {
                _ticketState.value = UiState.Error(e.message ?: "Gagal memuat daftar tiket")
            }
        }
    }

    // --- FUNGSI 2: LOAD DETAIL TIKET ---
    // Fungsi ini dipanggil saat masuk ke halaman E-Ticket
    fun loadTicketDetail(ticketId: Int) {
        viewModelScope.launch {
            _selectedTicket.value = UiState.Loading
            try {
                // Repository harus melakukan JOIN tabel untuk mendapatkan:
                // - Nama User (profiles.full_name)
                // - Harga (transactions.amount)
                // - Order ID (transactions.order_id)
                val ticket = repository.getTicketById(ticketId)

                if (ticket != null) {
                    _selectedTicket.value = UiState.Success(ticket)
                } else {
                    _selectedTicket.value = UiState.Error("Data tiket tidak ditemukan.")
                }
            } catch (e: Exception) {
                _selectedTicket.value = UiState.Error(e.message ?: "Gagal memuat detail tiket")
            }
        }
    }
}