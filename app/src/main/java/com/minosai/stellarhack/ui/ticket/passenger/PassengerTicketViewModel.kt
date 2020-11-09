package com.minosai.stellarhack.ui.ticket.passenger

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
import com.minosai.stellarhack.ui.ticket.passenger.model.PassengerTicketUIState
import com.minosai.stellarhack.utils.*
import com.minosai.stellarhack.utils.events.Event
import com.minosai.stellarhack.utils.events.EventManager
import com.minosai.stellarhack.utils.events.NewTicketEvent
import com.minosai.stellarhack.utils.preference.AppPreferences
import kotlinx.coroutines.launch

class PassengerTicketViewModel(
    stellarHackApp: StellarHackApp,
    private val ticketRepo: TicketRepo,
    private val eventManager: EventManager,
    private val appPreferences: AppPreferences
) : AndroidViewModel(stellarHackApp) {

    private val _uiState = MutableLiveData<PassengerTicketUIState>(PassengerTicketUIState.Loading)
    val uiState = _uiState.toLiveData()

    var currentEndpointId = ""
    var currentTicket: Ticket? = null
    var isPaymentStarted = false

    private val payloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                payload.asBytes()?.toString(Charsets.UTF_8)?.parseJson<Ticket>()?.let { ticket ->
                    currentTicket = ticket
                    _uiState.postValue(PassengerTicketUIState.Ticket)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    if (isPaymentStarted) {
                        viewModelScope.launch {
                            currentTicket?.let {
                                ticketRepo.createTicket(it)
                                ticketRepo.reduceBalance(it.amount)
                            }
                            closeConnections()
                            _uiState.postValue(PassengerTicketUIState.Success)
                            eventManager.sendEvent(Event(NewTicketEvent))
                        }
                    }
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    if (isPaymentStarted || currentTicket == null) {
                        _uiState.postValue(PassengerTicketUIState.Loading)
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
            Nearby.getConnectionsClient(stellarHackApp)
                .acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    currentEndpointId = endpointId
                }
                else -> {
                    handleError()
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            handleError()
        }
    }

    fun startAdvertising() {
        AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build().let { options ->
                Nearby.getConnectionsClient(getApplication<StellarHackApp>())
                    .startAdvertising(
                        mapOf(
                            "name" to appPreferences.username.nonNull(),
                            "id" to appPreferences.userId.toString()
                        ).toJson(),
                        Constants.PACKAGE_NAME,
                        connectionLifecycleCallback,
                        options
                    )
                    .addOnSuccessListener {
                        _uiState.postValue(PassengerTicketUIState.Advertising)
                    }
                    .addOnFailureListener {
                        handleError()
                    }
            }
    }

    private fun closeConnections() {
        Nearby.getConnectionsClient(getApplication<StellarHackApp>()).apply {
            stopAllEndpoints()
            stopAdvertising()
        }
    }

    fun pay() {
        currentTicket ?: return
        if (currentTicket!!.amount > appPreferences.walletBalance) {
            handleError()
            return
        }
        TicketPaymentPayload(true, currentTicket!!)
            .toJson().toByteArray(Charsets.UTF_8)
            .let(Payload::fromBytes)
            .let {
                Nearby.getConnectionsClient(getApplication<StellarHackApp>())
                    .sendPayload(currentEndpointId, it)
            }
        isPaymentStarted = true
    }

    fun handleError() {
        closeConnections()
        _uiState.postValue(PassengerTicketUIState.Error)
    }
}