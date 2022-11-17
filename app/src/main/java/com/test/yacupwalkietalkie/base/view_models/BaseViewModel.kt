package com.test.yacupwalkietalkie.base.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.yacupwalkietalkie.base.BaseActivity

typealias StateResult <State, Effects> = Pair<State, Set<Effects>>

abstract class BaseViewModel<State, Effects> : ViewModel() {
    abstract val initialState: State
    protected val stateResultInner: StateResultLiveData<State, Effects> = StateResultLiveData()
    val stateResult: LiveData<StateResultEvent<State, Effects>> = stateResultInner
    val currentState: State
        get() = requireNotNull(stateResult.value?.peekData()?.first)


    open fun onCreate(act: BaseActivity) {
        if (stateResultInner.value == null) {
            stateResultInner.value = StateResultEvent(initialState to emptySet())
        }
    }

    fun setStateResult(state: State, effects: Set<Effects> = emptySet()) {
        stateResultInner.value = StateResultEvent(state to effects)
    }

    open fun onStart(act: BaseActivity) = Unit
    open fun onResume(act: BaseActivity) = Unit
    open fun onPause(act: BaseActivity) = Unit
    open fun onStop(act: BaseActivity) = Unit
    open fun onDestroy(act: BaseActivity) = Unit
}
