package com.minosai.stellarhack.ui.auth.data

import com.minosai.stellarhack.ui.auth.model.ClaimMappingRequest
import com.minosai.stellarhack.ui.auth.model.LoginRequest
import com.minosai.stellarhack.ui.auth.model.LoginResponse
import com.minosai.stellarhack.ui.auth.model.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface AuthApi {

    @POST("api/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/")
    suspend fun signup(@Body request: SignupRequest): Response<SignupRequest>

    @POST("api/create_claim_mapping/")
    suspend fun claimMapping(
        @HeaderMap headers: Map<String, String>,
        @Body request: ClaimMappingRequest
    ): Response<Any>

}