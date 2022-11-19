package com.test.yacupwalkietalkie.screens.act_welcome

import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.di.ActWelcomeInjector
import com.test.yacupwalkietalkie.base.safe
import com.test.yacupwalkietalkie.base.toSet
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData
import com.test.yacupwalkietalkie.base.view_models.BaseViewModel
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersList
import com.test.yacupwalkietalkie.ui.common.ScreenState
import com.test.yacupwalkietalkie.utils.permissions.PermissionsManager
import com.test.yacupwalkietalkie.utils.resources.StringsProvider

class ActWelcomeVm : BaseViewModel<ActWelcomeVm.State, ActWelcomeVm.Effect>() {

    var permissionsManager: PermissionsManager? = null
    var stringsProvider: StringsProvider? = null

    data class State(
        val screenState: ScreenState,
        val navigatedToSettings: Boolean
    )

    sealed class Effect {
        data class BaseEffectWrapper(val data: BaseEffectsData) : Effect()
    }

    override val initialState: State = State(
        screenState = ScreenState.SUCCESS,
        navigatedToSettings = false
    )

    override fun onCreate(act: BaseActivity) {
        super.onCreate(act)
        val actWelcome = act as? ActWelcome ?: return
        ActWelcomeInjector(actWelcome).injectVm(this)
        proceedToNextIfPossible(false)
    }

    override fun onResume(act: BaseActivity) {
        if (currentState.navigatedToSettings) {
            proceedToNextIfPossible(true)
        }
        setStateResult(currentState.copy(navigatedToSettings = false))
    }

    private fun proceedToNextIfPossible(showErrorIfNot: Boolean) {
        val pm = permissionsManager ?: return
        if (pm.areAllPermissionsGranted().safe()) {
            setStateResult(
                state = currentState.copy(screenState = ScreenState.SUCCESS),
                effects = Effect.BaseEffectWrapper(
                    data = BaseEffectsData.NavigateTo(
                        clazz = ActPeersList::class.java,
                        finishCurrent = true
                    )
                ).toSet()
            )
        } else {
            val state = if (pm.hasAnyDeniedPermanently() && showErrorIfNot) {
                currentState.copy(screenState = ScreenState.ERROR)
            } else {
                currentState.copy(screenState = ScreenState.SUCCESS)
            }
            setStateResult(state)
        }
    }

    inner class Listener {
        fun onBottomButtonClicked() {
            val pm = permissionsManager ?: return
            if (currentState.screenState == ScreenState.ERROR) {
                setStateResult(currentState.copy(navigatedToSettings = true))
                pm.navigateToAppPermissionsSettings()
            } else {
                pm.checkPermissions {
                    proceedToNextIfPossible(true)
                }
            }
        }
    }
}
