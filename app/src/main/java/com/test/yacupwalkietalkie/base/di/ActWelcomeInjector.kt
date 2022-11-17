package com.test.yacupwalkietalkie.base.di

import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcome
import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcomeVm

class ActWelcomeInjector(
    actWelcome: ActWelcome
) : BaseDiInjector(actWelcome) {

    fun injectVm(vm: ActWelcomeVm) {
        vm.permissionManager = providePermissionsManager()
        vm.stringsProvider = provideStringsProvider()
    }
}
