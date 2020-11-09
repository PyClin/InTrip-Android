package com.minosai.stellarhack.ui.home.data

import com.minosai.stellarhack.ui.home.model.BalanceModel
import com.minosai.stellarhack.utils.model.getIfSuccess
import com.minosai.stellarhack.utils.network.safeApiCall
import com.minosai.stellarhack.utils.preference.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletRepo(private val walletApi: WalletApi, private val appPreferences: AppPreferences) {

    suspend fun deposit(amount: Float) = withContext(Dispatchers.IO) {
        val resource = safeApiCall { walletApi.deposit(BalanceModel(amount)) }
        resource.getIfSuccess()?.let {
            appPreferences.walletBalance = it.amount
        }
        resource
    }

    suspend fun getBalance() = withContext(Dispatchers.IO) {
        val resource = safeApiCall { walletApi.balance() }
        resource.getIfSuccess()?.let {
            appPreferences.walletBalance = it.amount
        }
        resource
    }

}