package com.test.yacupwalkietalkie.ui.common.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.sp
import com.test.yacupwalkietalkie.R


class Typography {
    val LightS = getFontWithSize(size = TextSize.S, fontWeight = FontWeight.Light)
    val LightM = getFontWithSize(size = TextSize.M, fontWeight = FontWeight.Light)
    val LightL = getFontWithSize(size = TextSize.L, fontWeight = FontWeight.Light)
    val LightXl = getFontWithSize(size = TextSize.Xl, fontWeight = FontWeight.Light)
    val LightXxl = getFontWithSize(size = TextSize.Xxl, fontWeight = FontWeight.Light)

    val RegS = getFontWithSize(size = TextSize.S)
    val RegM = getFontWithSize(size = TextSize.M)
    val RegL = getFontWithSize(size = TextSize.L)
    val RegXl = getFontWithSize(size = TextSize.Xl)
    val RegXxl = getFontWithSize(size = TextSize.Xxl)

    val BoldS = getFontWithSize(size = TextSize.S, fontWeight = FontWeight.Bold)
    val BoldM = getFontWithSize(size = TextSize.M, fontWeight = FontWeight.Bold)
    val BoldL = getFontWithSize(size = TextSize.L, fontWeight = FontWeight.Bold)
    val BoldXl = getFontWithSize(size = TextSize.Xl, fontWeight = FontWeight.Bold)
    val BoldXxl = getFontWithSize(size = TextSize.Xxl, fontWeight = FontWeight.Bold)

    @OptIn(ExperimentalUnitApi::class)
    private fun getFontWithSize(size: Float, fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = Fonts.Montserrat,
            fontSize = size.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            color = AppTheme.color.black
        )
}

object Fonts {
    val Montserrat = FontFamily(
        Font(R.font.mono_light, FontWeight.Light),
        Font(R.font.mono_reg, FontWeight.Normal),
        Font(R.font.mono_bold, FontWeight.Bold),
    )
}


object TextSize {
    val S = 12f
    val M = 14f
    val L = 16f
    val Xl = 19f
    val Xxl = 24f
}
