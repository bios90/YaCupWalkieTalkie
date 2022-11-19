package com.test.yacupwalkietalkie.ui.common.theme

import androidx.compose.runtime.Composable

object AppTheme {
    val color = Colors()
    val typography = Typography()
    val dimens = Dimens()
}

@Composable
fun AppTheme(content: @Composable() () -> Unit) {
    content.invoke()
}
