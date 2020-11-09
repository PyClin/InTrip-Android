package com.minosai.stellarhack.ui.ticket.conductor.rv

import android.view.View
import com.minosai.stellarhack.ui.ticket.conductor.model.EndpointInfo
import com.minosai.stellarhack.utils.base.BaseViewHolder
import com.minosai.stellarhack.utils.parseJson
import kotlinx.android.synthetic.main.layout_endpoint_info.view.*

class EndpointViewHolder(itemView: View, listener: (EndpointInfo?) -> Unit) :
    BaseViewHolder<EndpointInfo>(itemView) {

    init {
        itemView.setOnClickListener {
            listener(currentData)
        }
    }

    override fun bind(data: EndpointInfo) = with(itemView) {
        endpointName.text = data.info.endpointName.parseJson<Map<String, String>>()?.get("name")
    }
}