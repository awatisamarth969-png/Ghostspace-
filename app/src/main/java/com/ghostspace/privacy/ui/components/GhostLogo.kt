package com.ghostspace.privacy.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GhostLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ghost_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Glowing Background Aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x66A855F7), Color.Transparent),
                radius = w * 0.65f
            ),
            radius = w * 0.55f
        )

        // Ghost Body Path
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.5f + offsetY)
            cubicTo(
                w * 0.2f, h * 0.15f + offsetY,
                w * 0.8f, h * 0.15f + offsetY,
                w * 0.8f, h * 0.5f + offsetY
            )
            lineTo(w * 0.8f, h * 0.8f + offsetY)
            // Ripple bottom waves
            cubicTo(
                w * 0.72f, h * 0.75f + offsetY,
                w * 0.65f, h * 0.85f + offsetY,
                w * 0.55f, h * 0.78f + offsetY
            )
            cubicTo(
                w * 0.45f, h * 0.86f + offsetY,
                w * 0.35f, h * 0.75f + offsetY,
                w * 0.2f, h * 0.8f + offsetY
            )
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE9D5FF), Color(0xFFA855F7))
            ),
            style = Fill
        )

        // Expressive Eyes
        drawCircle(
            color = Color(0xFF0F091F),
            radius = w * 0.055f,
            center = androidx.compose.ui.geometry.Offset(w * 0.40f, h * 0.42f + offsetY)
        )
        drawCircle(
            color = Color(0xFF0F091F),
            radius = w * 0.055f,
            center = androidx.compose.ui.geometry.Offset(w * 0.60f, h * 0.42f + offsetY)
        )
    }
}
