package com.minosai.stellarhack.utils

import com.google.gson.Gson

val GSON = Gson()

fun Any.toJson(): String {
    return GSON.toJson(this)
}

inline fun <reified T> String.parseJson(): T? {
    return try {
        GSON.fromJson(this, T::class.java)
    } catch (e: Exception) {
        null
    }
}