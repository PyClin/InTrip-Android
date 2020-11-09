package com.minosai.stellarhack.ticket

import androidx.lifecycle.LiveData
import com.minosai.stellarhack.ticket.local.TicketDao
import com.minosai.stellarhack.ticket.model.CLAIMED_STATUS_ELIGIBLE
import com.minosai.stellarhack.ticket.model.ClaimRequest
import com.minosai.stellarhack.ticket.model.Ticket
import com.minosai.stellarhack.ticket.remote.TicketApi
import com.minosai.stellarhack.utils.model.Resource
import com.minosai.stellarhack.utils.model.getIfSuccess
import com.minosai.stellarhack.utils.network.safeApiCall
import com.minosai.stellarhack.utils.preference.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class TicketRepo(
    private val ticketApi: TicketApi,
    private val ticketDao: TicketDao,
    private val appPreferences: AppPreferences
) {

    suspend fun getAllTickets(): LiveData<List<Ticket>> = withContext(Dispatchers.IO) {
        safeApiCall(ticketApi::getTicketList)
            .getIfSuccess()
            ?.let { ticketDao.insertAll(it) }
        ticketDao.getAllTickets()
    }

    suspend fun createTicket(ticket: Ticket) = withContext(Dispatchers.IO) {
        ticketDao.insert(ticket)
    }

    suspend fun syncTickets(): Resource<Unit> = withContext(Dispatchers.IO) {
        ticketDao.getAllTicketsBlocking().forEach {
            val resource = safeApiCall { ticketApi.createTicket(it) }
            if (resource is Resource.Error) {
                return@withContext Resource.Error<Unit>("Something wen't wrong while syncing")
            }
        }
        Resource.Success(Unit)
//        ticketDao.getAllTicketsBlocking()
//            .map { async { safeApiCall { ticketApi.createTicket(it) } } }
//            .awaitAll()
//            .let {
//                it.fold(true) { acc, data ->
//                    acc && data is Resource.Success<*>
//                }.let { isAllSuccess ->
//                    if (isAllSuccess) Resource.Success(Unit)
//                    else Resource.Error<Unit>("Something wen't wrong while syncing")
//                }
//            }
    }

    suspend fun claim() = withContext(Dispatchers.IO) {
        val list = ticketDao.getTicketsWithClaimedStatus(CLAIMED_STATUS_ELIGIBLE)
        safeApiCall { ticketApi.claim(ClaimRequest(list.map { it.id })) }
    }

    fun addBalance(amount: Float) {
        appPreferences.apply {
            walletBalance += amount
        }
    }

    fun reduceBalance(amount: Float) {
        appPreferences.apply {
            walletBalance -= amount
        }
    }

}