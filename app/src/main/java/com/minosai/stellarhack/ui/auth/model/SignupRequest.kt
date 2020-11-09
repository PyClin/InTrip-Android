package com.minosai.stellarhack.ui.auth.model

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    @SerializedName("user_type") val userType: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
)