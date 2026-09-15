package com.taskroulette.app.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate as drawRotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskroulette.app.model.TaskItem
import com.taskroulette.app.ui.theme.Baloo2
import com.taskroulette.app.ui.theme.TrColors
import kotlin.math.cos
import kotlin.math.sin

private const val VIEWBOX = 300f

private data class WheelSlice(val task: TaskItem, val startDeg: Float, val endDeg: Float, val fill: Color, val striped: Boolean)

private fun buildSlices(pool: List<TaskItem>): List<WheelSlice> {
    val n = pool.size
    if (n == 0) return emptyList()
    val sliceAngle = 360f / n
    return pool.mapIndexed { i, task ->
        WheelSlice(
            task = task,
            startDeg = i * sliceAngle,
            endDeg = (i + 1) * sliceAngle,
            fill = TrColors.wheelPalette[i % TrColors.wheelPalette.size],
            striped = i % 2 == 0,
        )
    }
}

/** Point on the wheel at angle [deg] measured clockwise from straight up, matching the web prototype. */
private fun pointAt(cx: Float, cy: Float, r: Float, deg: Float): Offset {
    val rad = Math.toRadians(deg.toDouble())
    return Offset((cx + r * sin(rad)).toFloat(), (cy - r * cos(rad)).toFloat())
}

private fun slicePath(cx: Float, cy: Float, r: Float, startDeg: Float, endDeg: Float): Path {
    return Path().apply {
        moveTo(cx, cy)
        val p0 = pointAt(cx, cy, r, startDeg)
        lineTo(p0.x, p0.y)
        arcTo(
            rect = Rect(Offset(cx - r, cy - r), Size(r * 2, r * 2)),
            startAngleDegrees = startDeg - 90f,
            sweepAngleDegrees = endDeg - startDeg,
            forceMoveTo = false,
        )
        close()
    }
}

@Composable
fun WheelView(
    pool: List<TaskItem>,
    rotationTarget: Float,
    spinning: Boolean,
    onSpin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec = if (spinning || rotationTarget != 0f) {
            tween(durationMillis = 3800, easing = CubicBezierEasing(0.15f, 0.65f, 0.15f, 1f))
        } else {
            tween(durationMillis = 0)
        },
        label = "wheelRotation",
    )

    Box(modifier = modifier.size(width = 270.dp, height = 340.dp), contentAlignment = Alignment.TopCenter) {

        BouncingStars(modifier = Modifier.align(Alignment.TopCenter).offset(y = 0.dp))

        Pointer(modifier = Modifier.align(Alignment.TopCenter).offset(y = 30.dp))

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 70.dp)
                .size(270.dp),
            contentAlignment = Alignment.Center,
        ) {
            val slices = buildSlices(pool)
            Canvas(
                modifier = Modifier
                    .size(270.dp)
                    .rotate(rotation),
            ) {
                val scale = size.minDimension / VIEWBOX
                val cx = VIEWBOX / 2f * scale
                val cy = VIEWBOX / 2f * scale
                val outerR = 148f * scale
                val innerR = 139f * scale
                val sliceR = 130f * scale
                val dashedR = 126f * scale
                val hubR = 30f * scale

                drawCircle(color = TrColors.wheelRim, radius = outerR, center = Offset(cx, cy))
                drawCircle(color = TrColors.wheelInnerRim, radius = innerR, center = Offset(cx, cy))

                slices.forEach { slice ->
                    val path = slicePath(cx, cy, sliceR, slice.startDeg, slice.endDeg)
                    drawPath(path, color = slice.fill)
                    drawPath(path, color = Color.White, style = Stroke(width = 2.5f * scale))

                    clipPath(path) {
                        if (slice.striped) {
                            drawStripes(cx, cy, sliceR, scale)
                        } else {
                            drawDots(cx, cy, sliceR, scale)
                        }
                    }
                }

                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = dashedR,
                    center = Offset(cx, cy),
                    style = Stroke(
                        width = 2f * scale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * scale, 7f * scale)),
                    ),
                )
                drawCircle(color = TrColors.wheelHub, radius = hubR, center = Offset(cx, cy))
                drawCircle(
                    color = TrColors.wheelRim,
                    radius = hubR,
                    center = Offset(cx, cy),
                    style = Stroke(width = 4f * scale),
                )
            }
        }

        SpinButton(
            spinning = spinning,
            onClick = onSpin,
            modifier = Modifier.align(Alignment.TopCenter).offset(y = 70.dp + (270.dp - 76.dp) / 2),
        )
    }
}

private fun DrawScope.drawStripes(cx: Float, cy: Float, r: Float, scale: Float) {
    drawRotate(degrees = 45f, pivot = Offset(cx, cy)) {
        val period = 16f * scale
        val bandWidth = 7f * scale
        var x = cx - r * 3f
        val endX = cx + r * 3f
        while (x < endX) {
            drawRect(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(x, cy - r * 3f),
                size = Size(bandWidth, r * 6f),
            )
            x += period
        }
    }
}

private fun DrawScope.drawDots(cx: Float, cy: Float, r: Float, scale: Float) {
    val spacing = 22f * scale
    val dotR = 3.4f * scale
    var y = cy - r * 1.5f
    val endY = cy + r * 1.5f
    while (y < endY) {
        var x = cx - r * 1.5f
        val endX = cx + r * 1.5f
        while (x < endX) {
            drawCircle(color = Color.White.copy(alpha = 0.4f), radius = dotR, center = Offset(x, y))
            x += spacing
        }
        y += spacing
    }
}

@Composable
private fun SpinButton(spinning: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "spinPulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(500, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulseScale",
    )
    Box(
        modifier = modifier
            .size(76.dp)
            .scale(if (spinning) pulse else 1f)
            .shadow(elevation = 8.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(TrColors.hotPinkLight, TrColors.hotPink, TrColors.hotPinkDeep),
                )
            )
            .border(4.dp, TrColors.wheelHub, CircleShape)
            .clickable(enabled = !spinning, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (spinning) "···" else "SPIN",
            color = Color(0xFFFFF5FA),
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
        )
    }
}

private val PinShape = RoundedCornerShape(topStartPercent = 50, topEndPercent = 50, bottomEndPercent = 50, bottomStartPercent = 0)

@Composable
private fun Pointer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(34.dp)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .rotate(-45f)
            .clip(PinShape)
            .background(Brush.linearGradient(listOf(TrColors.hotPinkLight, TrColors.hotPink)))
            .border(3.dp, Color(0xFFFFF5FA), PinShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .offset(x = 3.dp, y = 3.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.75f))
        )
    }
}

@Composable
private fun BouncingStars(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(width = 200.dp, height = 40.dp)) {
        BobbingStar(sizeSp = 22, periodMs = 2600, delayMs = 0, modifier = Modifier.align(Alignment.CenterStart).offset(x = 6.dp))
        BobbingStar(sizeSp = 26, periodMs = 2300, delayMs = 300, modifier = Modifier.align(Alignment.TopCenter))
        BobbingStar(sizeSp = 22, periodMs = 2800, delayMs = 600, modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-6).dp))
    }
}

@Composable
private fun BobbingStar(sizeSp: Int, periodMs: Int, delayMs: Int, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "starBob")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = periodMs
                0f at 0
                1f at periodMs / 2
                0f at periodMs
            },
            repeatMode = RepeatMode.Restart,
            initialStartOffset = androidx.compose.animation.core.StartOffset(delayMs),
        ),
        label = "starBobT",
    )
    Text(
        text = "★",
        fontSize = sizeSp.sp,
        color = TrColors.starGold,
        modifier = modifier
            .offset(y = (-8f * t).dp)
            .rotate(-4f + 8f * t),
    )
}

@Composable
fun TwinkleStar(sizeSp: Int, periodMs: Int, delayMs: Int, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "twinkle")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = periodMs
                0f at 0
                1f at periodMs / 2
                0f at periodMs
            },
            repeatMode = RepeatMode.Restart,
            initialStartOffset = androidx.compose.animation.core.StartOffset(delayMs),
        ),
        label = "twinkleT",
    )
    Text(
        text = "✦",
        fontSize = sizeSp.sp,
        color = TrColors.twinkle,
        modifier = modifier
            .scale(0.85f + 0.2f * t)
            .alpha(0.5f + 0.5f * t),
    )
}
