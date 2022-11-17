package com.test.yacupwalkietalkie.ui.common.theme

import androidx.compose.runtime.Composable

object AppTheme {
    val color = Colors()
    val typography = Typography()
    val dimens = Dimens()
}

@Composable
fun AppTheme(content: @Composable() () -> Unit) {
    content.invoke() //STOPSHIP Todo Make later theme settings if needed or remove
}