package com.minosai.stellarhack.ui.ticket.conductor

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ui.ticket.conductor.model.ConductorTicketUIState
import com.minosai.stellarhack.ui.ticket.conductor.rv.EndpointAdapter
import com.minosai.stellarhack.utils.nonNull
import com.minosai.stellarhack.utils.parseJson
import com.minosai.stellarhack.utils.toast
import kotlinx.android.synthetic.main.activity_conductor_ticket.*
import kotlinx.android.synthetic.main.layout_conductor_ticket.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_ticket_loading.*
import org.koin.android.viewmodel.ext.android.viewModel

class ConductorTicketActivity : AppCompatActivity() {

    private val viewModel by viewModel<ConductorTicketViewModel>()

    private val endpointAdapter by lazy {
        EndpointAdapter {
            it ?: return@EndpointAdapter
            val name = it.info.endpointName.parseJson<Map<String, String>>()?.get("name").nonNull()
            val id = it.info.endpointName.parseJson<Map<String, String>>()?.get("id").nonNull().toInt()
            viewModel.requestConnection(it.endpointId, name, id)
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.startDiscovery()
            } else toast("Permission denied")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conductor_ticket)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        setupRV()
        setupClicks()
        setupObservers()
        ticketLoadingRipple.startRippleAnimation()
    }

    private fun setupRV() = with (rvEndpoints) {
        layoutManager = LinearLayoutManager(this@ConductorTicketActivity)
        adapter = endpointAdapter
    }

    private fun setupClicks() {
        buttonConductorTicketPayment.setOnClickListener {
            viewModel.sendTicket(
                inputConductorTicketSource.editText?.text.toString(),
                inputConductorTicketDestination.editText?.text.toString(),
                inputconductorTicketAmount.editText?.text.toString().toFloat()
            )
        }
    }

    private fun setupObservers() {
        viewModel.endpointList.observe(this, Observer {
            endpointAdapter.dataList = it
        })

        viewModel.uiState.observe(this, Observer {
            hideAllElements()
            handleUIState(it)
        })
    }

    private fun handleUIState(state: ConductorTicketUIState) = when(state) {
        ConductorTicketUIState.Discovering -> {
            rvEndpoints.isVisible = true
        }
        ConductorTicketUIState.Ticket -> {
            conductorTicketContainer.isVisible = true
            conductorTicketTitle.text = viewModel.currentEndpointName
        }
        ConductorTicketUIState.Payment -> {
            conductorLoadingContainer.isVisible = true
            ticketLoadingText.text = "Waiting for payment"
        }
        ConductorTicketUIState.Loading -> {
            conductorLoadingContainer.isVisible = true
            ticketLoadingText.text = "Loading..."
        }
        ConductorTicketUIState.Success -> {
            toast("Ticket sent and payment successful")
            finish()
        }
        ConductorTicketUIState.Error -> {
            toast("Something went wrong")
            finish()
        }
    }

    private fun hideAllElements() {
        rvEndpoints.isVisible = false
        conductorTicketContainer.isVisible = false
        conductorLoadingContainer.isVisible = false
    }

    override fun onBackPressed() {
        viewModel.handleError()
    }
}