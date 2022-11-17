package com.test.yacupwalkietalkie.ui.common.subviews

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class RippleTheme(
    private val color: Color,
) : RippleTheme {
    @Composable
    override fun defaultColor(): Color = color

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleAlpha(
            0.2f,
            0.2f,
            0.2f,
            0.2f,
        )
}
