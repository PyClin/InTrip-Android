package com.minosai.stellarhack.ui.ticket.conductor.di

import com.minosai.stellarhack.StellarHackApp
import com.minosai.stellarhack.ui.ticket.conductor.ConductorTicketViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val conductorTicketModule = module {
    viewModel { ConductorTicketViewModel(androidContext() as StellarHackApp, get(), get()) }
}