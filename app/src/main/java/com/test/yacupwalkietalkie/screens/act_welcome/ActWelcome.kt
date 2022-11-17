package com.test.yacupwalkietalkie.screens.act_welcome

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.view_models.createViewModelFactory
import com.test.yacupwalkietalkie.ui.common.utils.ScreenWindowData

class ActWelcome : BaseActivity() {

    override val screenWindowData: ScreenWindowData
        get() = ScreenWindowData.fullScreenTrans(
            isLightStatusBarIcons = true,
            isLightNavBarIcons = false
        )

    private val actMainViewModel: ActWelcomeVm by viewModels {
        createViewModelFactory<ActWelcomeVm>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        subscribeState(
            vm = actMainViewModel,
            stateConsumer = ::consumeState,
            effectsConsumer = ::handleEffects
        )
    }

    private fun consumeState(state: ActWelcomeVm.State) {
        setContent {
            ActWelcomeCompose(
                state = state,
                onBottomButtonClicked = actMainViewModel.Listener()::onBottomButtonClicked
            )
        }
    }

    private fun handleEffects(effects: Set<ActWelcomeVm.Effect>) {
        for (eff in effects) {
            when (eff) {
                is ActWelcomeVm.Effect.BaseEffectWrapper -> handleBaseEffects(eff.data)
            }
        }
    }
}
