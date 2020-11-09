package com.minosai.stellarhack.ui.ticket.conductor.model

sealed class ConductorTicketUIState {
    object Discovering : ConductorTicketUIState()
    object Ticket : ConductorTicketUIState()
    object Payment : ConductorTicketUIState()
    object Loading : ConductorTicketUIState()
    object Success : ConductorTicketUIState()
    object Error : ConductorTicketUIState()
}