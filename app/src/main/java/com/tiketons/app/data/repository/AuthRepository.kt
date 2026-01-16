package com.tiketons.app.data.repository

import com.tiketons.app.data.preferences.UserPreference
import com.tiketons.app.data.remote.SupabaseClient
import com.tiketons.app.modeldata.UserModel
import com.tiketons.app.modeldata.UserProfile
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
// --- IMPORT PENTING UNTUK MEMPERBAIKI ERROR ---
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive

class AuthRepository(private val pref: UserPreference) {

    private val auth = SupabaseClient.client.auth
    private val db = SupabaseClient.client

    // --- LOGIN ---
    suspend fun login(email: String, pass: String): Result<Boolean> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                password = pass
            }

            val user = auth.currentUserOrNull() ?: throw Exception("Gagal mendapatkan user")
            val token = auth.currentSessionOrNull()?.accessToken ?: ""

            val fullName = try {
                user.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: "User"
            } catch (e: Exception) { "User" }

            val userModel = UserModel(
                email = user.email ?: email,
                token = token,
                name = fullName,
                id = user.id,
                isLogin = true
            )
            pref.saveSession(userModel)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- REGISTER (FIX ERROR MAP VS JSONOBJECT) ---
    suspend fun register(username: String, email: String, pass: String, fullName: String): Result<Boolean> {
        return try {
            // 1. Daftar ke Auth Supabase
            auth.signUpWith(Email) {
                this.email = email
                password = pass

                // PERBAIKAN DI SINI: Gunakan buildJsonObject, bukan mapOf
                data = buildJsonObject {
                    put("full_name", fullName)
                }
            }

            // 2. Ambil User ID
            val userId = auth.currentUserOrNull()?.id ?: throw Exception("Register gagal mendapatkan ID")

            // 3. Simpan data tambahan ke tabel 'profiles'
            val userProfile = UserProfile(
                id = userId,
                email = email,
                username = username,
                fullName = fullName,
                role = "USER"
            )
            db.from("profiles").insert(userProfile)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- LOGOUT ---
    suspend fun logout() {
        try {
            auth.signOut()
            pref.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUserOrNull()?.email
    }

    // --- SINGLETON ---
    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(pref: UserPreference): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(pref).also { instance = it }
            }
    }
}