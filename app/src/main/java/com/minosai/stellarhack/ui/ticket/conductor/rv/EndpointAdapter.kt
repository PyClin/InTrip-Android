package com.minosai.stellarhack.ui.ticket.conductor.rv

import android.view.View
import com.minosai.stellarhack.R
import com.minosai.stellarhack.ui.ticket.conductor.model.EndpointInfo
import com.minosai.stellarhack.utils.base.BaseAdapter

class EndpointAdapter(private val listener: (EndpointInfo?) -> Unit) :
    BaseAdapter<EndpointInfo, EndpointViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.layout_endpoint_info

    override fun getViewHolder(itemView: View): EndpointViewHolder {
        return EndpointViewHolder(itemView, listener)
    }
}