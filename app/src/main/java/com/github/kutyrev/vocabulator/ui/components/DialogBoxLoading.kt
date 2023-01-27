package com.github.kutyrev.vocabulator.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import com.github.kutyrev.vocabulator.R

private const val PROGRESS_INDICATOR_COLOR = 0xFF35898f
private const val ANIMATION_DURATION = 600
private const val ANIMATION_INITIAL_VALUE = 0f
private const val ANIMATION_TARGET_VALUE = 360f
private const val PROGRESS_STEP = 1f
private const val PROGRESS_IND_ALPHA = 0.1f

@Composable
fun DialogBoxLoading(
    cornerRadius: Dp = dimensionResource(id = R.dimen.corner_radius_std),
    paddingStart: Dp = dimensionResource(id = R.dimen.padding_long),
    paddingEnd: Dp =  dimensionResource(id = R.dimen.padding_long),
    paddingTop: Dp = dimensionResource(id = R.dimen.padding_prolonged),
    paddingBottom: Dp = dimensionResource(id = R.dimen.padding_prolonged),
    progressIndicatorColor: Color = Color(PROGRESS_INDICATOR_COLOR),
    progressIndicatorSize: Dp = dimensionResource(id = R.dimen.progress_ind_size)
) {
    Dialog(
        onDismissRequest = {
        }
    ) {
        Surface(
            elevation = dimensionResource(id = R.dimen.elevation_std),
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = paddingStart, end = paddingEnd, top = paddingTop),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProgressIndicatorLoading(
                    progressIndicatorSize = progressIndicatorSize,
                    progressIndicatorColor = progressIndicatorColor
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dialog_spacer_height)))

                Text(
                    modifier = Modifier
                        .padding(bottom = paddingBottom),
                    text = stringResource(R.string.loading_message),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
fun ProgressIndicatorLoading(progressIndicatorSize: Dp, progressIndicatorColor: Color) {

    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = ANIMATION_INITIAL_VALUE,
        targetValue = ANIMATION_TARGET_VALUE,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = ANIMATION_DURATION
            }
        )
    )

    CircularProgressIndicator(
        progress = PROGRESS_STEP,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                dimensionResource(id = R.dimen.border_size_std),
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = PROGRESS_IND_ALPHA),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = dimensionResource(id = R.dimen.progress_ind_stroke),
        color = Color.White // Set background color
    )
}
