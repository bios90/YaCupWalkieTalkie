package com.test.yacupwalkietalkie.base.view_models

import com.test.yacupwalkietalkie.base.BaseActivity
import java.io.Serializable

sealed class BaseEffectsData {
    data class NavigateTo(
        val clazz: Class<BaseActivity>,
        val args: Serializable,
        val finishCurrent: Boolean
    ) : BaseEffectsData()

    data class Toast(val text: String) : BaseEffectsData()
}
