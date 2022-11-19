package com.test.yacupwalkietalkie.ui.common

import android.content.Context
import android.os.SystemClock
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
fun TextStyle.secondaryTextColor() = this.copy(color = AppTheme.color.gray2)

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

val statusBarHeightCompose
    @Composable
    get() = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

val navBarHeightCompose
    @Composable
    get() = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

@Composable
inline fun Modifier.debounceClickable(
    debounceTime: Long = 500,
    crossinline onClick: () -> Unit,
): Modifier {
    return clickable(
        interactionSource = MutableInteractionSource(),
        indication = rememberRipple(
            color = AppTheme.color.green.copy(alpha = 0.4f)
        ),
        onClick = debounced(
            debounceTime = debounceTime,
            onClick = onClick,
        )
    )
}

val CornerBasedShape.top: CornerBasedShape
    get() = copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
val CornerBasedShape.bottom: CornerBasedShape
    get() = copy(topStart = ZeroCornerSize, topEnd = ZeroCornerSize)

@Composable
fun AnimatedVisibilityMy(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationEnter: Int = 500,
    durationExit: Int = 500,
    content: @Composable() AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    modifier = modifier,
    visible = visible,
    content = content,
    enter = fadeIn(
        animationSpec = keyframes {
            this.durationMillis = durationEnter
        }
    ),
    exit = fadeOut(
        animationSpec = keyframes {
            this.durationMillis = durationExit
        }
    )
)

fun Color.withAlpha(alpha: Float) = copy(alpha = alpha)
