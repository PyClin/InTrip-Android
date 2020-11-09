package com.minosai.stellarhack.ui.auth.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ui.auth.ui.signup.SignupActivity
import com.minosai.stellarhack.ui.home.MainActivity
import com.minosai.stellarhack.utils.model.Resource
import com.minosai.stellarhack.utils.observeNonNull
import com.minosai.stellarhack.utils.toast
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupClicks()
        setupObservers()
    }

    private fun setupClicks() {
        loginButtonLogin.setOnClickListener {
            viewModel.login(
                loginInputUsername.editText?.text.toString(),
                loginInputPassword.editText?.text.toString()
            )
        }

        loginButtonSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observeNonNull(this) {
            loginContainer.isVisible = false
            loginProgressBar.isVisible = false
            when (it) {
                is Resource.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resource.Loading -> {
                    loginProgressBar.isVisible = true
                }
                is Resource.Error -> {
                    loginContainer.isVisible = true
                    toast(it.message)
                }
            }
        }
    }
}