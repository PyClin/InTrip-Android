package com.minosai.stellarhack.ui.auth.data

import com.minosai.stellarhack.ticket.local.TicketDao
import com.minosai.stellarhack.ui.auth.model.ClaimMappingRequest
import com.minosai.stellarhack.ui.auth.model.LoginRequest
import com.minosai.stellarhack.ui.auth.model.SignupRequest
import com.minosai.stellarhack.utils.model.getIfSuccess
import com.minosai.stellarhack.utils.network.safeApiCall
import com.minosai.stellarhack.utils.preference.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepo(
    private val authApi: AuthApi,
    private val ticketDao: TicketDao,
    private val appPreferences: AppPreferences
) {

    suspend fun login(username: String, password: String) = withContext(Dispatchers.IO) {
        safeApiCall { authApi.login(LoginRequest(username, password)) }
            .let { response ->
                response.getIfSuccess()?.let {
                    appPreferences.accessToken = it.accessToken
                    appPreferences.refreshToken = it.refreshToken
                    appPreferences.username = it.username
                    appPreferences.userType = it.userType
                    appPreferences.userId = it.id
                }
                response
            }
    }

    suspend fun signup(username: String, password: String, userType: String) =
        withContext(Dispatchers.IO) {
            safeApiCall { authApi.signup(SignupRequest(userType, username, password)) }
//            appPreferences.username = username
//            appPreferences.userType = userType
//            appPreferences.walletBalance = walletBalance
//            ticketDao.nukeTable()
        }

    suspend fun claimMapping(employer: String, accessToken: String) =
        withContext(Dispatchers.IO) {
            safeApiCall {
                authApi.claimMapping(
                    mapOf("Authorization" to "Bearer $accessToken"),
                    ClaimMappingRequest(employer)
                )
            }
        }

}