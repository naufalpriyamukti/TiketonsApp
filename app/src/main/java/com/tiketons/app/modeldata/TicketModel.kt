package com.tiketons.app.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketModel(
    // ID bisa null saat insert, tapi ada saat select. Default 0 biar aman.
    @SerialName("id")
    val id: Int? = 0,

    @SerialName("transaction_id")
    val transactionId: String? = null,

    @SerialName("event_name")
    val eventName: String,

    // Tanggal & Lokasi bisa null di database, jadi gunakan String?
    @SerialName("event_date")
    val eventDate: String? = null,

    @SerialName("location")
    val location: String? = "-",

    @SerialName("tribun")
    val tribun: String,

    @SerialName("qr_code")
    val qrCode: String,

    // --- FIX UTAMA: WAJIB ADA ---
    // Field ini yang menyebabkan error "Unresolved reference: isUsed" di TicketScreen
    @SerialName("is_used") val isUsed: Boolean = false,

    // Untuk menampilkan harga (pastikan query Supabase mengambil kolom ini)
    val amount: Double? = null,

    // Untuk menampilkan nama customer (pastikan query Supabase mengambil data profil)
    @SerialName("user_name") // Sesuaikan dengan nama kolom/alias di query Supabase
    val userName: String? = null
)