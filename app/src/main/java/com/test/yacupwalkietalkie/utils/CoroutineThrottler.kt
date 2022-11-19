package com.test.yacupwalkietalkie.utils

import com.test.yacupwalkietalkie.base.safe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineThrottler(
    private val delay: Long,
    private val scope: CoroutineScope,
    private val throttleFirst: Boolean
) {
    private var job: Job? = null
    fun makeAction(action: () -> Unit) {
        if (job?.isActive.safe()) {
            return
        }
        job = scope.launch {
            if (throttleFirst.not()) {
                delay(delay)
            }
            action.invoke()
            if (throttleFirst) {
                delay(delay)
            }
        }
    }
}
