package com.minosai.stellarhack.ui.auth.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ui.auth.data.AuthRepo
import com.minosai.stellarhack.ui.auth.model.LoginResponse
import com.minosai.stellarhack.ui.auth.model.SignupRequest
import com.minosai.stellarhack.ui.auth.utils.USER_TYPE_EMPLOYEE
import com.minosai.stellarhack.ui.auth.utils.USER_TYPE_PUBLIC
import com.minosai.stellarhack.ui.auth.utils.USER_TYPE_STAFF
import com.minosai.stellarhack.utils.model.Resource
import com.minosai.stellarhack.utils.nonNull
import com.minosai.stellarhack.utils.toLiveData
import kotlinx.coroutines.launch

class SignupViewModel(private val authRepo: AuthRepo) : ViewModel() {

    private val _signupState = MutableLiveData<Resource<Any>>()
    val signupState = _signupState.toLiveData()

    private var currentUserType = USER_TYPE_PUBLIC

    fun updateUserType(id: Int) {
        currentUserType = when (id) {
            R.id.signupButtonPublic -> USER_TYPE_PUBLIC
            R.id.signupButtonEmployee -> USER_TYPE_EMPLOYEE
            R.id.signupButtonStaff -> USER_TYPE_STAFF
            else -> currentUserType
        }
    }

    fun signup(username: String, password: String, employer: String) =
        viewModelScope.launch {
            _signupState.postValue(Resource.Loading())
            val resource = authRepo.signup(username, password, currentUserType)
            if (resource is Resource.Success) {
                login(username, password, employer)
            } else {
                _signupState.postValue(resource)
            }
        }

    private suspend fun login(username: String, password: String, employer: String) {
        val resource = authRepo.login(username, password)
        if (resource is Resource.Success && currentUserType == USER_TYPE_EMPLOYEE) {
            claimMapping(employer, resource.data?.accessToken.nonNull())
        } else {
            _signupState.postValue(resource)
        }
    }

    private suspend fun claimMapping(employer: String, accessToken: String) {
        _signupState.postValue(authRepo.claimMapping(employer, accessToken))
    }

}