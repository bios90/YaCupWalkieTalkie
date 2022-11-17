package com.test.yacupwalkietalkie.ui.common.subviews

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.yacupwalkietalkie.ui.common.debounced
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme

@Composable
fun ButtonGreen(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = AppTheme.typography.RegL,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    corners: CornerBasedShape = RoundedCornerShape(AppTheme.dimens.x2),
    elevation: ButtonElevation? = null,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    contentPadding: PaddingValues = PaddingValues(horizontal = AppTheme.dimens.x4, vertical = AppTheme.dimens.x3),
    imageStart: @Composable RowScope.() -> Unit = {},
    textColor: Color = AppTheme.color.white,
) {
    BaseButton(
        text = text,
        textStyle = textStyle,
        modifier = modifier
            .clip(corners),
        onClick = onClick,
        colorBg = AppTheme.color.green,
        colorRipple = AppTheme.color.black.copy(alpha = 0.4f),
        isEnabled = isEnabled,
        elevation = elevation,
        minWidth = minWidth,
        minHeight = minHeight,
        contentPadding = contentPadding,
        imageStart = imageStart,
        textColor = textColor
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    text: Any,
    textStyle: TextStyle,
    onClick: () -> Unit,
    colorBg: Color,
    colorBgDisabled: Color = colorBg,
    colorRipple: Color,
    elevation: ButtonElevation?,
    isEnabled: Boolean = true,
    contentPadding: PaddingValues,
    minWidth: Dp,
    minHeight: Dp,
    imageStart: @Composable RowScope.() -> Unit,
    textColor: Color,
    content: (@Composable RowScope.() -> Unit)? = null,
    disabledAlpha: Float = 0.4f,
) {
    CompositionLocalProvider(
        LocalRippleTheme provides RippleTheme(colorRipple),
        LocalMinimumTouchTargetEnforcement provides false
    ) {
        val clickable = debounced(debounceTime = 500, onClick = onClick)
        Button(
            onClick = { clickable.invoke() },
            modifier = modifier
                .defaultMinSize(
                    minWidth = minWidth,
                    minHeight = minHeight
                )
                .alpha(if (isEnabled) 1f else disabledAlpha),
            enabled = isEnabled,
            elevation = elevation,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorBg,
                disabledBackgroundColor = colorBgDisabled
            ),
            contentPadding = contentPadding,
        ) {
            if (content != null) {
                content.invoke(this)
            } else {
                imageStart()
                val textAsAnnotatedString = text as? AnnotatedString
                val textAsString = text as? String
                if (textAsAnnotatedString != null) {
                    Text(
                        text = textAsAnnotatedString,
                        style = textStyle,
                        color = textColor
                    )
                } else {
                    Text(
                        text = textAsString ?: "",
                        style = textStyle,
                        color = textColor
                    )
                }
            }
        }
    }
}
