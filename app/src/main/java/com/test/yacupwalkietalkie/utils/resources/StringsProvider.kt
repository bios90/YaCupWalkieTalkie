package com.test.yacupwalkietalkie.utils.resources

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

class StringsProvider(private val context: Context) {
    fun get(@StringRes id: Int) = context.getString(id)
}
