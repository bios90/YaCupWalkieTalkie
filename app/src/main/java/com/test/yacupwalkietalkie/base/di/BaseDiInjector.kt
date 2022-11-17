package com.test.yacupwalkietalkie.base.di

import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.utils.permissions.PermissionManager
import com.test.yacupwalkietalkie.utils.resources.StringsProvider

open class BaseDiInjector(private val act: BaseActivity) {

    fun providePermissionsManager() = PermissionManager(act)
    fun provideStringsProvider() = StringsProvider(act)
}
