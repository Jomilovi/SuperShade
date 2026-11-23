package com.supershade.ui.shade

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.supershade.haptics.LocalSuperHaptics
import com.supershade.haptics.SuperHaptics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/**
 * Finger-tracked horizontal swipe with release + fling physics:
 *  - 1:1 tracking while dragging, subtle tilt, rubber-band when a direction is disabled
 *  - fling velocity or distance past the threshold commits the dismissal, carrying the
 *    release velocity into the exit spring
 *  - otherwise springs back with a lively overshoot, again seeded with the release velocity
 *  - haptic tick when crossing the commit threshold in either direction
 */
@Stable
class FluidSwipeState internal constructor(
    internal val scope: CoroutineScope,
) {
    internal val offset = Animatable(0f)
    internal val dragMutex = Mutex()
    internal var settleJob: Job? = null
    internal var widthPx by mutableFloatStateOf(1f)

    /** Current horizontal translation in px (positive = to the right). */
    val offsetPx: Float get() = offset.value

    /** Distance after which a release commits the dismissal. */
    val thresholdPx: Float get() = widthPx * 0.28f

    /** 0..1 progress towards the commit threshold-ish range, used for background effects. */
    val progress: Float get() = (abs(offset.value) / (widthPx * 0.5f)).coerceIn(0f, 1f)

    val isAtRest: Boolean get() = abs(offset.value) < 4f
}

@Composable
fun rememberFluidSwipeState(key: Any?): FluidSwipeState {
    val scope = rememberCoroutineScope()
    return remember(key) { FluidSwipeState(scope) }
}

@Composable
fun FluidSwipeToDismiss(
    state: FluidSwipeState,
    onDismissed: (towardsEnd: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enableStartToEnd: Boolean = true,
    enableEndToStart: Boolean = true,
    backgroundContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val context = LocalContext.current
    val haptics: SuperHaptics = LocalSuperHaptics.current ?: remember(context) { SuperHaptics(context) }
    val density = LocalDensity.current
    val flingVelocityPx = with(density) { 900.dp.toPx() }
    val currentOnDismissed by rememberUpdatedState(onDismissed)
    val allowStart by rememberUpdatedState(enableStartToEnd)
    val allowEnd by rememberUpdatedState(enableEndToStart)
    var isAttached by remember { mutableStateOf(true) }
    DisposableEffect(state) {
        isAttached = true
        onDispose { isAttached = false }
    }

    LaunchedEffect(state) {
        snapshotFlow { abs(state.offset.value) > state.thresholdPx }
            .distinctUntilChanged()
            .collect { past -> if (past && !state.isAtRest) haptics.notificationDismissTick() }
    }

    fun allowed(dir: Float) = if (dir > 0f) allowStart else allowEnd

    val draggableState = rememberDraggableState { delta ->
        state.settleJob?.cancel()
        state.scope.launch {
            state.dragMutex.withLock {
                val cur = state.offset.value
                val dir = if (cur != 0f) sign(cur) else sign(delta)
                // Rubber-band when the direction is disabled.
                val effective = if (allowed(if (delta != 0f) sign(cur + delta) else dir)) delta else delta * 0.18f
                state.offset.snapTo(cur + effective)
            }
        }
    }

    Box(modifier = modifier) {
        Box(modifier = Modifier.matchParentSize()) { backgroundContent() }
        Box(
            modifier = Modifier
                .onSizeChanged { state.widthPx = it.width.toFloat().coerceAtLeast(1f) }
                .graphicsLayer {
                    translationX = state.offset.value
                    rotationZ = (state.offset.value / state.widthPx) * 3.5f
                }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStopped = { velocity ->
                        state.settleJob?.cancel()
                        state.settleJob = state.scope.launch {
                            state.dragMutex.withLock {
                                val o = state.offset.value
                                val dir = sign(o)
                                val flung = abs(velocity) > flingVelocityPx && sign(velocity) == dir && abs(o) > 8f
                                val commit = dir != 0f && allowed(dir) && (abs(o) > state.thresholdPx || flung)
                                if (commit) {
                                    haptics.notificationDismissCommit()
                                    state.offset.animateTo(
                                        dir * state.widthPx * 1.2f,
                                        spring(Spring.DampingRatioNoBouncy, 420f),
                                        initialVelocity = velocity,
                                    )
                                    currentOnDismissed(dir > 0f)
                                    // A surviving row indicates a rejected/failed removal, so restore it.
                                    delay(180L)
                                    if (isAttached) state.offset.animateTo(0f, spring(0.8f, 300f))
                                } else {
                                    state.offset.animateTo(
                                        0f,
                                        spring(0.62f, 450f),
                                        initialVelocity = velocity,
                                    )
                                }
                            }
                        }
                    },
                ),
        ) { content() }
    }
}
