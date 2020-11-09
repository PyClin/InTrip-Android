package com.minosai.stellarhack.ticket.remote

import com.minosai.stellarhack.ticket.model.ClaimRequest
import com.minosai.stellarhack.ticket.model.Ticket
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TicketApi {

    @POST("api/ticket/list/")
    suspend fun getTicketList(): Response<List<Ticket>>

    @POST("api/ticket/create/")
    suspend fun createTicket(@Body ticket: Ticket): Response<Any>

    @POST("api/claim/create/")
    suspend fun claim(@Body request: ClaimRequest): Response<Any>

}