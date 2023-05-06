package com.github.kutyrev.vocabulator.features.tutorial

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.github.kutyrev.vocabulator.R
import kotlin.math.absoluteValue

private const val PAGE_COUNT = 4

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialScreen(onSettingsMenuItemClick: () -> Unit) {
    val pagerState = rememberPagerState()
    val titleTextRes = rememberSaveable {
        mutableStateOf(R.string.help_label_1)
    }
    val mainTextRes = rememberSaveable {
        mutableStateOf(R.string.help_text_1)
    }
    val imageRes = rememberSaveable {
        mutableStateOf(R.drawable.tutorial_1)
    }

    val isShowToSettingsButton = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            when (page) {
                0 -> {
                    titleTextRes.value = R.string.app_name
                    mainTextRes.value = R.string.main_desc_text
                    imageRes.value = R.drawable.icon
                    isShowToSettingsButton.value = false
                }
                1 -> {
                    titleTextRes.value = R.string.help_label_1
                    mainTextRes.value = R.string.help_text_1
                    imageRes.value = R.drawable.tutorial_1
                    isShowToSettingsButton.value = false
                }
                2 -> {
                    titleTextRes.value = R.string.help_label_2
                    mainTextRes.value = R.string.help_text_2
                    imageRes.value = R.drawable.tutorial_2
                    isShowToSettingsButton.value = false
                }
                3 -> {
                    titleTextRes.value = R.string.help_label_3
                    mainTextRes.value = R.string.help_text_3
                    imageRes.value = R.drawable.tutorial_3
                    isShowToSettingsButton.value = true
                }
            }
        }
    }

    Surface() {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_std))
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                pageCount = PAGE_COUNT,
                state = pagerState
            ) { page ->
                Card(
                    Modifier
                        .padding(top = dimensionResource(id = R.dimen.padding_std))
                        .graphicsLayer {
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        },
                    elevation = 0.dp
                ) {
                    Column {
                        Column(horizontalAlignment = CenterHorizontally) {
                            Image(
                                painter = painterResource(id = imageRes.value),
                                contentDescription = stringResource(
                                    R.string.app_functions_description_desc
                                ),
                                modifier = Modifier
                                    .border(BorderStroke(1.dp, Color.LightGray))
                            )
                            Text(
                                text = stringResource(titleTextRes.value),
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text = stringResource(mainTextRes.value),
                            style = MaterialTheme.typography.subtitle2,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
                        if (isShowToSettingsButton.value) {
                            OutlinedButton(onClick = onSettingsMenuItemClick) {
                                Text(stringResource(R.string.tutorial_settings_button))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std_doubled)))
            Row(
                Modifier
                    .height(dimensionResource(id = R.dimen.pager_indicator_box_size))
                    .fillMaxWidth()
                    .align(CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(PAGE_COUNT) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.pager_indicator_item_padding))
                            .clip(CircleShape)
                            .background(color)
                            .size(dimensionResource(id = R.dimen.pager_indicator_item_size))
                    )
                }
            }
        }
    }
}
