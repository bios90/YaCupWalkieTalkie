package com.test.yacupwalkietalkie.screens.act_welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.test.yacupwalkietalkie.ui.common.subviews.AppSpacer
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme

@Composable
fun ActWelcomeCompose(
    state: ActWelcomeVm.State,
) {
    Column(modifier = Modifier.fillMaxSize()
        .background(AppTheme.color.white)
    ) {
        AppSpacer(height = AppTheme.dimens.x10)
        Text(
            style = AppTheme.typography.BoldL,
            text = state.title
        )
        AppSpacer(height = AppTheme.dimens.x10)
        Text(
            style = AppTheme.typography.RegS,
            text = state.subtitle
        )
    }
}

@Composable
@Preview
private fun Preview() {
    ActWelcomeCompose(
        state = ActWelcomeVm.State(
            title = "Title",
            subtitle = "aldskfnasl nkajfgnkjlsdfnkljndsfg"
        )
    )
}