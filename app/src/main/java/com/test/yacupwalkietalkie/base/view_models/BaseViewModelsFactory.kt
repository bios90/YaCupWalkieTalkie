@file:Suppress("UNCHECKED_CAST")

package com.test.yacupwalkietalkie.base.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.yacupwalkietalkie.base.App
import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcomeVm
import java.io.Serializable

class BaseViewModelsFactory<V : ViewModel, A : Serializable>(
    val viewModelClass: Class<V>,
    val args: A? = null,
) : ViewModelProvider.Factory {


    override fun <V : ViewModel> create(modelClass: Class<V>): V {
        return when (viewModelClass) {
            ActWelcomeVm::class.java -> ActWelcomeVm()
            else -> throw IllegalStateException("Trying to create unknown ViewModel")
        } as V
    }
}

inline fun <reified V : ViewModel> createViewModelFactory(): BaseViewModelsFactory<V, Serializable> =
    createViewModelFactory(args = null)

inline fun <reified V : ViewModel, A : Serializable> createViewModelFactory(args: A? = null): BaseViewModelsFactory<V, A> =
    BaseViewModelsFactory(
        viewModelClass = V::class.java,
        args = args
    )
