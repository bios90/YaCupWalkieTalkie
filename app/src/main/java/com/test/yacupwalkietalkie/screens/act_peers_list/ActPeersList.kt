package com.test.yacupwalkietalkie.screens.act_peers_list

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.view_models.createViewModelFactory
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcomeVm
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme
import com.test.yacupwalkietalkie.ui.common.utils.ScreenWindowData

class ActPeersList : BaseActivity() {

    override val screenWindowData: ScreenWindowData
        get() = ScreenWindowData(
            colorNavBar = AppTheme.color.white,
            colorStatusBar = AppTheme.color.white,
            isUnderStatusBar = false,
            isUnderNavBar = false,
            isLightNavBarIcons = false,
            isLightStatusBarIcons = false
        )

    private val actPeersListVm: ActPeersListVm by viewModels {
        createViewModelFactory<ActPeersListVm>()
    }

    private var lastState: ActPeersListVm.State? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeState(
            vm = actPeersListVm,
            stateConsumer = ::consumeState,
            effectsConsumer = ::handleEffects
        )
    }

    private fun consumeState(state: ActPeersListVm.State) {
        if (lastState == state) {
            return
        }
        lastState = state
        setContent {
            ActPeersListCompose(
                state = state,
                onDeviceClicked = actPeersListVm.Listener()::onDeviceClicked,
                onTurnWifiClicked = actPeersListVm.Listener()::clickedTurnOnWifi
            )
        }
    }

    private fun handleEffects(effects: Set<ActPeersListVm.Effect>) {
        for (eff in effects) {
            when (eff) {
                is ActPeersListVm.Effect.BaseEffectWrapper -> handleBaseEffects(eff.data)
            }
        }
    }
}
