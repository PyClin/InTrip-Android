package com.minosai.stellarhack.ui.auth.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access") val accessToken: String?,
    @SerializedName("refresh") val refreshToken: String?,
    @SerializedName("user_type") val userType: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String?
)