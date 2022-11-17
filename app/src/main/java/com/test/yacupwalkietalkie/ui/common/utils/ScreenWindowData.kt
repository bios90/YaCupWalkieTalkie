package com.test.yacupwalkietalkie.ui.common.utils

import androidx.compose.ui.graphics.Color
import com.test.yacupwalkietalkie.ui.common.color

data class ScreenWindowData(
    val colorStatusBar: Color,
    val colorNavBar: Color,
    val isUnderStatusBar: Boolean,
    val isUnderNavBar: Boolean,
    val isLightStatusBarIcons: Boolean? = null,
    val isLightNavBarIcons: Boolean? = null,
) {
    companion object {
        fun fullScreenTrans(
            isLightStatusBarIcons: Boolean = false,
            isLightNavBarIcons: Boolean = false,
        ) = ScreenWindowData(
            colorStatusBar = "#01000000".color, //Hack to make because full transparent does not work
            colorNavBar = "#01000000".color,
            isUnderStatusBar = true,
            isUnderNavBar = true,
            isLightNavBarIcons = isLightNavBarIcons,
            isLightStatusBarIcons = isLightStatusBarIcons,
        )
    }
}
