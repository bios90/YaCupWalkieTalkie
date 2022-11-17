package com.test.yacupwalkietalkie.ui.common

import android.content.Context
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView

val String.color
    get() = Color(android.graphics.Color.parseColor(this))

fun getComposeRootView(context: Context) = ComposeView(context)
    .apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
    }