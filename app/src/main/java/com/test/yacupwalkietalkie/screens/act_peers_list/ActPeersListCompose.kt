package com.test.yacupwalkietalkie.screens.act_peers_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.test.yacupwalkietalkie.R
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.ui.common.alignStart
import com.test.yacupwalkietalkie.ui.common.navBarHeightCompose
import com.test.yacupwalkietalkie.ui.common.subviews.AppSpacer
import com.test.yacupwalkietalkie.ui.common.subviews.ButtonGreen
import com.test.yacupwalkietalkie.ui.common.subviews.ItemDevice
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme
import com.test.yacupwalkietalkie.ui.common.top

@Composable
fun ActPeersListCompose(
    state: ActPeersListVm.State,
    onDeviceClicked: (ModelDevice) -> Unit,
    onTurnWifiClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.white)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AppSpacer(height = AppTheme.dimens.x8)
            Text(
                modifier = Modifier.padding(AppTheme.dimens.x6),
                style = AppTheme.typography.RegL.alignStart(),
                text = stringResource(R.string.available_devices_here)
            )
            LazyColumn(content = {
                for (device in state.devices) {
                    item {
                        AppSpacer(height = AppTheme.dimens.x2)
                    }
                    item(
                        key = device.macAddress,
                    ) {
                        ItemDevice(
                            modifier = Modifier.padding(horizontal = AppTheme.dimens.x6),
                            device = device,
                            onClick = { onDeviceClicked.invoke(device) }
                        )
                    }
                }
                item {
                    AppSpacer(height = AppTheme.dimens.x8)
                }
            })
        }

        if (state.isWifiEnabled == false) {
            val shape = RoundedCornerShape(AppTheme.dimens.x6).top
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .shadow(
                        elevation = AppTheme.dimens.x2,
                        shape = shape,
                    )
                    .fillMaxWidth()
                    .clip(shape)
                    .background(AppTheme.color.white)
                    .padding(horizontal = AppTheme.dimens.x6)
            ) {
                AppSpacer(height = AppTheme.dimens.x6)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style = AppTheme.typography.RegM,
                    text = stringResource(R.string.widi_disabled_lets_try_turn_on)
                )
                AppSpacer(height = AppTheme.dimens.x2)
                ButtonGreen(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.turn_on),
                    onClick = onTurnWifiClicked
                )
                AppSpacer(height = AppTheme.dimens.x6)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ActPeersListCompose(
        state = ActPeersListVm.State(
            isWifiEnabled = false,
            connectedToDevice = false,
            devices = listOf(
                ModelDevice(
                    macAddress = "00:00:00:00:01",
                    name = "DeviceName1"
                ),
                ModelDevice(
                    macAddress = "00:00:00:00:02",
                    name = "DeviceName2"
                )
            )
        ),
        onDeviceClicked = {},
        onTurnWifiClicked = {}
    )
}

