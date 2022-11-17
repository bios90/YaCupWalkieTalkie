package com.test.yacupwalkietalkie.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData
import com.test.yacupwalkietalkie.base.view_models.BaseViewModel
import com.test.yacupwalkietalkie.ui.common.getComposeRootView
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme
import com.test.yacupwalkietalkie.ui.common.utils.WindowWrapper

abstract class BaseActivity : AppCompatActivity(), WindowWrapper {

    protected val rootView: ComposeView by lazy { getComposeRootView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView)
    }

    protected fun setContent(content: @Composable () -> Unit) {
        rootView.setContent {
            AppTheme {
                ApplyScreenWindowData()
                content.invoke()
            }
        }
    }

    fun <State, Effects> subscribeState(
        vm: BaseViewModel<State, Effects>,
        stateConsumer: (State) -> Unit,
        effectsConsumer: (Set<Effects>) -> Unit,
    ) {
        vm.stateResult.observe(this, { resultEvent ->
            val stateResult = resultEvent.getIfNotHandled()
            stateConsumer.invoke(stateResult.first)
            effectsConsumer.invoke(stateResult.second)
        })
        addLifeCycleObserver(
            onCreate = { vm.onCreate(this) },
            onResume = { vm.onResume(this) },
            onStart = { vm.onStart(this) },
            onPause = { vm.onPause(this) },
            onStop = { vm.onStop(this) },
            onDestroy = { vm.onDestroy(this) },
        )
    }

    protected fun handleBaseEffects(data: BaseEffectsData) {
        when (data) {
            is BaseEffectsData.Toast -> Toast(data.text)
            is BaseEffectsData.NavigateTo -> Toast("Will navitage somewhere!!!1")
        }
    }
}
