package com.minosai.stellarhack.ui.home.di

import com.minosai.stellarhack.ticket.TicketRepo
import com.minosai.stellarhack.ui.home.MainViewModel
import com.minosai.stellarhack.ui.home.data.WalletApi
import com.minosai.stellarhack.ui.home.data.WalletRepo
import com.minosai.stellarhack.utils.network.FEATURE_RETROFIT
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val homeModule = module {

    factory { get<Retrofit>(named(FEATURE_RETROFIT)).create(WalletApi::class.java) }

    factory { WalletRepo(get(), get()) }

    viewModel { MainViewModel(get(), get(), get(), get()) }

}