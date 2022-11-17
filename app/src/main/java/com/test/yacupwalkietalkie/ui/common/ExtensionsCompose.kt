package com.test.yacupwalkietalkie.ui.common

import android.content.Context
import android.os.SystemClock
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme

val String.color
    get() = Color(android.graphics.Color.parseColor(this))

fun getComposeRootView(context: Context) = ComposeView(context)
    .apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

fun TextStyle.alignStart() = this.copy(textAlign = TextAlign.Start)
fun TextStyle.secondaryTextColor() = this.copy(color = AppTheme.color.gray1)

@Composable
inline fun debounced(crossinline onClick: () -> Unit, debounceTime: Long = 1000L): () -> Unit {
    var lastTimeClicked by remember { mutableStateOf(0L) }
    val onClickLambda: () -> Unit = {
        val now = SystemClock.uptimeMillis()
        if (now - lastTimeClicked > debounceTime) {
            onClick()
        }
        lastTimeClicked = now
    }
    return onClickLambda
}
