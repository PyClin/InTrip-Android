package com.minosai.stellarhack.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

val Any.TAG
    get() = this.javaClass.simpleName

fun String?.nonNull() = this ?: ""

fun Context.toast(message: String?) {
    if (message.isNullOrBlank()) return
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    context?.toast(message)
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showSnackbar(message: String) {
    view?.showSnackbar(message)
}

fun Context.shareText(text: String) {
    Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }.let { intent ->
        Intent.createChooser(intent, null)
            .also(::startActivity)
    }
}

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, block: (T) -> Unit) {
    observe(owner, Observer {
        it ?: return@Observer
        block(it)
    })
}

fun <T> MutableLiveData<T>.toLiveData(): LiveData<T> = this

inline fun <reified T> Any?.castTo(): T? = if (this is T) this else null