package com.minosai.stellarhack.ui.ticket.passenger

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ui.ticket.passenger.model.PassengerTicketUIState
import com.minosai.stellarhack.utils.toast
import kotlinx.android.synthetic.main.activity_passenger_ticket.*
import kotlinx.android.synthetic.main.layout_passenger_ticket_preview.*
import kotlinx.android.synthetic.main.layout_ticket_loading.*
import org.koin.android.viewmodel.ext.android.viewModel

class PassengerTicketActivity : AppCompatActivity() {

    private val viewModel by viewModel<PassengerTicketViewModel>()

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.startAdvertising()
            } else toast("Permission denied")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_ticket)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        addObservers()
        setupClicks()
        ticketLoadingRipple.startRippleAnimation()
    }

    private fun setupClicks() {
        passengerTicketPayButton.setOnClickListener {
            viewModel.pay()
        }
    }

    private fun addObservers() {
        viewModel.uiState.observe(this, Observer {
            hideAllElements()
            handleUIState(it)
        })
    }

    private fun handleUIState(state: PassengerTicketUIState) = when (state) {
        PassengerTicketUIState.Loading -> {
            passengerTicketLoadingContainer.isVisible = true
            ticketLoadingText.text = "Loading..."
        }
        PassengerTicketUIState.Advertising -> {
            passengerTicketLoadingContainer.isVisible = true
            ticketLoadingText.text = "Waiting for ticket..."
        }
        PassengerTicketUIState.Ticket -> {
            viewModel.currentTicket?.let { ticket ->
                passengerTicketSourceText.text = ticket.source
                passengerTicketDestinationText.text = ticket.destination
                passengerTicketAmountText.text = ticket.getAmountString()
            }
            passengerTicketContainer.isVisible = true
        }
        PassengerTicketUIState.Error -> {
            toast("An error occurred")
            finish()
        }
        PassengerTicketUIState.Success -> {
            toast("Ticket received successfully")
            finish()
        }
    }

    private fun hideAllElements() {
        passengerTicketContainer.isVisible = false
        passengerTicketLoadingContainer.isVisible = false
    }

    override fun onBackPressed() {
        viewModel.handleError()
    }
}