package com.test.yacupwalkietalkie.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
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
        vm.state.observe(this, Observer {
            stateConsumer.invoke(it.first)
            effectsConsumer.invoke(it.second)
        })
        addLifeCycleObserver(
            onCreate = { vm.onCreate() },
            onResume = { vm.onResume() },
            onStart = { vm.onStart() },
            onPause = { vm.onPause() },
            onStop = { vm.onStop() },
            onDestroy = { vm.onDestroy() },
        )
    }
}
