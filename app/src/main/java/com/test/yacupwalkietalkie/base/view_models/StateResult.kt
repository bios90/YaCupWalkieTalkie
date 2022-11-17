package com.test.yacupwalkietalkie.base.view_models

open class StateResultEvent<State, Effect>(private val data: StateResult<State, Effect>) {
    var wasHandled = false

    fun getIfNotHandled(): StateResult<State, Effect> = if (wasHandled) {
        data.copy(second = emptySet())
    } else {
        wasHandled = true
        data
    }

    fun peekData() = data
}
