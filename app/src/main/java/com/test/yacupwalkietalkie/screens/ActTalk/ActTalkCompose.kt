package com.test.yacupwalkietalkie.screens.ActTalk

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.yacupwalkietalkie.R
import com.test.yacupwalkietalkie.ui.common.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActTalkCompose(
    state: ActTalkVm.State,
    onButtonDown: () -> Unit,
    onButtonUp: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val shape = RoundedCornerShape(50)
        val buttonColor = when {
            state.isRecording -> AppTheme.color.red
            state.canRecord.not() -> AppTheme.color.gray1
            else -> AppTheme.color.green
        }
        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = AppTheme.dimens.x10)
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
    }
}

@Preview
@Composable
private fun Preview() {
    ActTalkCompose(
        state = ActTalkVm.State(
            canRecord = true,
            isRecording = false
        ),
        onButtonDown = {},
        onButtonUp = {}
    )
}
