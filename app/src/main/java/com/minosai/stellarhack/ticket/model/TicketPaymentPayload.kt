package com.minosai.stellarhack.ticket.model

data class TicketPaymentPayload(
    val isPaymentAccepted: Boolean,
    val ticket: Ticket
)