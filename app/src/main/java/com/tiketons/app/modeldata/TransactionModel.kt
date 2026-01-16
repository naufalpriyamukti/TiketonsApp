package com.tiketons.app.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionModel(
    @SerialName("id") val id: Int? = null, // Auto increment dari Supabase
    @SerialName("order_id") val orderId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("event_id") val eventId: Int,
    @SerialName("event_name") val eventName: String? = "Event Tiketons",
    @SerialName("amount") val amount: Double,
    @SerialName("status") val status: String, // PENDING, SUCCESS, FAILED
    @SerialName("payment_type") val paymentMethod: String? = "MANUAL",
    @SerialName("va_number") val vaNumber: String? = null,
    @SerialName("tribun") val tribun: String,
    @SerialName("created_at") val createdAt: String? = null
)