package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VirtualPlaneBg

/**
 * 3.3 Foreground Virtual Plane.
 * Renders a dark gray rectangle (16:9 aspect ratio) floating in the center of the screen,
 * representing the future VR virtual theater screen (~3 meters away in virtual space).
 *
 * Characteristics:
 * - 80% screen width
 * - 16:9 aspect ratio
 * - Semi-transparent (~85% opacity) so the camera passthrough remains visible behind it
 * - Floating depth: slight drop shadow, thin border, HUD reticle & corner markers drawn via Canvas
 */
@Composable
fun VirtualScreenPlane(
    modifier: Modifier = Modifier,
    opacity: Float = 0.85f
) {
    // Subtle breathing glow animation representing active virtual display standby
    val infiniteTransition = rememberInfiniteTransition(label = "plane_glow_transition")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Calculate 80% of width for 16:9, clamping to bounds if height overflows in portrait/landscape
        val targetWidthByConstraint = maxWidth * 0.80f
        val calculatedHeight = targetWidthByConstraint * (9f / 16f)

        val (planeWidth, planeHeight) = if (calculatedHeight > maxHeight * 0.85f) {
            val h = maxHeight * 0.85f
            val w = h * (16f / 9f)
            Pair(w, h)
        } else {
            Pair(targetWidthByConstraint, calculatedHeight)
        }

        val shape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .width(planeWidth)
                .height(planeHeight)
                .shadow(
                    elevation = 16.dp,
                    shape = shape,
                    spotColor = Color.Black.copy(alpha = 0.8f),
                    ambientColor = CyanAccent.copy(alpha = 0.15f)
                )
                .clip(shape)
                .background(
                    VirtualPlaneBg.copy(alpha = opacity)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = glowAlpha),
                            CyanAccent.copy(alpha = glowAlpha * 0.8f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = shape
                )
                .testTag("virtual_screen_plane"),
            contentAlignment = Alignment.Center
        ) {
            // HUD Canvas Layer: Corner registration brackets, center crosshair, and depth guides
            Canvas(modifier = Modifier.fillMaxSize()) {
                val bracketLen = 22.dp.toPx()
                val bracketColor = Color.White.copy(alpha = 0.45f)
                val strokeW = 1.8f

                // Top-Left Corner Bracket
                drawLine(
                    color = bracketColor,
                    start = Offset(16.dp.toPx(), 16.dp.toPx()),
                    end = Offset(16.dp.toPx() + bracketLen, 16.dp.toPx()),
                    strokeWidth = strokeW
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(16.dp.toPx(), 16.dp.toPx()),
                    end = Offset(16.dp.toPx(), 16.dp.toPx() + bracketLen),
                    strokeWidth = strokeW
                )

                // Top-Right Corner Bracket
                drawLine(
                    color = bracketColor,
                    start = Offset(size.width - 16.dp.toPx(), 16.dp.toPx()),
                    end = Offset(size.width - 16.dp.toPx() - bracketLen, 16.dp.toPx()),
                    strokeWidth = strokeW
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(size.width - 16.dp.toPx(), 16.dp.toPx()),
                    end = Offset(size.width - 16.dp.toPx(), 16.dp.toPx() + bracketLen),
                    strokeWidth = strokeW
                )

                // Bottom-Left Corner Bracket
                drawLine(
                    color = bracketColor,
                    start = Offset(16.dp.toPx(), size.height - 16.dp.toPx()),
                    end = Offset(16.dp.toPx() + bracketLen, size.height - 16.dp.toPx()),
                    strokeWidth = strokeW
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(16.dp.toPx(), size.height - 16.dp.toPx()),
                    end = Offset(16.dp.toPx(), size.height - 16.dp.toPx() - bracketLen),
                    strokeWidth = strokeW
                )

                // Bottom-Right Corner Bracket
                drawLine(
                    color = bracketColor,
                    start = Offset(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                    end = Offset(size.width - 16.dp.toPx() - bracketLen, size.height - 16.dp.toPx()),
                    strokeWidth = strokeW
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                    end = Offset(size.width - 16.dp.toPx(), size.height - 16.dp.toPx() - bracketLen),
                    strokeWidth = strokeW
                )

                // Subtle dashed optical center crosshairs
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f), 0f)
                val crosshairLength = 18.dp.toPx()
                val centerX = size.width / 2f
                val centerY = size.height / 2f

                // Horizontal crosshair
                drawLine(
                    color = CyanAccent.copy(alpha = 0.35f),
                    start = Offset(centerX - crosshairLength, centerY),
                    end = Offset(centerX + crosshairLength, centerY),
                    strokeWidth = 1.2f,
                    pathEffect = dashEffect
                )
                // Vertical crosshair
                drawLine(
                    color = CyanAccent.copy(alpha = 0.35f),
                    start = Offset(centerX, centerY - crosshairLength),
                    end = Offset(centerX, centerY + crosshairLength),
                    strokeWidth = 1.2f,
                    pathEffect = dashEffect
                )
            }

            // Virtual Display Placeholder Content
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .testTag("virtual_screen_content"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // TV / Theater Icon
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "Virtual Theater Display",
                    tint = CyanAccent.copy(alpha = 0.85f),
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "VIRTUAL THEATER SCREEN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "16:9 Cinema Aspect • ~3.0m Virtual Depth",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phase 2 indicator pill
                Row(
                    modifier = Modifier
                        .background(
                            color = CyanAccent.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 0.8.dp,
                            color = CyanAccent.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VR Display Placeholder",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}
