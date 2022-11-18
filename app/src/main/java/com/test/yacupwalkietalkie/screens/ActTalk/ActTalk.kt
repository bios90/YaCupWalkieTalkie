package com.test.yacupwalkietalkie.screens.ActTalk

import android.os.Bundle
import androidx.activity.viewModels
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.getArgs
import com.test.yacupwalkietalkie.base.view_models.createViewModelFactory
import com.test.yacupwalkietalkie.data.ModelConnectionData
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersListVm
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme
import com.test.yacupwalkietalkie.ui.common.utils.ScreenWindowData
import java.io.Serializable

class ActTalk : BaseActivity() {
    data class Args(
        val connectionData: ModelConnectionData
    ) : Serializable

    private val args: Args by lazy { requireNotNull(getArgs()) }

    override val screenWindowData: ScreenWindowData
        get() = ScreenWindowData(
            colorNavBar = AppTheme.color.white,
            colorStatusBar = AppTheme.color.white,
            isUnderStatusBar = false,
            isUnderNavBar = false,
            isLightNavBarIcons = false,
            isLightStatusBarIcons = false
        )

    private val actTalkVm: ActTalkVm by viewModels {
        createViewModelFactory<ActTalkVm, Args>(args = args)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeState(
            vm = actTalkVm,
            stateConsumer = ::consumeState,
            effectsConsumer = ::handleEffects
        )
    }

    private fun consumeState(state: ActTalkVm.State) {
        setContent {
            ActTalkCompose(
                state = state,
                onButtonDown = actTalkVm.Listener()::recordButtonDown,
                onButtonUp = actTalkVm.Listener()::recordButtonUp,
            )
        }
    }

    private fun handleEffects(effects: Set<ActTalkVm.Effect>) {
        for (eff in effects) {
            when (eff) {
                is ActTalkVm.Effect.BaseEffectWrapper -> handleBaseEffects(eff.data)
            }
        }
    }
}
