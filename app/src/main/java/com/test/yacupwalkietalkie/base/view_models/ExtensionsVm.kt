package com.test.yacupwalkietalkie.base.view_models

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun ViewModel.makeActionDelayed(delayTime: Long, action: () -> Unit) {
    viewModelScope.launch {
        delay(delayTime)
        action.invoke()
    }
}

fun ViewModel.makeOnBackground(action: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch(
        context = Dispatchers.IO,
        block = action
    )
