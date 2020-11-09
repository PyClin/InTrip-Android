package com.minosai.stellarhack.ui.home.rv

import android.view.View
import com.minosai.stellarhack.ticket.model.Ticket
import com.minosai.stellarhack.utils.Constants
import com.minosai.stellarhack.utils.base.BaseViewHolder
import kotlinx.android.synthetic.main.layout_ticket.view.*
import java.text.SimpleDateFormat
import java.util.*

class TicketViewHolder(itemView: View) : BaseViewHolder<Ticket>(itemView) {

    companion object {
        private const val DATE_FORMAT = "EEE, MMM d, hh:mm aaa"
    }

    private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    override fun bind(data: Ticket) = with(itemView) {
        ticketTimestamp.text = dateFormat.format(data.getDate())
        ticketIdText.text = "Bus • ${data.ticketId}"
        ticketCost.text = data.getAmountString()
        ticketSource.text = data.source
        ticketDestination.text = data.destination
    }
}