package com.tiketons.app.modeldata

import com.google.gson.annotations.SerializedName

// 1. Request Body
data class PaymentRequest(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("paymentType") val paymentType: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerEmail") val customerEmail: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("eventId") val eventId: String,
    @SerializedName("event_name") val eventName: String? = null,
    @SerializedName("tribunName") val tribunName: String
)

// 2. Response Wrapper
data class PaymentResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PaymentData?
)

// 3. Response Data
data class PaymentData(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("total_amount") val totalAmount: String,
    @SerializedName("payment_type") val paymentType: String,
    @SerializedName("va_number") val vaNumber: String?,
    @SerializedName("expiration_time") val expirationTime: String?
)
