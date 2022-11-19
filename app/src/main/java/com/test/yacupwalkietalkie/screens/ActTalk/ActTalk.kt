package com.test.yacupwalkietalkie.screens.ActTalk

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.getArgs
import com.test.yacupwalkietalkie.base.safe
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
    private var lastState: ActTalkVm.State? = null

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

    override fun onBackPressed() {
        actTalkVm.Listener().onBackPressed()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return if (actTalkVm.currentState.isRationMode.not()) {
            super.dispatchKeyEvent(event)
        } else {
            val action = event?.action
            val keyCode = event?.keyCode
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    if (action == KeyEvent.ACTION_DOWN) {
                        actTalkVm.Listener().recordButtonDown()
                    } else {
                        actTalkVm.Listener().recordButtonUp()
                    }
                    return true;
                }
                else -> super.dispatchKeyEvent(event);
            }
        }
    }

    private fun consumeState(state: ActTalkVm.State) {
        if (lastState == state) {
            return
        }
        lastState = state
        setContent {
            ActTalkCompose(
                state = state,
                onButtonDown = actTalkVm.Listener()::recordButtonDown,
                onButtonUp = actTalkVm.Listener()::recordButtonUp,
                onRationModeToggled = actTalkVm.Listener()::onRationModeToggled,
                onStayMoreClicked = actTalkVm.Listener()::onStayMoreClicked,
                onDismissClicked = actTalkVm.Listener()::onDismissClicked,
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
