package com.minosai.stellarhack.ui.ticket.conductor.model

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo

data class EndpointInfo(
    val endpointId: String,
    val info: DiscoveredEndpointInfo
)