package com.minosai.stellarhack.ui.home.data

import com.minosai.stellarhack.ui.home.model.BalanceModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WalletApi {

    @POST("api/deposit/")
    suspend fun deposit(@Body reqest: BalanceModel): Response<BalanceModel>

    @POST("api/balance/")
    suspend fun balance(): Response<BalanceModel>

}