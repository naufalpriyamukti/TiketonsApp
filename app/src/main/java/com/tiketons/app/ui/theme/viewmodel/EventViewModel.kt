package com.tiketons.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiketons.app.data.repository.EventRepository
import com.tiketons.app.modeldata.EventModel
import com.tiketons.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _eventState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Loading)
    val eventState: StateFlow<UiState<List<EventModel>>> = _eventState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        getAllEvents()
    }

    fun getAllEvents() {
        viewModelScope.launch {
            _eventState.value = UiState.Loading
            val events = repository.getEvents()
            if (events.isNotEmpty()) {
                _eventState.value = UiState.Success(events)
            } else {
                _eventState.value = UiState.Error("Tidak ada event tersedia")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isEmpty()) {
                getAllEvents()
            } else {
                val results = repository.searchEvents(query)
                _eventState.value = UiState.Success(results)
            }
        }
    }
    // --- FUNGSI YANG HILANG (Tambahkan ini) ---
    fun searchEvents(query: String) {
        viewModelScope.launch {
            _eventState.value = UiState.Loading
            // Jika query kosong, ambil semua data lagi
            if (query.isEmpty()) {
                getAllEvents()
            } else {
                val result = repository.searchEvents(query)
                if (result.isNotEmpty()) {
                    _eventState.value = UiState.Success(result)
                } else {
                    _eventState.value = UiState.Error("Event tidak ditemukan")
                }
            }
        }
    }
}