package com.minosai.stellarhack.ui.ticket.passenger.model

sealed class PassengerTicketUIState {
    object Loading : PassengerTicketUIState()
    object Advertising : PassengerTicketUIState()
    object Ticket : PassengerTicketUIState()
    object Error : PassengerTicketUIState()
    object Success : PassengerTicketUIState()
}