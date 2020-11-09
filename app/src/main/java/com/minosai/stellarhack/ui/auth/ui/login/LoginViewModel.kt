package com.minosai.stellarhack.ui.auth.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minosai.stellarhack.ui.auth.data.AuthRepo
import com.minosai.stellarhack.ui.auth.model.LoginResponse
import com.minosai.stellarhack.utils.model.Resource
import com.minosai.stellarhack.utils.toLiveData
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepo: AuthRepo) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<LoginResponse>>()
    val loginState = _loginState.toLiveData()

    fun login(username: String, password: String) = viewModelScope.launch {
        _loginState.postValue(Resource.Loading())
        _loginState.postValue(authRepo.login(username, password))
    }

}