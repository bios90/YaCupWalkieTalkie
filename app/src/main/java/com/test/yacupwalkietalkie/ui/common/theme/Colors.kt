package com.test.yacupwalkietalkie.ui.common.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.test.yacupwalkietalkie.ui.common.color

class Colors {
    val transparent by mutableStateOf("#00000000".color)
    val white by mutableStateOf("#ffffff".color)
    val black by mutableStateOf("#0C0C0C".color)
    val gray1 by mutableStateOf("#8A8C90".color)
    val green by mutableStateOf("#31B768".color)
    val red by mutableStateOf("#F83138".color)
}
