package com.test.yacupwalkietalkie.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineDebouncer(
    private val delay: Long,
    private val scope: CoroutineScope,
) {
    private var job: Job? = null
    fun makeAction(action: () -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(delay)
            action.invoke()
        }
    }
}
