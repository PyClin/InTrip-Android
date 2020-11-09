package com.minosai.stellarhack.ui.home.rv

import android.view.View
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ticket.model.Ticket
import com.minosai.stellarhack.utils.base.BaseAdapter

class TicketAdapter : BaseAdapter<Ticket, TicketViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.layout_ticket

    override fun getViewHolder(itemView: View): TicketViewHolder {
        return TicketViewHolder(itemView)
    }
}