package com.minosai.stellarhack.ui.ticket.conductor

import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.minosai.stellarhack.StellarHackApp
import com.minosai.stellarhack.ticket.TicketRepo
import com.minosai.stellarhack.ticket.model.Ticket
import com.minosai.stellarhack.ticket.model.TicketPaymentPayload
import com.minosai.stellarhack.ui.ticket.conductor.model.ConductorTicketUIState
import com.minosai.stellarhack.ui.ticket.conductor.model.EndpointInfo
import com.minosai.stellarhack.utils.Constants
import com.minosai.stellarhack.utils.events.Event
import com.minosai.stellarhack.utils.events.EventManager
import com.minosai.stellarhack.utils.events.NewTicketEvent
import com.minosai.stellarhack.utils.parseJson
import com.minosai.stellarhack.utils.toJson
import com.minosai.stellarhack.utils.toLiveData
import kotlinx.coroutines.launch
import java.util.*

class ConductorTicketViewModel(
    stellarHackApp: StellarHackApp,
    private val ticketRepo: TicketRepo,
    private val eventManager: EventManager
) : AndroidViewModel(stellarHackApp) {

    private val _uiState = MutableLiveData<ConductorTicketUIState>(ConductorTicketUIState.Loading)
    val uiState = _uiState.toLiveData()

    private val _endpointList = MutableLiveData<List<EndpointInfo>>(listOf())
    val endpointList = _endpointList.toLiveData()

    var currentEndpointId = ""
    var currentEndpointName = ""
    var currentFromId: Int = 0
    var currentTicket: Ticket? = null
    var receivedPayment = false

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                payload.asBytes()?.toString(Charsets.UTF_8)
                    ?.parseJson<TicketPaymentPayload>()
                    ?.let { ticketPayload ->
                        viewModelScope.launch {
                            currentTicket?.let {
                                ticketRepo.createTicket(it)
                                ticketRepo.addBalance(it.amount)
                            }
                            closeConnections()
                            _uiState.postValue(ConductorTicketUIState.Success)
                            receivedPayment = true
                            eventManager.sendEvent(Event(NewTicketEvent))
                        }
                    }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    if (receivedPayment.not()) {
                        _uiState.postValue(ConductorTicketUIState.Payment)
                    }
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    if (receivedPayment.not()) {
                        _uiState.postValue(ConductorTicketUIState.Loading)
                    }
                }
                else -> {
                    handleError()
                }
            }
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Nearby.getConnectionsClient(getApplication<StellarHackApp>())
                .acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    currentEndpointId = endpointId
                    _uiState.postValue(ConductorTicketUIState.Ticket)
                }
                else -> {
                    handleError()
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            _endpointList.value?.toMutableList()
                ?.apply {
                    removeAll { it.endpointId == endpointId }
                    _endpointList.postValue(this)
                }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            _endpointList.value?.toMutableList()
                ?.takeIf { it.find { it.endpointId == endpointId } == null }
                ?.apply {
                    add(EndpointInfo(endpointId, info))
                    _endpointList.postValue(this)
                }
        }

        override fun onEndpointLost(endpointId: String) {
            _endpointList.value?.toMutableList()
                ?.apply {
                    removeAll { it.endpointId == endpointId }
                    _endpointList.postValue(this)
                }
        }
    }

    fun startDiscovery() {
        DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build().let { options ->
                Nearby.getConnectionsClient(getApplication<StellarHackApp>())
                    .startDiscovery(Constants.PACKAGE_NAME, endpointDiscoveryCallback, options)
                    .addOnSuccessListener {
                        _uiState.postValue(ConductorTicketUIState.Discovering)
                    }
                    .addOnFailureListener {
                        handleError()
                    }
            }
    }

    fun requestConnection(endpointId: String, endpointName: String, fromId: Int) {
        _uiState.postValue(ConductorTicketUIState.Loading)
        Nearby.getConnectionsClient(getApplication<StellarHackApp>())
            .requestConnection(Build.MODEL, endpointId, connectionLifecycleCallback)
            .addOnSuccessListener {
                _uiState.postValue(ConductorTicketUIState.Loading)
                currentEndpointName = endpointName
                currentFromId = fromId
            }
            .addOnFailureListener {
                handleError()
            }
    }

    fun sendTicket(source: String, destination: String, amount: Float) {
        Ticket.getInstance(source, destination, amount, Calendar.getInstance().time, currentFromId)
            .also { currentTicket = it }
            .toJson().toByteArray(Charsets.UTF_8)
            .let(Payload::fromBytes)
            .let {
                Nearby.getConnectionsClient(getApplication<StellarHackApp>())
                    .sendPayload(currentEndpointId, it)
            }
    }

    fun handleError() {
        closeConnections()
        _uiState.postValue(ConductorTicketUIState.Error)
    }

    private fun closeConnections() {
        Nearby.getConnectionsClient(getApplication<StellarHackApp>()).apply {
            stopAllEndpoints()
            stopDiscovery()
        }
    }
}