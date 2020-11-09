package com.minosai.stellarhack.ui.auth.di

import com.minosai.stellarhack.ui.auth.data.AuthApi
import com.minosai.stellarhack.ui.auth.data.AuthRepo
import com.minosai.stellarhack.ui.auth.ui.login.LoginViewModel
import com.minosai.stellarhack.ui.auth.ui.signup.SignupViewModel
import com.minosai.stellarhack.utils.network.AUTH_RETROFIT
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val authModule = module {

    factory { get<Retrofit>(named(AUTH_RETROFIT)).create(AuthApi::class.java) }

    factory { AuthRepo(get(), get(), get()) }

    viewModel { LoginViewModel(get()) }

    viewModel { SignupViewModel(get()) }

}