package com.turtlepaw.sunlight.presentation.pages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.turtlepaw.sunlight.R
import com.turtlepaw.sunlight.presentation.Routes
import com.turtlepaw.sunlight.presentation.components.ItemsListWithModifier
import com.turtlepaw.sunlight.presentation.theme.SleepTheme
import com.turtlepaw.sunlight.utils.Settings
import kotlinx.coroutines.launch
import kotlin.math.abs


@OptIn(ExperimentalHorologistApi::class, ExperimentalWearFoundationApi::class)
@Composable
fun WearHome(
    navigate: (route: String) -> Unit,
    goal: Int,
    today: Int,
    sunlightLx: Float,
    threshold: Int
) {
    SleepTheme {
        val focusRequester = rememberActiveFocusRequester()
        val coroutineScope = rememberCoroutineScope()
        val scalingLazyListState = rememberScalingLazyListState()
        val animatedGoal = remember { Animatable(0f) }

        LaunchedEffect(true) {
            coroutineScope.launch {
                animatedGoal.animateTo(
                    targetValue = goal.toFloat(),
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
            TimeText(
                modifier = Modifier.scrollAway(scalingLazyListState)
            )
            PositionIndicator(
                scalingLazyListState = scalingLazyListState
            )
            ItemsListWithModifier(
                modifier = Modifier
                    .rotaryWithScroll(
                        reverseDirection = false,
                        focusRequester = focusRequester,
                        scrollableState = scalingLazyListState,
                    ),
                scrollableState = scalingLazyListState,
                verticalAlignment = Arrangement.Top,
            ) {
                item {
                    val size = 95.dp
                    val iconSize = 30.dp
                    Box(
                        modifier = Modifier
                            .size(size)
//                            .padding(
//                                bottom = 8.dp,
//                            )
                            .padding(
                                top = 35.dp,
                            )
                    ) {
                        CircularProgressIndicator(
                            trackColor = MaterialTheme.colors.surface,
                            progress = animateFloatAsState(
                                targetValue = animatedGoal.value / goal,
                                label = "GoalProgress"
                            ).value, // Adjust this value to change the progress
                            modifier = Modifier
                                .size(size)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.sunlight_gold),
                            contentDescription = "sunlight",
                            modifier = Modifier
                                .size(iconSize)
                                .align(Alignment.Center)
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(5.dp))
                }
                item {
                    if(sunlightLx >= threshold){
                        Text(
                            text = "Earning minutes!",
                            modifier = Modifier.padding(bottom = 4.dp),
                            color = MaterialTheme.colors.primary
                        )
                    } else {
                        Text(
                            text = "Sunlight today",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                item {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 36.sp)) {
                                append("$today")
                            }
                            append("m")
//                            withStyle(style = SpanStyle(fontSize = 36.sp)) {
//                                append("${timeDifference.minutes}")
//                            }
//                            append("min")
                        },
                        color = MaterialTheme.colors.primary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(3.dp)
                    )
                }
                item {
                    Text(
                        text = if(today >= goal)
                            "You've reached your goal"
                        else if(today == 0)
                            "Wear your watch in the sun to earn minutes"
                        else
                            "${abs(today - goal)}m left to your goal",
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Spacer(modifier = Modifier.padding(vertical = 5.dp))
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 5.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
                    ) {
//                        Button(
//                            onClick = {
//                                navigate(
//                                    Routes.HISTORY.getRoute()
//                                )
//                            },
//                            colors = ButtonDefaults.secondaryButtonColors(),
//                            modifier = Modifier
//                                .size(ButtonDefaults.DefaultButtonSize)
//                                //.wrapContentSize(align = Alignment.Center)
//                        ) {
//                            // Icon for history button
//                            Icon(
//                                painter = painterResource(id = R.drawable.history),
//                                contentDescription = "History",
//                                tint = MaterialTheme.colors.primary,
//                                modifier = Modifier
//                                    .padding(2.dp)
//                            )
//                        }

                        Button(
                            onClick = {
                                navigate(
                                    Routes.SETTINGS.getRoute()
                                )
                            },
                            colors = ButtonDefaults.secondaryButtonColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                //.wrapContentSize(align = Alignment.Center)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(2.dp),
                                verticalAlignment = Alignment.CenterVertically,

                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.settings),
                                    tint = MaterialTheme.colors.primary,
                                    contentDescription = "Settings",
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = "Settings",
                                    color = MaterialTheme.colors.primary,
                                    style = MaterialTheme.typography.title3
                                )
                            }
                        }
                    }

                }
//                item {
//                    Text(
//                        text = "Made with ☀️ by turtlepaw",
//                        color = Color.White,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .padding(
//                                top = 10.dp
//                            )
//                    )
//                }
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearHome(
        navigate = {},
        Settings.GOAL.getDefaultAsInt(),
        30,
        5000f,
        Settings.SUN_THRESHOLD.getDefaultAsInt()
    )
}