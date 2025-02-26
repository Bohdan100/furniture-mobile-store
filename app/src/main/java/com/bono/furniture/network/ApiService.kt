package com.bono.furniture.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

import com.bono.furniture.models.PaymentRequest
import com.bono.furniture.models.PaymentResponse

interface ApiService {
    @POST("backend_url")
    fun processPayment(@Body request: PaymentRequest): Call<PaymentResponse>
}