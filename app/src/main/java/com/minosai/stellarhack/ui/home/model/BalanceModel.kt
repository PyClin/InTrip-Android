package com.minosai.stellarhack.ui.home.model

import com.google.gson.annotations.SerializedName

data class BalanceModel(
    @SerializedName("amount", alternate = ["balance"]) val amount: Float
)