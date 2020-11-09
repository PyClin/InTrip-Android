package com.minosai.stellarhack.ui.auth.model

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("user_type") val userType: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("id") val userId: String?
)