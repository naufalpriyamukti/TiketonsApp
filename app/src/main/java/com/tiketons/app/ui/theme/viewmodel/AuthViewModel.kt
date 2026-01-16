package com.tiketons.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiketons.app.data.repository.AuthRepository
import com.tiketons.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val loginState: StateFlow<UiState<Boolean>> = _loginState

    private val _registerState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val registerState: StateFlow<UiState<Boolean>> = _registerState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = repository.login(email, pass)
            result.onSuccess {
                _loginState.value = UiState.Success(it)
            }.onFailure {
                _loginState.value = UiState.Error(it.message ?: "Login Gagal")
            }
        }
    }

    // --- PERBAIKAN DI SINI ---
    // Urutan parameter disesuaikan dengan UI: fullName -> username -> email -> pass
    fun register(fullName: String, username: String, email: String, pass: String) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading

            // Pastikan saat memanggil repository, urutannya sesuai dengan definisi fungsi di Repository Anda
            // Asumsi: Repository meminta (username, email, pass, fullName)
            val result = repository.register(username, email, pass, fullName)

            result.onSuccess {
                _registerState.value = UiState.Success(it)
            }.onFailure {
                _registerState.value = UiState.Error(it.message ?: "Register Gagal")
            }
        }
    }
}