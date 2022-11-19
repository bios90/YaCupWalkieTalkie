package com.test.yacupwalkietalkie.screens.ActTalk

import android.view.MotionEvent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.test.yacupwalkietalkie.R
import com.test.yacupwalkietalkie.data.ModelLocation
import com.test.yacupwalkietalkie.ui.common.alignStart
import com.test.yacupwalkietalkie.ui.common.secondaryTextColor
import com.test.yacupwalkietalkie.ui.common.subviews.AppSpacer
import com.test.yacupwalkietalkie.ui.common.subviews.ButtonGray
import com.test.yacupwalkietalkie.ui.common.subviews.ButtonGreen
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActTalkCompose(
    state: ActTalkVm.State,
    onButtonDown: () -> Unit,
    onButtonUp: () -> Unit,
    onRationModeToggled: (Boolean) -> Unit,
    onDismissClicked: () -> Unit,
    onStayMoreClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.white)
    ) {
        val shape = RoundedCornerShape(50)
        var buttonColor by remember {
            mutableStateOf(AppTheme.color.green)
        }
        buttonColor = when {
            state.isRecording -> AppTheme.color.red
            state.isCompanionSpeaking -> AppTheme.color.gray2
            else -> AppTheme.color.green
        }
        var openDialog by remember { mutableStateOf(false) }
        openDialog = state.showDismissDialog

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimens.x6),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppSpacer(height = AppTheme.dimens.x10)
            Row(
                modifier = Modifier.height(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(10f),
                    text = "Собеседник: ${state.companionLocationData?.userName ?: ""}"
                )
                if (state.isCompanionSpeaking) {
                    Image(
                        modifier = Modifier.width(60.dp),
                        painter = painterResource(id = R.drawable.ic_live_record),
                        contentDescription = "img_live_record"
                    )
                }
            }

            AppSpacer(height = AppTheme.dimens.x10)
            val (lastRotation, setLastRotation) = remember { mutableStateOf(0) }
            var newRotation = lastRotation
            val modLast = if (lastRotation > 0) lastRotation % 360 else 360 - (-lastRotation % 360)
            val stateRotation = state.compassRotation.toInt()
            if (modLast != stateRotation) {
                val backward =
                    if (stateRotation > modLast) modLast + 360 - stateRotation else modLast - stateRotation // distance in degrees between modLast and rotation going backward
                val forward =
                    if (stateRotation > modLast) stateRotation - modLast else 360 - modLast + stateRotation // distance in degrees between modLast and rotation going forward

                newRotation = if (backward < forward) {
                    lastRotation - backward
                } else {
                    lastRotation + forward
                }
                setLastRotation(newRotation)
            }
            val angle: Float by animateFloatAsState(
                targetValue = newRotation.toFloat(),
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )

            Image(
                modifier = Modifier
                    .size(120.dp)
                    .rotate(angle),
                painter = painterResource(id = R.drawable.ic_cup_arrow),
                contentDescription = "img_compass",
            )
            AppSpacer(height = AppTheme.dimens.x10)
            val distance = state.distance?.toInt() ?: 0
            val distanceText = buildString {
                append(distance)
                append(" ")
                append(pluralStringResource(id = R.plurals.meters, count = distance))
            }
            Text(
                style = AppTheme.typography.BoldL,
                modifier = Modifier.fillMaxWidth(),
                text = distanceText
            )
        }
        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 122.dp)
                .size(120.dp)
                .clip(shape)
                .shadow(
                    elevation = AppTheme.dimens.x1,
                    shape = shape
                )
                .background(
                    color = buttonColor,
                    shape = shape
                )
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            onButtonDown.invoke()
                            true
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            onButtonUp.invoke()
                            true
                        }
                        else -> false
                    }
                }
                .padding(AppTheme.dimens.x4),
            colorFilter = ColorFilter.tint(AppTheme.color.white),
            painter = painterResource(id = R.drawable.ic_mic),
            contentDescription = "microphone_button"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimens.x6)
                .padding(bottom = AppTheme.dimens.x6)
                .align(Alignment.BottomCenter)
        ) {
            Column(modifier = Modifier.weight(10f)) {
                Text(
                    style = AppTheme.typography.BoldL.alignStart(),
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.ratio_mode)
                )
                Text(
                    style = AppTheme.typography.RegS.secondaryTextColor().alignStart(),
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.ratio_mode_explanation)
                )
            }
            Switch(
                checked = state.isRationMode,
                onCheckedChange = onRationModeToggled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppTheme.color.green,
                    checkedTrackColor = AppTheme.color.green.copy(alpha = 0.7f),
                )
            )
        }


        if (openDialog) {
            DismissAlert(
                onExitClicked = onDismissClicked,
                onStayMoreClicked = onStayMoreClicked
            )
        }
    }
}

@Composable
fun DismissAlert(
    onExitClicked: () -> Unit,
    onStayMoreClicked: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                style = AppTheme.typography.BoldL,
                text = stringResource(R.string.exit)
            )
        },
        text = {
            Text(
                style = AppTheme.typography.RegM.secondaryTextColor().alignStart(),
                text = stringResource(R.string.better_to_be_in_silence)
            )
        },

        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        confirmButton = {
            ButtonGreen(
                modifier = Modifier
                    .padding(bottom = AppTheme.dimens.x6)
                    .padding(end = AppTheme.dimens.x3),
                text = stringResource(R.string.make_exit),
                onClick = onExitClicked
            )
        },
        dismissButton = {
            ButtonGray(
                modifier = Modifier.padding(bottom = AppTheme.dimens.x6),
                text = stringResource(R.string.listen_more),
                onClick = onStayMoreClicked
            )
        },
        onDismissRequest = {},
    )
}

@Preview
@Composable
private fun Preview() {
    ActTalkCompose(
        state = ActTalkVm.State(
            isCompanionSpeaking = true,
            isRecording = false,
            distance = 100f,
            userLocation = null,
            companionLocationData = ModelLocation(
                userName = "Vasya",
                lat = 100.0,
                lon = 200.0
            ),
            compassRotation = 20f,
            isRationMode = true,
            showDismissDialog = true
        ),
        onButtonDown = {},
        onButtonUp = {},
        onRationModeToggled = {},
        onDismissClicked = {},
        onStayMoreClicked = {}
    )
}

