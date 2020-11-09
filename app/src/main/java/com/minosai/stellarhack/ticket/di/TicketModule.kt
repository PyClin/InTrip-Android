package com.minosai.stellarhack.ticket.di

import com.minosai.stellarhack.ticket.TicketRepo
import com.minosai.stellarhack.ticket.local.TicketDatabase
import com.minosai.stellarhack.ticket.model.Ticket
import com.minosai.stellarhack.ticket.remote.TicketApi
import com.minosai.stellarhack.utils.network.FEATURE_RETROFIT
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val ticketModule = module {

    single { TicketDatabase.buildDatabase(androidContext()) }

    factory { get<TicketDatabase>().ticketDao() }

    factory { get<Retrofit>(named(FEATURE_RETROFIT)).create(TicketApi::class.java) }

    factory { TicketRepo(get(), get(), get()) }

}