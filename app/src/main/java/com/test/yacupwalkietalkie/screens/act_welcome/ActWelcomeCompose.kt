package com.test.yacupwalkietalkie.screens.act_welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.yacupwalkietalkie.R
import com.test.yacupwalkietalkie.ui.common.ScreenState
import com.test.yacupwalkietalkie.ui.common.alignStart
import com.test.yacupwalkietalkie.ui.common.secondaryTextColor
import com.test.yacupwalkietalkie.ui.common.subviews.AppSpacer
import com.test.yacupwalkietalkie.ui.common.subviews.ButtonGreen
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme

@Composable
fun ActWelcomeCompose(
    state: ActWelcomeVm.State,
    onBottomButtonClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.white)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_bears),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp),
            contentDescription = "img_bears",
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0f to AppTheme.color.transparent,
                        0.5f to AppTheme.color.white,
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(10f)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = AppTheme.dimens.x6)
                    .weight(6f)
            ) {
                val showSimpleScreen = state.screenState == ScreenState.SUCCESS
                val titleText = if (showSimpleScreen) {
                    stringResource(R.string.welcome)
                } else {
                    stringResource(R.string.oy)
                }
                Text(
                    text = titleText,
                    style = AppTheme.typography.BoldXxl,
                )
                AppSpacer(height = AppTheme.dimens.x3)
                val subtitleText = if (showSimpleScreen) {
                    stringResource(R.string.welcome_to_kamchatka)
                } else {
                    stringResource(R.string.bears_are_angry)
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = subtitleText,
                    style = AppTheme.typography.RegM
                        .alignStart()
                        .secondaryTextColor(),
                )
                AppSpacer(height = AppTheme.dimens.x12)
                val buttonText = if (showSimpleScreen) {
                    stringResource(R.string.provide)
                } else {
                    stringResource(R.string.to_settings)
                }
                ButtonGreen(
                    modifier = Modifier.fillMaxWidth(),
                    text = buttonText,
                    onClick = onBottomButtonClicked
                )
            }
        }

    }

}

@Composable
@Preview
private fun Preview() {
    ActWelcomeCompose(
        state = ActWelcomeVm.State(
            screenState = ScreenState.ERROR,
            navigatedToSettings = false
        ),
        onBottomButtonClicked = {}
    )
}
