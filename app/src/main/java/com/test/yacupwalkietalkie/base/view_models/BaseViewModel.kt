package com.test.yacupwalkietalkie.base.view_models

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<State, Effects> : ViewModel() {
    protected val stateInner: MutableLiveData<Pair<State, Set<Effects>>> = MutableLiveData()
    val state = stateInner as LiveData<Pair<State, Set<Effects>>>
    val currentState: State
        get() = state.value?.first ?: initialState()

    abstract fun initialState(): State

    open fun onCreate() = Unit
    open fun onStart() = Unit
    open fun onResume() = Unit
    open fun onPause() = Unit
    open fun onStop() = Unit
    open fun onDestroy() = Unit
}