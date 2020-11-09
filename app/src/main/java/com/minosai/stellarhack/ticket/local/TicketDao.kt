package com.minosai.stellarhack.ticket.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.minosai.stellarhack.ticket.model.Ticket
import com.minosai.stellarhack.utils.base.BaseDao

@Dao
abstract class TicketDao : BaseDao<Ticket>() {

    @Query("SELECT * FROM ticket")
    abstract fun getAllTickets(): LiveData<List<Ticket>>

    @Query("SELECT * FROM ticket")
    abstract suspend fun getAllTicketsBlocking(): List<Ticket>

    @Query("SELECT * FROM ticket WHERE claimedStatus LIKE :status")
    abstract suspend fun getTicketsWithClaimedStatus(status: String): List<Ticket>

    @Query("DELETE FROM ticket")
    abstract suspend fun nukeTable()
}