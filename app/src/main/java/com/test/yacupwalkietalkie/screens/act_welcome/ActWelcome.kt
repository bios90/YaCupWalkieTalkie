package com.test.yacupwalkietalkie.screens.act_welcome

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.Toast
import com.test.yacupwalkietalkie.base.view_models.createViewModelFactory
import java.io.Serializable

class ActWelcome : BaseActivity() {

    private val actMainViewModel: ActWelcomeVm by viewModels {
        createViewModelFactory<ActWelcomeVm>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeState(
            vm = actMainViewModel,
            stateConsumer = ::consumeState,
            effectsConsumer = ::handleEffects
        )
    }

    private fun consumeState(state: ActWelcomeVm.State) {
        setContent {
            ActWelcomeCompose(state = state)
        }
    }

    private fun handleEffects(effects: Set<ActWelcomeVm.Effects>) {
        for (eff in effects) {
            when (eff) {
                is ActWelcomeVm.Effects.Toast -> Toast(eff.text)
            }
        }
    }
}