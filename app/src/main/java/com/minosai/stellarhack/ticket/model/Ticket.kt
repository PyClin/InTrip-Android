package com.minosai.stellarhack.ticket.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.minosai.stellarhack.utils.Constants
import com.minosai.stellarhack.utils.nonNull
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

private const val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"
private val format = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault())

@Entity
data class Ticket(
    @PrimaryKey
    @SerializedName("ticket_id") val ticketId: String,
    @SerializedName("source") val source: String?,
    @SerializedName("destination") val destination: String?,
    @SerializedName("amount") val amount: Float = 0f,
    @SerializedName("timestamp") val timeStamp: String?,
    @SerializedName("from_user") val fromUserId: Int = 0,
    @SerializedName("to_user") val toUserId: Int = 0,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("claimed_status") val claimedStatus: String = ""
) {

    companion object {
        fun getInstance(
            source: String?,
            destination: String?,
            amount: Float,
            date: Date,
            fromId: Int
        ) = Ticket(
            ticketId = "$source$destination$amount${getTimestamp(date)}".hash(),
            source = source,
            destination = destination,
            amount = amount,
            timeStamp = getTimestamp(date),
            fromUserId = fromId
        )
    }

    fun getDate() = try { format.parse(timeStamp.nonNull()) } catch (e: Exception) { null }

    fun getAmountString() = "${Constants.RUPEE_SYMBOL}$amount"

}

private fun getTimestamp(date: Date) = format.format(date)

private fun String.hash(): String {
    return MessageDigest.getInstance("SHA-256").digest(toByteArray()).toHex()
}

private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}