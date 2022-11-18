package com.test.yacupwalkietalkie.ui.common.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.ui.common.debounceClickable
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme

@Composable
fun ItemDevice(
    modifier: Modifier = Modifier,
    device: ModelDevice,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(AppTheme.dimens.x2)
    Column(
        modifier = modifier
            .shadow(
                elevation = AppTheme.dimens.x2,
                shape = shape,
            )
            .clip(shape)
            .background(AppTheme.color.white)
            .fillMaxWidth()
            .debounceClickable(
                onClick = onClick
            )
            .padding(horizontal = AppTheme.dimens.x4, vertical = AppTheme.dimens.x3),
    ) {
        Text(
            style = AppTheme.typography.BoldL,
            text = device.name
        )
        AppSpacer(height = AppTheme.dimens.x2)
        Text(
            style = AppTheme.typography.RegS,
            text = device.macAddress
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ItemDevice(
        device = ModelDevice(
            "00:00:00:00",
            "DeviceName"
        ),
        onClick = {}
    )
}
