package com.tiketons.app.modeldata

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class EventModel(
    @SerialName("id") val id: Int? = null, // Auto increment di DB
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("date") val date: String, // Format YYYY-MM-DD
    @SerialName("time") val time: String,
    @SerialName("location") val location: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("vendor_name") val vendorName: String,
    @SerialName("price_vip") val priceVip: Double,
    @SerialName("price_regular") val priceRegular: Double
)