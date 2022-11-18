package com.test.yacupwalkietalkie.base.view_models

import com.test.yacupwalkietalkie.base.BaseActivity
import java.io.Serializable

sealed class BaseEffectsData {
    data class NavigateTo(
        val clazz: Class<out BaseActivity>,
        val args: Serializable? = null,
        val finishCurrent: Boolean = false,
        val finishAllPrevious: Boolean = false
    ) : BaseEffectsData()

    data class Toast(val text: String) : BaseEffectsData()
    object Finish : BaseEffectsData()
}
