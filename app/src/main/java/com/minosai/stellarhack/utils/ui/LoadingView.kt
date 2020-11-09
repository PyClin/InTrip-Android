package com.minosai.stellarhack.utils.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.minosai.stellarhack.R
import kotlinx.android.synthetic.main.layout_loading.view.*

class LoadingView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(ctx, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.layout_loading, this)
    }

    fun show(text: String? = null) {
        loadingText.text = text ?: "Loading..."
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

}