package com.test.yacupwalkietalkie.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable


fun AppCompatActivity.addLifeCycleObserver(
    onCreate: (LifecycleOwner?) -> Unit = { },
    onStart: (LifecycleOwner?) -> Unit = { },
    onResume: (LifecycleOwner?) -> Unit = { },
    onPause: (LifecycleOwner?) -> Unit = { },
    onStop: (LifecycleOwner?) -> Unit = { },
    onDestroy: (LifecycleOwner?) -> Unit = { },
) = lifecycle.addObserver(
    object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) = onCreate.invoke(owner)
        override fun onStart(owner: LifecycleOwner) = onStart.invoke(owner)
        override fun onResume(owner: LifecycleOwner) = onResume.invoke(owner)
        override fun onPause(owner: LifecycleOwner) = onPause.invoke(owner)
        override fun onStop(owner: LifecycleOwner) = onStop.invoke(owner)
        override fun onDestroy(owner: LifecycleOwner) = onDestroy.invoke(owner)
    }
)

fun AppCompatActivity.makeActionDelayed(delayTime: Long, action: () -> Unit) {
    lifecycleScope.launch {
        delay(delayTime)
        action.invoke()
    }
}

fun Intent.putArgs(args: Serializable) {
    putExtra(Consts.ARGS, args)
}

fun <T : Serializable> Intent.getArgs(): T? {
    return getSerializableExtra(Consts.ARGS) as? T
}

fun <T : Serializable> AppCompatActivity.getArgs(): T? = intent?.getArgs()