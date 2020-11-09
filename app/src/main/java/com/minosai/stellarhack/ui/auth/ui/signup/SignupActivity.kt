package com.minosai.stellarhack.ui.auth.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ui.auth.ui.login.LoginActivity
import com.minosai.stellarhack.ui.home.MainActivity
import com.minosai.stellarhack.utils.model.Resource
import kotlinx.android.synthetic.main.activity_signup.*
import org.koin.android.viewmodel.ext.android.viewModel

class SignupActivity : AppCompatActivity() {

    private val viewModel by viewModel<SignupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setupClicks()
        addObservers()
    }

    private fun setupClicks() {
        signupToggleUserType.addOnButtonCheckedListener { _, checkedId, _ ->
            signupInputEmployerID.isVisible = checkedId == R.id.signupButtonEmployee
            viewModel.updateUserType(checkedId)
        }

        signupButtonSignup.setOnClickListener {
            viewModel.signup(
                signupInputUsername.editText?.text.toString(),
                signupInputPassword.editText?.text.toString(),
                signupInputEmployerID.editText?.text.toString()
            )
        }

        signupButtonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun addObservers() {
        viewModel.signupState.observe(this, Observer {
            signupContainer.isVisible = false
            signupProgressBar.isVisible = false
            when (it) {
                is Resource.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resource.Loading -> {
                    signupProgressBar.isVisible = true
                }
                is Resource.Error -> {

                }
            }
        })
    }
}