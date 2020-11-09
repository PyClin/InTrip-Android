package com.minosai.stellarhack.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ui.auth.ui.login.LoginActivity
import com.minosai.stellarhack.ui.auth.utils.USER_TYPE_EMPLOYEE
import com.minosai.stellarhack.ui.auth.utils.USER_TYPE_PUBLIC
import com.minosai.stellarhack.ui.auth.utils.USER_TYPE_STAFF
import com.minosai.stellarhack.ui.home.rv.TicketAdapter
import com.minosai.stellarhack.ui.ticket.conductor.ConductorTicketActivity
import com.minosai.stellarhack.ui.ticket.passenger.PassengerTicketActivity
import com.minosai.stellarhack.utils.Constants
import com.minosai.stellarhack.utils.model.Resource
import com.minosai.stellarhack.utils.observeNonNull
import com.minosai.stellarhack.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    private val ticketAdapter by lazy { TicketAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.shouldLogin()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_main)
        setupClicks()
        setupObservers()
        setupUI()
    }

    private fun setupUI() {
        with(homeTicketRV) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ticketAdapter
            isNestedScrollingEnabled = false
        }

        hideAllViews()
        when (viewModel.userType) {
            USER_TYPE_PUBLIC -> {
                homeChipDeposit.isVisible = true
            }
            USER_TYPE_EMPLOYEE -> {
                homeChipDeposit.isVisible = true
                homeChipClaim.isVisible = true
            }
            USER_TYPE_STAFF -> {
                homeChipSync.isVisible = true
                homeTicketActionButton.text = "Sell Ticket"
            }
        }
    }

    private fun hideAllViews() {
        homeChipDeposit.isVisible = false
        homeChipClaim.isVisible = false
        homeChipSync.isVisible = false
    }

    private fun setupClicks() {

        homeChipRefresh.setOnClickListener {
            viewModel.refreshWalletBalance()
        }

        homeChipDeposit.setOnClickListener {
            homeLayoutDeposit.isVisible = homeLayoutDeposit.isVisible.not()
        }

        homeTicketActionButton.setOnClickListener {
            when (viewModel.userType) {
                USER_TYPE_EMPLOYEE, USER_TYPE_PUBLIC ->
                    startActivity(Intent(this, PassengerTicketActivity::class.java))
                USER_TYPE_STAFF ->
                    startActivity(Intent(this, ConductorTicketActivity::class.java))
            }
        }

        homeWalletDepositButton.setOnClickListener {
            viewModel.deposit(homeWalletDepositInput.editText?.text.toString().toFloat())
        }

        homeChipClaim.setOnClickListener {
            viewModel.claim()
        }

        homeChipSync.setOnClickListener {
            viewModel.sync()
        }
    }

    private fun setupObservers() {
        viewModel.ticketList.observeNonNull(this) {
            homeTicketLoadingView.hide()
            homeTicketContainer.isVisible = false
            when (it) {
                is Resource.Success -> {
                    val newList = it.data ?: listOf()
                    homeTicketContainer.isVisible = newList.isNotEmpty()
                    ticketAdapter.dataList = newList
                }
                is Resource.Loading -> {
                    homeTicketLoadingView.show("Loading tickets...")
                }
                is Resource.Error -> {
                    homeTicketContainer.isVisible = true
                    toast("Something wen't wrong")
                }
            }
        }

        viewModel.walletBalance.observeNonNull(this) {
            homeWalletLoadingView.hide()
            homeWalletContainer.isVisible = false
            when (it) {
                is Resource.Success -> {
                    homeWalletContainer.isVisible = true
                    homeWalletDepositInput.editText?.text = null
                    homeLayoutDeposit.isVisible = false
                    homeWalletBalance.text = "${Constants.RUPEE_SYMBOL}${"%.2f".format(it.data)}"
                }
                is Resource.Loading -> {
                    homeWalletLoadingView.show("Refreshing wallet...")
                }
                is Resource.Error -> {
                    homeWalletContainer.isVisible = true
                    toast("Something wen't wrong while updating wallet")
                }
            }
        }

        viewModel.syncState.observeNonNull(this) {
            homeChipSync.isVisible = false
            homeLayoutSyncLoading.isVisible = false
            when (it) {
                is Resource.Success -> {
                    homeChipSync.isVisible = true
                    toast("Synced successfully")
                }
                is Resource.Loading -> {
                    homeLayoutSyncLoading.isVisible = true
                }
                is Resource.Error -> {
                    homeChipSync.isVisible = true
                    toast("An error occurred while syncing")
                }
            }
        }
    }
}