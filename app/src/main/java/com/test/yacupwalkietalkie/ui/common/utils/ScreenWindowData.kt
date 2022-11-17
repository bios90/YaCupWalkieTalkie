package com.test.yacupwalkietalkie.ui.common.utils

import androidx.compose.ui.graphics.Color

data class ScreenWindowData(
    val colorStatusBar: Color,
    val colorNavBar: Color,
    val isUnderStatusBar: Boolean,
    val isUnderNavBar: Boolean,
    val isLightStatusBarIcons: Boolean? = null,
    val isLightNavBarIcons: Boolean? = null,
)
{
    companion object{
        fun fullScreenTrans() = ScreenWindowData(
            colorStatusBar = Color.Transparent,
            colorNavBar = Color.Transparent,
            isUnderStatusBar = true,
            isUnderNavBar = true
        )
    }
}