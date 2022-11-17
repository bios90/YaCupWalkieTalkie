package com.test.yacupwalkietalkie.screens.act_welcome

import com.test.yacupwalkietalkie.base.App
import com.test.yacupwalkietalkie.base.view_models.BaseViewModel
import com.test.yacupwalkietalkie.base.view_models.makeActionDelayed

class ActWelcomeVm(app: App) : BaseViewModel<ActWelcomeVm.State, ActWelcomeVm.Effects>() {

    data class State(
        val title: String,
        val subtitle: String,
    )

    sealed class Effects {
        data class Toast(val text: String) : Effects()
    }

    override fun onCreate() {
        super.onCreate()
        stateInner.postValue(currentState to emptySet())
        makeActionDelayed(delayTime = 5000) {
            stateInner.postValue(
                currentState.copy(
                    title = "New title after delay",
                    subtitle = "New subtitle after delay",
                ) to setOf(Effects.Toast("Eeee and Toast Effect delay also"))
            )
        }
    }

    override fun initialState(): State = State(
        title = "Some initial title",
        subtitle = "Some initial subtitle"
    )

}