package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPillBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * 3.4 Minimal UI Overlay.
 * Top-left status pill ("Passthrough Active") and top-right settings gear placeholder.
 * Non-intrusive to preserve VR immersion inside a headset / Cardboard viewer.
 */
@Composable
fun StatusOverlay(
    modifier: Modifier = Modifier,
    virtualPlaneOpacity: Float = 0.85f,
    onOpacityChange: (Float) -> Unit = {}
) {
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Pulsing animation for the active camera indicator dot
    val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Top-Left: Small status text "Passthrough Active" (white, small, semi-transparent background pill)
        Row(
            modifier = Modifier
                .background(
                    color = StatusPillBg,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 0.8.dp,
                    color = Color.White.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("status_pill"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing live indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(pulseScale)
                    .background(color = StatusGreen, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Passthrough Active",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }

        // Top-Right: Settings gear icon (placeholder)
        IconButton(
            onClick = { showSettingsDialog = true },
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = StatusPillBg,
                    shape = CircleShape
                )
                .border(
                    width = 0.8.dp,
                    color = Color.White.copy(alpha = 0.18f),
                    shape = CircleShape
                )
                .testTag("settings_gear_button")
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextPrimary.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sayyed Studios VR",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "VR Passthrough Prototype v1.0\nCreated by Sayyed Yuzasif Abbas",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Virtual Plane Opacity: ${(virtualPlaneOpacity * 100).toInt()}%",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Slider(
                        value = virtualPlaneOpacity,
                        onValueChange = onOpacityChange,
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("plane_opacity_slider")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Future Phases:\n• Stereoscopic Split-Screen (VR Box/Cardboard)\n• Gyroscope Head Tracking\n• YouTube Virtual Cinema on 3D Plane\n• Voice Commands",
                        color = TextSecondary.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showSettingsDialog = false },
                    modifier = Modifier.testTag("settings_close_button")
                ) {
                    Text("Close", color = CyanAccent, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
