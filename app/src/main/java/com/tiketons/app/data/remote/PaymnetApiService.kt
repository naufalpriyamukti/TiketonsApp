package com.tiketons.app.data.remote

import com.tiketons.app.modeldata.PaymentRequest
import com.tiketons.app.modeldata.PaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {

    // Pastikan import di atas adalah 'retrofit2.Response'
    @POST("api/payment/charge")
    suspend fun createTransaction(@Body request: PaymentRequest): Response<PaymentResponse>

    // Saya comment dulu biar tidak error "Unresolved reference TransactionStatusResponse"
    // Nanti bisa di-uncomment kalau Model-nya sudah dibuat
    /*
    @GET("payment/status/{orderId}")
    suspend fun checkStatus(@Path("orderId") orderId: String): Response<TransactionStatusResponse>
    */
}