package com.minosai.stellarhack.di

import com.minosai.stellarhack.ticket.di.ticketModule
import com.minosai.stellarhack.ui.auth.di.authModule
import com.minosai.stellarhack.ui.home.di.homeModule
import com.minosai.stellarhack.ui.ticket.conductor.di.conductorTicketModule
import com.minosai.stellarhack.ui.ticket.passenger.di.passengerTicketModule
import com.minosai.stellarhack.utils.network.networkModule

val appComponent = listOf(
    appModule,
    ticketModule,
    networkModule,
    authModule,
    homeModule,
    conductorTicketModule,
    passengerTicketModule
)