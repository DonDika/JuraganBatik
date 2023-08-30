package com.dondika.juraganbatik.data.remote

import com.dondika.juraganbatik.data.model.TransactionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("v2/{orderId}/status")
    fun getTransaction(
       @Path("orderId") orderId: String,
    ): Call<TransactionResponse>





}