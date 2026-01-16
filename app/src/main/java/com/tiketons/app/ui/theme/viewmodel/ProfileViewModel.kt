package com.tiketons.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiketons.app.data.remote.SupabaseClient
import com.tiketons.app.ui.common.UiState
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive

data class UserProfile(
    val email: String,
    val fullName: String
)

class ProfileViewModel : ViewModel() {
    private val auth = SupabaseClient.client.auth

    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfile>> = _profileState

    private val _updateState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updateState: StateFlow<UiState<Boolean>> = _updateState

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            val user = auth.currentUserOrNull()
            if (user != null) {
                // Ambil nama (aman dari null/error)
                val rawName = user.userMetadata?.get("full_name")?.jsonPrimitive?.content
                val finalName = if (rawName.isNullOrEmpty() || rawName == "null") "Pengguna Baru" else rawName

                _profileState.value = UiState.Success(
                    UserProfile(email = user.email ?: "", fullName = finalName)
                )
            } else {
                _profileState.value = UiState.Error("User tidak ditemukan")
            }
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            try {
                // --- PERBAIKAN DI SINI ---
                // Gunakan 'data' bukan 'userMetadata' di dalam modifyUser
                auth.modifyUser {
                    data = buildJsonObject {
                        put("full_name", newName)
                    }
                }
                _updateState.value = UiState.Success(true)
                loadProfile()
            } catch (e: Exception) {
                _updateState.value = UiState.Error(e.message ?: "Gagal update profile")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
}