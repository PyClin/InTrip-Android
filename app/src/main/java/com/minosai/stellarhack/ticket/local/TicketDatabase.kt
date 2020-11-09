package com.minosai.stellarhack.ticket.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.minosai.stellarhack.ticket.model.Ticket

@Database(entities = [Ticket::class], version = 1, exportSchema = false)
abstract class TicketDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TicketDatabase::class.java,
                "ticket.db"
            ).build()
    }

}