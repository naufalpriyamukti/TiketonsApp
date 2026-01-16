package com.tiketons.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiketons.app.data.repository.EventRepository
import com.tiketons.app.modeldata.EventModel
import com.tiketons.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: EventRepository) : ViewModel() {

    private val _adminState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val adminState: StateFlow<UiState<Boolean>> = _adminState

    // State untuk menampung input form
    var eventName = MutableStateFlow("")
    var eventDesc = MutableStateFlow("")
    var eventDate = MutableStateFlow("")
    var eventTime = MutableStateFlow("")
    var eventLoc = MutableStateFlow("")
    var eventVendor = MutableStateFlow("")
    var priceReg = MutableStateFlow("") // String biar gampang diinput, nanti convert ke Double
    var priceVip = MutableStateFlow("")
    var imageUrl = MutableStateFlow("https://placehold.co/600x400") // Default placeholder

    fun createEvent() {
        viewModelScope.launch {
            _adminState.value = UiState.Loading
            try {
                val event = EventModel(
                    name = eventName.value,
                    description = eventDesc.value,
                    date = eventDate.value,
                    time = eventTime.value,
                    location = eventLoc.value,
                    vendorName = eventVendor.value,
                    priceRegular = priceReg.value.toDoubleOrNull() ?: 0.0,
                    priceVip = priceVip.value.toDoubleOrNull() ?: 0.0,
                    imageUrl = imageUrl.value
                )
                val result = repository.createEvent(event)
                if (result.isSuccess) {
                    _adminState.value = UiState.Success(true)
                    resetForm()
                } else {
                    _adminState.value = UiState.Error("Gagal membuat event")
                }
            } catch (e: Exception) {
                _adminState.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            _adminState.value = UiState.Loading
            val result = repository.deleteEvent(id)
            if (result.isSuccess) {
                _adminState.value = UiState.Success(true)
            } else {
                _adminState.value = UiState.Error("Gagal menghapus event")
            }
        }
    }

    fun resetState() {
        _adminState.value = UiState.Idle
    }

    private fun resetForm() {
        eventName.value = ""
        eventDesc.value = ""
        eventDate.value = ""
        eventTime.value = ""
        eventLoc.value = ""
        eventVendor.value = ""
        priceReg.value = ""
        priceVip.value = ""
    }
}