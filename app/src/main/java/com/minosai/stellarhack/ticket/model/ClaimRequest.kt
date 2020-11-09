package com.minosai.stellarhack.ticket.model

import com.google.gson.annotations.SerializedName

data class ClaimRequest(
    @SerializedName("ids") val ids: List<Int>
)