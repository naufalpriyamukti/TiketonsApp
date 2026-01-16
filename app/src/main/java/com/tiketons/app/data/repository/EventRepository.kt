package com.tiketons.app.data.repository

import android.util.Log
import com.tiketons.app.data.remote.SupabaseClient
import com.tiketons.app.modeldata.EventModel
import io.github.jan.supabase.postgrest.from

class EventRepository {
    private val db = SupabaseClient.client

    // 1. Ambil Semua Event (Untuk Home User)
    suspend fun getEvents(): List<EventModel> {
        return try {
            // Request ke Supabase tabel "events"
            val response = db.from("events").select().decodeList<EventModel>()

            // Log jika berhasil (Cek di Logcat: "EventRepository")
            Log.d("EventRepository", "Sukses! Data didapat: ${response.size} event")

            response
        } catch (e: Exception) {
            // Log jika gagal (PENTING UNTUK DEBUGGING)
            Log.e("EventRepository", "Gagal ambil event: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // 2. Cari Event (Untuk Search Bar)
    suspend fun searchEvents(query: String): List<EventModel> {
        return try {
            db.from("events").select {
                filter {
                    ilike("name", "%$query%") // Case insensitive search
                }
            }.decodeList<EventModel>()
        } catch (e: Exception) {
            Log.e("EventRepository", "Error search: ${e.message}")
            emptyList()
        }
    }

    // 3. Tambah Event Baru (Untuk Admin)
    suspend fun createEvent(event: EventModel): Result<Boolean> {
        return try {
            db.from("events").insert(event)
            Result.success(true)
        } catch (e: Exception) {
            Log.e("EventRepository", "Gagal create event: ${e.message}")
            Result.failure(e)
        }
    }

    // 4. Hapus Event (Untuk Admin)
    suspend fun deleteEvent(id: Int): Result<Boolean> {
        return try {
            db.from("events").delete {
                filter { eq("id", id) }
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e("EventRepository", "Gagal delete event: ${e.message}")
            Result.failure(e)
        }
    }

    // --- TAMBAHAN WAJIB (SINGLETON) ---
    // Agar Injection.kt tidak error saat panggil EventRepository.getInstance()
    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository().also { instance = it }
            }
    }
}