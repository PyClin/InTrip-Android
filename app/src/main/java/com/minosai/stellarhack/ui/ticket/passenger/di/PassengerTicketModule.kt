package com.minosai.stellarhack.ui.ticket.passenger.di

import com.minosai.stellarhack.StellarHackApp
import com.minosai.stellarhack.ui.ticket.passenger.PassengerTicketViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val passengerTicketModule = module {
    viewModel { PassengerTicketViewModel(androidContext() as StellarHackApp, get(), get(), get()) }
}