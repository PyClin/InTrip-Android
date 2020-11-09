package com.minosai.stellarhack.ui.auth.model

import com.google.gson.annotations.SerializedName

data class ClaimMappingRequest(
    @SerializedName("employer_username") val employerUsername: String?
)