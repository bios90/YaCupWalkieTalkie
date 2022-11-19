package com.test.yacupwalkietalkie.base.di

import com.test.yacupwalkietalkie.base.di.BaseDiInjector.providePermissionsManager
import com.test.yacupwalkietalkie.base.di.BaseDiInjector.provideStringsProvider
import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcome
import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcomeVm

class ActWelcomeInjector(
    private val actWelcome: ActWelcome
) {
    fun injectVm(vm: ActWelcomeVm) {
        vm.permissionsManager = providePermissionsManager(actWelcome)
        vm.stringsProvider = provideStringsProvider(actWelcome)
    }
}
