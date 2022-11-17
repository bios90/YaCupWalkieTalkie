package com.test.yacupwalkietalkie.ui.common.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppSpacer(
    height: Dp,
    background: Color = Color.Transparent,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    fillMaxWidth: Boolean = true,
) = Spacer(
    modifier = Modifier
        .fillMaxWidth()
        .padding(paddingValues)
        .background(background)
        .apply {
            if (fillMaxWidth) {
                then(fillMaxWidth())
            }
        }
        .height(height)
)
